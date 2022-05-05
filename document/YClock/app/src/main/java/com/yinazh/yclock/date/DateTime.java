package com.yinazh.yclock.date;

import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 农历日期工具类
 * @author yinazh
 **/
public class DateTime {

    private final Calendar calendar;

    private static DateTime INSTANCE;
    public static DateTime build(){
        if(INSTANCE == null){
            INSTANCE = new DateTime();
        }
        return INSTANCE;
    }
    private DateTime() {
        /**
         * Calendar会以当前时间作为初始化参数
         * 后续在获取时间信息的时候，都是返回初始化时的数据，并不会自动更新
         * 所以在获得即时数据时，需要先更新Calendar的时间信息，然后再获取
         * */
        calendar = Calendar.getInstance(DateConstants.TIME_ZONE);
    }

    /**
     * 阳历日期
     */
    public String getTime(long currentTime){
        return DateFormat.format(DateConstants.FORMAT_24_ARGS, currentTime).toString();
    }

    public String getDateFormat(){
        updateCurrentTime();
        return DateConstants.CHINESE_DATE_FORMAT.format(calendar.getTime());
    }

    public long getDate(){
        return System.currentTimeMillis();
    }

    public String getDateWithWeek(){
        updateCurrentTime();
        return getDateFormat() + "\n" + getWeek();
    }

    public String getDateStr(){
        updateCurrentTime();
        return DateConstants.DATE_CHECK_FORMAT.format(calendar.getTime());
    }

    private int getYear(){
        updateCurrentTime();
        return calendar.get(Calendar.YEAR);
    }

    private int getMonth(){
        updateCurrentTime();
        return calendar.get(Calendar.MONTH) + 1;
    }

    private int getDay(){
        updateCurrentTime();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String getWeek(){
        updateCurrentTime();
        return DateConstants.SOLAR.WEEKDAY[calendar.get(Calendar.DAY_OF_WEEK)];
    }

    /**
     * 即时更新当前 Calendar
     * */
    private void updateCurrentTime(){
        calendar.setTimeInMillis(System.currentTimeMillis());
    }

    /**
     * 农历日期
     * */
    /**
     * 计算农历年的总天数
     * 传回农历 year 年的总天数 1900--2100
     * */
    public int yearDays(int year) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((DateConstants.LUNAR.INFO[year - 1900] & i) != 0) {
                sum += 1;
            }
        }
        return (sum + leapDays(year));
    }

    /**
     * 传回农历year年闰月的天数
     */
    public  int leapDays(int year) {
        if(leapMonth(year) == 0){ return 0; }
        return (DateConstants.LUNAR.INFO[year - 1899] & 0xf) != 0 ? 30 : 29;
    }

    /**
     * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
     */
    public  int leapMonth(int year) {
        long var = DateConstants.LUNAR.INFO[year - 1900] & 0xf;
        return (int)(var == 0xf ? 0 : var);
    }


    /**
     * 传回农历 year年month月 的总天数
     */
    public int monthDays(int year, int month) {
        return (DateConstants.LUNAR.INFO[year - 1900] & (0x10000 >> month)) == 0 ? 29 : 30;
    }

    /**
     * 传回农历 year 年的生肖
     * */
    public String zodiacYear(int year) {
        return "『 " + DateConstants.LUNAR.ZODIAC_INFO[(year - 4) % 12] + " 』" + "年";

    }

    /**
     * 计算天干地支，传入月日的offset传回干支, 0=甲子
     * */
    final private static String cyclicalm(int num) {
        return (DateConstants.LUNAR.HEAVENLY_STEMS_INFO[num % 10] + DateConstants.LUNAR.EARTHLY_BRANCH_INFO[num % 12]);
    }

    /**
     * 传入 offset 传回干支, 0=甲子
     */
    final public String cyclical(int year, int month,int day) {
        int num = year - 1900 + 36;
        //立春日期
        int term2 = sTerm(year, 2);
        if(month>2 || (month==2 && day >= term2)){
            num = num +0;
        }else{
            num = num -1;
        }
        return cyclicalm(num);
    }

    /**
     * 将农历day日格式化成农历表示的字符串
     * */
    public static String getChinaDayString(int day) {
        if(day == 20){ return "二十"; }
        if(day == 30){ return DateConstants.LUNAR.CHINESE_TEN[(int)(day/10)]+"十"; }
        return DateConstants.LUNAR.CHINESE_TEN[(int)((day-1)/10)] + DateConstants.LUNAR.CHINESE_DAY[(int)((day-1)%10)];
    }

    /**
     * 计算公历nY年nM月nD日和bY年bM月bD日渐相差多少天
     * */
    public int getDaysOfTwoDate(int bYear,int bMonth, int bDday,int nYear, int nMonth,int nDday){
        try {
            Date baseDate = DateConstants.CHINESE_DATE_FORMAT.parse(bYear+"年"+bMonth+"月"+bDday+"日");
            Date nowaday = DateConstants.CHINESE_DATE_FORMAT.parse(nYear+"年"+nMonth+"月"+nDday+"日");
            // 求出相差的天数
            return (int) ((nowaday.getTime() - baseDate.getTime()) / 86400000L);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 农历lunYear年lunMonth月lunDay日
     * isLeap 当前年月是否是闰月
     * 从农历日期转换成公历日期
     * */
    public Calendar getSunDate(int lunYear, int lunMonth, int lunDay, boolean isLeap){
        //公历1900年1月31日为1900年正月初一
        int years = lunYear -1900;
        int days=0;
        for(int i=0;i<years;i++){
            //农历某年总天数
            days += yearDays(1900+i);
        }
        for(int i=1;i<lunMonth;i++){
            days += monthDays(lunYear,i);
        }
        if(leapMonth(lunYear)!=0 && lunMonth>leapMonth(lunYear)){
            //lunYear年闰月天数
            days += leapDays(lunYear);
        }
        if(isLeap){
            //lunYear年lunMonth月 闰月
            days +=monthDays(lunYear,lunMonth);
        }
        days += lunDay;
        days = days-1;
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.YEAR, 1900);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH,31);
        cal.add(Calendar.DATE, days);
        return cal;
    }

    public String getLunarString(){
        return getLunarString(getYear(), getMonth(), getDay());
    }

    /**
     * 得到农历月份
     * */
    public String getLunarString(int year, int month, int day){
        int var = getLunarDateInt(year,month,day);
        int lYear = (int)var/10000;
        int lMonth = (int)(var%10000)/100;
        int lDay = var - lYear*10000 - lMonth*100;
//        String lunY = cyclical(year,month,day)+"年";
        String lunY = cyclical(year,month,day);
        String lunM="";
        int testMonth = getSunDate(lYear,lMonth,lDay,false).get(Calendar.MONTH)+1;
        if(testMonth!=month){
            lunM = "闰";
        }
        lunM += DateConstants.LUNAR.CHINESE_NUMBER[(lMonth-1)%12]+"月";
        //int leap = leapMonth(lYear);
        String lunD = getChinaDayString(lDay);
        int animalsYear = 0;
        //立春
        int term2 = sTerm(year, 2);
        if(month>2 ||(month==2&& day>=term2)){
            animalsYear = year;
        }else{
            animalsYear = year-1;
        }
        String festival = getFestival(year,month,day);
        StringBuffer result = new StringBuffer();
        result.append(lunY).append(zodiacYear(animalsYear)).append("\n")
                .append(TextUtils.isEmpty(festival) ? "" : (festival + "\n"))
                .append(lunM).append(lunD);
        return result.toString();
    }

    /**
     * 将公历year年month月day日转换成农历
     * 返回格式为20140506（int）
     * */
    public int getLunarDateInt(int year, int month, int day){
        int iYear,LYear,LMonth,LDay, daysOfYear = 0;
        // 求出和1900年1月31日相差的天数
        int offset = getDaysOfTwoDate(1900,1,31,year,month,day);
        // 用offset减去每农历年的天数
        // 计算当天是农历第几天
        // i最终结果是农历的年份
        // offset是当年的第几天
        for (iYear = 1900; iYear < 2100 && offset >0; iYear++) {
            daysOfYear = yearDays(iYear);
            offset -= daysOfYear;
        }

        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
        }
        // 农历年份
        LYear = iYear;

        // 闰哪个月,1-12
        int leapMonth = leapMonth(iYear);
        boolean leap = false;

        // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        int iMonth=1, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset >0; iMonth++) {
            // 闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                --iMonth;
                leap = true;
                daysOfMonth = leapDays(iYear);
            } else{
                daysOfMonth = monthDays(iYear, iMonth);
            }
            // 解除闰月
            if (leap && iMonth == (leapMonth + 1)){ leap = false; }

            offset -= daysOfMonth;
        }
        // offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false;
            } else {
                leap = true;
                --iMonth;
            }
        }
        // offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
        }
        LMonth = iMonth;
        LDay = offset + 1;
        return LYear * 10000 + LMonth *100 + LDay;
    }

    /**
     * 获取公历对应的节假日或二十四节气
     */
    public String getFestival(int year, int month, int day){
        //农历节假日
        int var = getLunarDateInt(year, month, day);
        int lun_year = (int)var/10000;
        int lun_month = (int)(var%10000)/100;
        int lun_Day = (int)(var - lun_year*10000 - lun_month*100);
        for (int i = 0; i < DateConstants.LUNAR.HOLIDAY.length; i++) {
            String[] holidayInfo = DateConstants.LUNAR.HOLIDAY[i].split(" ");
            StringBuffer tempDay = new StringBuffer();
            tempDay.append(lun_month < 10 ? "0" : "").append(lun_month)
                    .append(lun_Day < 10 ? "0" : "").append(lun_Day);
            if (holidayInfo[0].trim().equals(tempDay.toString())) {
                return holidayInfo[1];
            }
        }

        //公历节假日
        for (int i = 0; i < DateConstants.SOLAR.HOLIDAY.length; i++) {
            //1893年前没有此节日
            if((i==DateConstants.SOLAR.HOLIDAY.length && year<1893)
                    || (i+3 == DateConstants.SOLAR.HOLIDAY.length && year<1999)
                    || (i+6 == DateConstants.SOLAR.HOLIDAY.length && year<1942)
                    || (i+10 == DateConstants.SOLAR.HOLIDAY.length && year<1949)
                    || (i == 19 && year<1921)
                    || (i == 20 && year<1933)
                    || (i == 22 && year<1976)){
                continue;
            }

            // 返回公历节假日名称
            String[] solarHolidayInfo = DateConstants.SOLAR.HOLIDAY[i].split(" ");
            StringBuffer tempDay = new StringBuffer();
            tempDay.append(month < 10 ? "0" : "").append(month)
                    .append(day < 10 ? "0" : "").append(day);
            if (solarHolidayInfo[0].trim().equals(tempDay.toString())) {
                return solarHolidayInfo[1];
            }
        }

        int b = getDateOfSolarTerms(year,month);
        if(day==(int)b/100){
            return DateConstants.LUNAR.SOLAR_TERMS_INFO[(month-1)*2];
        }else if(day== b%100){
            return DateConstants.LUNAR.SOLAR_TERMS_INFO[(month-1)*2+1];
        }
        return "";
    }
    /** */
    /**
     * 传出y年m月d日对应的农历. yearCyl3:农历年与1864的相差数 ? monCyl4:从1900年1月31日以来,闰月数
     * dayCyl5:与1900年1月31日相差的天数,再加40 ?
     *
     * isday: 这个参数为false---日期为节假日时，阴历日期就返回节假日 ，true---不管日期是否为节假日依然返回这天对应的阴历日期
     *
     * @param
     * @return
     */
    public String getLunarDate(int year_log, int month_log, int day_log,
                               boolean isday) {
        int var = getLunarDateInt(year_log,month_log,day_log);
        // 农历年份
        int year = (int)var/10000;

        //农历月份
        int month = (int)(var%10000)/100;
        // 设置对应的阴历月份
//        setLunarMonth(DateConstants.CHINESE_NUMBER[(month-1)%12] + "月");
        int day = var - year*10000 - month*100;
        if (!isday) {
            String Festival = getFestival(year_log,month_log,day_log);
            if(Festival.length()>1){
                return Festival;
            }
        }

        return DateConstants.LUNAR.CHINESE_NUMBER[(month-1)%12] + "月" + getChinaDayString(day);
    }

    /**
     * 星座计算
     * */
    public String getConstellation(int month, int day){
        int date = month * 100 + day;
        String constellation="";
        if(date>=120 && date <=218){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[1];
        }else if(date>=219 && date<=320){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[2];
        }else if(date>=321 && date<=419){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[3];
        }else if(date>=420 && date<=520){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[4];
        }else if(date>=521 && date<=620){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[5];
        }else if(date>=621 && date<=721){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[6];
        }else if(date>=722 && date<=822){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[7];
        }else if(date>=823 && date<=922){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[8];
        }else if(date>=923 && date<=1022){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[9];
        }else if(date>=1023 && date<=1121){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[10];
        }else if(date>=1122 && date<=1221){
            constellation = DateConstants.SOLAR.CONSTELLATIONS[11];
        }else{
            constellation = DateConstants.SOLAR.CONSTELLATIONS[0];
        }
        return constellation;
    }

    private int sTerm(int year, int n) {
        int[]  sTermInfo = new int[]{0,21208,42467,63836,85337,107014,
                128867,150921,173149,195551,218072,240693,
                263343,285989,308563,331033,353350,375494,
                397447,419210,440795,462224,483532,504758};
        Calendar cal = Calendar.getInstance();
        cal.set(1900, 0, 6, 2, 5, 0);
        long temp = cal.getTime().getTime();
        cal.setTime(new Date( (long) ((31556925974.7 * (year - 1900) + sTermInfo[n] * 60000L) + temp)));
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取节气
     * */
    public int getDateOfSolarTerms(int year, int month){
        return sTerm(year,(month-1)*2) * 100 + sTerm(year,(month-1)*2 +1);
    }

    /**
     * 计算宜忌
     * */
    public String jcrt(String d) {
        if(TextUtils.isEmpty(d)){ return "";}
        StringBuffer result = new StringBuffer();
        for(int i = 0 ; i < DateConstants.LUNAR.YIJI.length; i++){
            if(DateConstants.LUNAR.YIJI[i].equals(d)){
                result.append("宜:\t").
                        append(DateConstants.LUNAR.SUITABLE_INFO[i]).
                        append("-").append("忌:\t").
                        append(DateConstants.LUNAR.TABOO_INFO[i]);
                break;
            }
        }
        return result.toString();
    }

    /** num_y%12, num_m%12, num_d%12, num_y%10, num_d%10
     * m:农历月份 1---12
     * dt：农历日
     * */
    public String CalConv2(int yy, int mm, int dd, int y, int d, int m, int dt) {
        String dy = d + "" + dd;

        if ((yy == 0 && dd == 6) || (yy == 6 && dd == 0) || (yy == 1 && dd == 7) ||
                (yy == 7 && dd == 1) || (yy == 2 && dd == 8) || (yy == 8 && dd == 2) ||
                (yy == 3 && dd == 9) || (yy == 9 && dd == 3) || (yy == 4 && dd == 10) ||
                (yy == 10 && dd == 4) || (yy == 5 && dd == 11) || (yy == 11 && dd == 5)) {
            /*
             * 地支共有六对相冲，俗称“六冲”，即：子午相冲、丑未相冲、寅申相冲、卯酉相冲、辰戌相冲、巳亥相冲。
             * 如当年是甲子年，子为太岁，与子相冲者是午，假如当日是午日，则为岁破，其余的以此类推，即丑年的岁破为未日，
             * 寅年的岁破为申日，卯年的岁破为酉日，辰年的岁破为戌日，巳年的岁破为亥日，午年的岁破为子日，未年的岁破为丑日，
             * 申年的岁破为寅日，酉年的岁破为卯日，戌年的岁破为辰日，亥年的岁破为巳日。
             * */
            return "日值岁破 大事不宜";
        } else if ((mm == 0 && dd == 6) || (mm == 6 && dd == 0) || (mm == 1 && dd == 7) || (mm == 7 && dd == 1) ||
                (mm == 2 && dd == 8) || (mm == 8 && dd == 2) || (mm == 3 && dd == 9) || (mm == 9 && dd == 3) ||
                (mm == 4 && dd == 10) || (mm == 10 && dd == 4) || (mm == 5 && dd == 11) || (mm == 11 && dd == 5)) {
            return "日值月破 大事不宜";
        } else if ((y == 0 && "911".equals(dy)) || (y == 1 && "55".equals(dy)) || (y == 2 && "111".equals(dy)) ||
                (y == 3 && "75".equals(dy)) || (y == 4 && "311".equals(dy)) || (y == 5 && "95".equals(dy)) ||
                (y == 6 && "511".equals(dy))|| (y == 7 && "15".equals(dy)) || (y == 8 && "711".equals(dy)) || (y == 9 && "35".equals(dy))) {
            return "日值上朔 大事不宜";
        } else if ((m == 1 && dt == 13) || (m == 2 && dt == 11) || (m == 3 && dt == 9) || (m == 4 && dt == 7) ||
                (m == 5 && dt == 5) || (m == 6 && dt == 3) || (m == 7 && dt == 1) || (m == 7 && dt == 29) ||
                (m == 8 && dt == 27) || (m == 9 && dt == 25) || (m == 10 && dt == 23) || (m == 11 && dt == 21) ||
                (m == 12 && dt == 19)) {
            /*
             * 杨公十三忌   以农历正月十三日始，以后每月提前两天为百事禁忌日
             * */
            return "日值杨公十三忌 大事不宜";
            //}else if(var == getSiLi(m,dt)){
            //return "日值四离  大事勿用";
        } else {
            return null;
        }
    }

    /**
     * 返回公历year年month月的当月总天数
     * */
    public int getDaysOfMonth(int year, int month){
        switch(month){
            case 2:
                return ((year%4 == 0) && (year%100 != 0) || (year%400 == 0))? 29: 28;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            default:
                return 30;
        }
    }

    /** 输入
     * year 公历年份，大于1900
     * month 公历月份 1--12
     * 输出
     * ArrayList<String> year年month月全月宜忌
     * */
    public ArrayList<String> getyiji(int year, int month) {
        int num_y = -1;
        int num_m = (year - 1900) * 12 + month-1 +12;
        int num_d;
        int mLMonth=1,mLDay=1,mLun_x=0;
        int days_of_month = getDaysOfMonth(year, month);
        ArrayList<String> yiji = new ArrayList<String>();

        //年柱 1900年立春后为庚子年(60进制36)
        //立春日期
        int term2 = sTerm(year, 2);

        //月柱  1900年1月小寒以前为 丙子月(60进制12)
        //当月的24节气中的节开始日
        int firstNode = sTerm(year, (month-1)*2);
        //1900/1/1与 1970/1/1 相差25567日, 1900/1/1 日柱为甲戌日(60进制10)
        int dayCyclical = (int) (getDaysOfTwoDate(1900,1,1,year,month,1)+10);
        for(int i=0;i<days_of_month;i++){
            if(mLDay > mLun_x){
                int var = getLunarDateInt(year,month,i+1);
                int mLYear =(int)var/10000;
                mLMonth = (int)(var%10000)/100;
                mLDay = (int)(var - mLYear*10000 - mLMonth*100);
                mLun_x = (leapMonth(mLYear)!=0)?leapDays(mLYear):monthDays(mLYear, mLMonth);
            }
            //依节气调整二月分的年柱, 以立春为界
            if(month==2 && (i+1)==term2){
                num_y = year - 1900 + 36;
            }
            //依节气 调整月柱, 以「节」为界
            if((i+1)==firstNode) {
                num_m =(year - 1900) * 12 + month +12 ;
            }
            //日柱
            num_d = dayCyclical +i;

            mLDay++;

            String str = CalConv2(num_y%12, num_m%12, num_d%12, num_y%10, num_d%10, mLMonth, mLDay-1);
            if(TextUtils.isEmpty(str)){
                str = jcrt(DateConstants.LUNAR.JC_NAME[num_m%12][num_d%12]);
            }
            yiji.add(str);
        }

        return yiji;
    }

    /**
     * 公历某日宜忌
     * */
    public String getyiji(int year, int month, int day) {
        int num_y = -1;
        int num_m = (year - 1900) * 12 + month-1 +12;
        int num_d;
        int mLMonth=1,mLDay=1;
        int days_of_month = getDaysOfMonth(year, month);

        if(day > days_of_month) {
            day = days_of_month;
        }

        //年柱 1900年立春后为庚子年(60进制36)
        //cyclical(year,month);
        //立春日期
        int term2 = sTerm(year, 2);
        int firstNode = sTerm(year, (month-1)*2);//当月的24节气中的节开始日
        int dayCyclical = (int) (getDaysOfTwoDate(1900,1,1,year,month,1)+10);

        if(month==2 && day==term2){
            //cY = cyclicalm(year-1900 + 36);//依节气调整二月分的年柱, 以立春为界
            num_y = year - 1900 + 36;
        }
        if(day==firstNode) {
            num_m =(year - 1900) * 12 + month +12 ;//依节气 调整月柱, 以「节」为界
            //cM = cyclicalm(num_m);
        }
        num_d = dayCyclical +day -1;

        int var = getLunarDateInt(year,month,day);
        int mLYear =(int)var/10000;
        mLMonth = (int)(var%10000)/100;
        mLDay = (int)(var - mLYear*10000 - mLMonth*100);

        String str = CalConv2(num_y%12, num_m%12, num_d%12, num_y%10, num_d%10, mLMonth, mLDay);
        if(str == null){
            str = jcrt(DateConstants.LUNAR.JC_NAME[num_m%12][num_d%12]);
        }
        return str;
    }


    /**
     * 计算距离1900年12月31日days天后的日期
     * */
    public int getDateFromBaseDate(int days){
        int year = 0,month = 0,day = 0;
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.YEAR, 1900);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH,31);
        cal.add(Calendar.DATE, days);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        return 10000*year+100*month+day;
    }
    /******************************************************************/
}

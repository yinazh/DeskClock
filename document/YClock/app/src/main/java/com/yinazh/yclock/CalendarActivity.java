package com.yinazh.yclock;

import android.widget.CalendarView;

import com.yinazh.yclock.base.BaseActivity;
import com.yinazh.yclock.date.DateTime;

/**
 * @author yinazh
 *
 * 可参考：https://github.com/huanghaibin-dev/CalendarView
 */
public class CalendarActivity extends BaseActivity {
    private CalendarView calendarView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_calendar;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        calendarView = findViewById(R.id.id_calview);

        calendarView.setDate(DateTime.build().getDate());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

            }
        });
    }
}

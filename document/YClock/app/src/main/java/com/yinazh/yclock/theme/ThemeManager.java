package com.yinazh.yclock.theme;


/**
 * 主题管理类
 * */
public class ThemeManager {

    //TODO:自定义主题的实现；更新主题；主题存储

    //TODO：单例怎么简化


    private static ThemeManager INSTANCE;
    private ThemeType themeType;

    private ThemeManager(){

    }

    public static ThemeManager getInstance(){
        if(INSTANCE != null){
            INSTANCE = new ThemeManager();
        }
        return INSTANCE;
    }

    public ThemeType getThemeType(){
        return ThemeType.THEME_DEFAULT;
    }

    public void setThemeType(){

    }



}

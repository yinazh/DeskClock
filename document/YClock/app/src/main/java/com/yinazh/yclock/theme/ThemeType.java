package com.yinazh.yclock.theme;

/**
 * 主题类别，包括可自定义的主题
 * */
public enum ThemeType {

    THEME_DEFAULT("default", 1000, "default theme"),
    THEME_NIGHT("night", 1001, "night theme"),

    ;

    private String themeName;
    private int themeId;
    private String themeDescribe;


    ThemeType(String themeName, int themeId, String themeDescribe) {
        this.themeName = themeName;
        this.themeId = themeId;
        this.themeDescribe = themeDescribe;
    }


    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public String getThemeDescribe() {
        return themeDescribe;
    }

    public void setThemeDescribe(String themeDescribe) {
        this.themeDescribe = themeDescribe;
    }
}

package com.yinazh.yclock.font;

public enum FontsType {

    DIGITAL_ITALIC("digital_italic", "fonts/digital_italic.ttf"),
    DIGITAL_MONO_ITALIC("digital_mono_italic", "fonts/digital_mono_italic.ttf"),
    DIGITAL_MONO("digital_mono", "fonts/digital_mono.ttf"),
    DIGITAL("digital", "fonts/digital.ttf"),
    LINEBEAM("linebeam", "fonts/linebeam.ttf"),
    LOOPY_IT("loopy_it", "fonts/loopy_it.ttf"),
    LOOPY("loopy", "fonts/loopy.ttf"),
    RADIOLAND("radioland", "fonts/radioland.ttf"),
    RADIOLANDSLIM("radiolandslim", "fonts/radiolandslim.ttf"),

    ;

    private String name;
    private String file;

    FontsType(String name1, String file1){
        this.name = name1;
        this.file = file1;
    }

    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "FontsType{" +
                "name='" + name + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}

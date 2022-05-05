package com.yinazh.yclock.font;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.yinazh.yclock.debug.DebugConstants;

public class FontManager implements FontInterface{

    public static final String TAG = FontManager.class.getSimpleName();
    public static final boolean DEBUG = DebugConstants.DEBUG_FONT;
    public static FontsType DEFAULT_FONTS = FontsType.DIGITAL_MONO;
    private Context mContext;

    private static FontManager INSTANCE;
    public static FontManager getInstance(Context context){
        if(INSTANCE == null) INSTANCE = new FontManager(context);
        return INSTANCE;
    }
    private FontManager(Context context){
        this.mContext = context;
    }

    @Override
    public Typeface getFont() {
        AssetManager assets = mContext.getAssets();
        return Typeface.createFromAsset(assets, DEFAULT_FONTS.getFile());
    }
}

package com.yinazh.yclock.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author yinazh
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutId();
    protected abstract void initData();
    protected abstract void initView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        immersive();
        setContentView(getView());
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initData();
        initView();
    }

    public View getView() {
        return View.inflate(this, getLayoutId(), null);
    }

    /**
     * 有参跳跳转
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle){
        Intent in=new Intent(this,clz);
        in.putExtras(bundle);
        startActivity(in);
    }

    public void startActivityFinish(Class<?> clz, Bundle bundle){
        Intent in=new Intent(this,clz);
        in.putExtras(bundle);
        startActivity(in);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void immersive() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }
}

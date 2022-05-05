package com.yinazh.yclock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yinazh.yclock.base.BaseActivity;
import com.yinazh.yclock.date.DateTime;
import com.yinazh.yclock.debug.DebugConstants;
import com.yinazh.yclock.map.YLocation;

/**
 * @author yinazh
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = DebugConstants.YCLOCK_TAG + MainActivity.class.getSimpleName();
    private static final boolean DEBUG = false || DebugConstants.APP_DEBUG;

    private YLocation yLocation;

    private Context context;
    private TextView timeView;
    private TextView lunarView;
    private TextView dateView;

    private RelativeLayout addressContent;
    private TextView addressView;

    private static final int EVENT_UPDATE_TIME = 0x1000;
    private String todayDate = null;
    private DateTime dateCalendar;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case EVENT_UPDATE_TIME:
                    timeView.setText(dateCalendar.getTime(System.currentTimeMillis()));
                    String temp = dateCalendar.getDateStr();
                    if(TextUtils.isEmpty(todayDate) || !temp.equals(todayDate)){
                        todayDate = temp;
                        lunarView.setText(dateCalendar.getLunarString());
                    }
                    dateView.setText(dateCalendar.getDateWithWeek());

                    if(yLocation != null){
                        addressContent.setVisibility(View.VISIBLE);
                        addressView.setText(yLocation.getAddress());
                    }else{
                        addressContent.setVisibility(View.GONE);
                    }

                    sendEmptyMessageDelayed(EVENT_UPDATE_TIME, 1000);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        context = this;
    }

    @Override
    protected void initView() {
        timeView = findViewById(R.id.tv_time);
        lunarView = findViewById(R.id.tv_lunardate);
        dateView = findViewById(R.id.tv_date);
        dateView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CalendarActivity.class);
            startActivity(intent);
        });

        addressContent = findViewById(R.id.address_content);
        addressView = findViewById(R.id.tv_address);

        dateCalendar = DateTime.build();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.sendEmptyMessage(EVENT_UPDATE_TIME);

        yLocation = YLocation.getInstance().init(this.getApplicationContext());

        if(yLocation.checkPermission()){
            yLocation.requestLocationPermissions(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!yLocation.checkPermission()){
            yLocation.startLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null){
            handler.removeMessages(EVENT_UPDATE_TIME);
        }
        yLocation.destroyLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1001:
                if (grantResults.length > 0) {
                    boolean permission = false;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                            Toast.makeText(this, permissions[i] + "权限被拒绝了，无法开启定位服务", Toast.LENGTH_SHORT).show();
                            permission = false;
                            break;
                        }
                        permission = true;
                    }
                    if(permission){
                        yLocation.startLocation();
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
}
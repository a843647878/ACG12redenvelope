package com.acg12.redenvelope.activity;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acg12.redenvelope.R;
import com.acg12.redenvelope.StatusValue;
import com.acg12.redenvelope.receiver.ScreenReceiverUtil;
import com.acg12.redenvelope.service.DaemonService;
import com.acg12.redenvelope.service.PlayerMusicService;
import com.acg12.redenvelope.util.Contants;
import com.acg12.redenvelope.util.JobSchedulerManager;
import com.acg12.redenvelope.util.ScreenManager;
import com.acg12.redenvelope.util.T;
import com.acg12.redenvelope.util.VersionHelper;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

/**
 * Description
 * Created by chengwanying on 2018/1/25.
 * Company BeiJing kaimijiaoyu
 *
 * @author CU
 */

public class MainActivity extends BaseActivity implements AccessibilityManager.AccessibilityStateChangeListener {

    public static final int RECEIVE_BOOT_COMPLETED = 2001;

    private static String TAG = "ACG12";
    private AccessibilityManager accessibilityManager;

    private TextView openTextView;

    private boolean isRunning;
    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;


    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();
        }

        @Override
        public void onSreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            // "咕咚"、"乐动力"、"悦动圈"就是这么做滴
//            Intent intent = new Intent(SportsActivity.this,SportsActivity.class);
//            startActivity(intent);
            // 如果你觉得，直接跳出SportActivity很不爽
            // 那么，我们就制造个"1像素"惨案
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Contants.DEBUG)
            Log.d(TAG, "--->onCreate");

        String[] permisions = {Manifest.permission.RECEIVE_BOOT_COMPLETED};
        if (checkPermissions(permisions)) {
            // 1. 注册锁屏广播监听器
//        mScreenListener = new ScreenReceiverUtil(this);
//        mScreenManager = ScreenManager.getScreenManagerInstance(this);
//        mScreenListener.setScreenReceiverListener(mScreenListenerer);
            // 2. 启动系统任务
            //这里需要添加权限
            mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
            mJobManager.startJobScheduler();
            // 3. 启动前台Service
            startDaemonService();
            // 4. 启动播放音乐Service
            startPlayMusicService();
            initView(this);
        } else {
            reqPermission(permisions, RECEIVE_BOOT_COMPLETED);
        }


    }

    @Override
    public void initView(Activity activity) {
        super.initView(activity);
        VersionHelper.handleMaterialStatusBar(this);
        try {
            String weChatVersion = VersionHelper.getVersionName(this, VersionHelper.WechatPackageName);
            Log.d(TAG, "WeChatVersion:" + weChatVersion);
            if (weChatVersion.compareToIgnoreCase("6.6.0") >= 0) {
                Log.d(TAG, "大于6.6.0");
                StatusValue.getInstance().setIsSupportDelete(false);
            } else {
                Log.d(TAG, "小于6.6.0");
                StatusValue.getInstance().setIsSupportDelete(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        accessibilityManager =
                (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        openTextView = (TextView) activity.findViewById(R.id.tv_open);
        openTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(mAccessibleIntent);
                } catch (Exception e) {
                    T.showShort("遇到一些问题,请手动打开系统“设置”->找到“无障碍”或者“辅助服务”->“签到钱就到”");
                }
            }
        });

        updateServiceStatus();
    }


    private void updateServiceStatus() {
        boolean serviceEnabled = false;
        if (accessibilityManager == null) return;
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info != null && info.getId() != null && info.getId().equals(getPackageName() + "/.service.HongbaoService")) {
                serviceEnabled = true;
            }
        }

        if (serviceEnabled) {
            openTextView.setText("关闭插件");
            // Prevent screen from dimming
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            openTextView.setText("开启插件");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
//        Toast.makeText(this, "版本号：" + getVersionName(), Toast.LENGTH_SHORT).show();
    }

    private void initPreferenceValue() {
        String excludeWordses = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_watch_exclude_words", "");
        StatusValue.getInstance().setExculdeWords(excludeWordses);

        boolean issupportBlackSceen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_watch_black_screen_notification", true);
        StatusValue.getInstance().setIsSupportBlackSreen(issupportBlackSceen);

        boolean isSupportAutoRob = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_click_open_hongbao", true);
        StatusValue.getInstance().setIsSupportAutoRob(isSupportAutoRob);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        updateServiceStatus();
        initPreferenceValue();
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }


    private void stopPlayMusicService() {
        Intent intent = new Intent(MainActivity.this, PlayerMusicService.class);
        stopService(intent);
    }

    private void startPlayMusicService() {
        Intent intent = new Intent(MainActivity.this, PlayerMusicService.class);
        startService(intent);
    }

    private void startDaemonService() {
        Intent intent = new Intent(MainActivity.this, DaemonService.class);
        startService(intent);
    }

    private void stopDaemonService() {
        Intent intent = new Intent(MainActivity.this, DaemonService.class);
        stopService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RECEIVE_BOOT_COMPLETED:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 1. 注册锁屏广播监听器
//        mScreenListener = new ScreenReceiverUtil(this);
//        mScreenManager = ScreenManager.getScreenManagerInstance(this);
//        mScreenListener.setScreenReceiverListener(mScreenListenerer);
                    // 2. 启动系统任务
                    mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
                    mJobManager.startJobScheduler();
                    // 3. 启动前台Service
                    startDaemonService();
                    // 4. 启动播放音乐Service
                    startPlayMusicService();
                    initView(this);
                } else {
                    T.showShort("权限被拒绝,请退出应用重新打开!");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

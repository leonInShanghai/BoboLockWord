package com.bobo.bobolockword;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by 公众号：IT波 on 2021/6/13 Copyright © Leon. All rights reserved.
 * Functions: 这个才是app逻辑上的首页
 * Marketing is about selling products or servides. The goal is to show the value of a product.
 * The marketing mix helps make a product successful. It is also called the for Ps.
 * Tell me more.
 * On of the four Ps is product.
 * A product is some item that customers want.All products also have a life-cycle.
 * Do some products have longer life-cycles than others?
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener, LEAlertDialog.ClickListenerInterface {

    private ScreenListener screenListener;
    private SharedPreferences sharedPreferences;
    private FragmentTransaction transaction;
    private StudyFragment studyFragment;
    private SetFragment setFragment;
    // 右上角的“错词本”
    private Button wrongBtn;
    private LEAlertDialog leAlertDialog;


    @Override
    protected void onResume() {
        super.onResume();

        // 注意onResume一直会调用千万注意判断一下只能让它进来一次
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (leAlertDialog == null) {
                // 申请浮窗权限 判断权限是否赋予过
                if (!Settings.canDrawOverlays(this)) {
                    leAlertDialog = new LEAlertDialog(this, R.style.le_dialog,
                            "没有锁屏显示权限去设置打开?", "确定", "取消",
                            true);
                    // 用户点击其它空白地方和返回键不会消失
                    leAlertDialog.setCancelable(false);
                    leAlertDialog.show();
                    leAlertDialog.setClicklistener(this);
                }
            } else {
                if (leAlertDialog != null && Settings.canDrawOverlays(this)) {
                    leAlertDialog.dismiss();
                    if (!sharedPreferences.getBoolean(Common.BTN_TF_KEY, false)) {
                        Toast.makeText(this, "请开启单词锁屏", Toast.LENGTH_SHORT).show();
                        clickSet(null);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        LETrtStBarUtil.setTransparentToolbar(this);
        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
        wrongBtn = findViewById(R.id.wrong_btn);
        wrongBtn.setOnClickListener(this);
        // 设置editor用于编辑数据
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        screenListener = new ScreenListener(this);

        // 开始监听广播状态
        screenListener.begin(new ScreenListener.ScreenStateListener() {

            /**
             * 手机点亮屏幕回调
             */
            @Override
            public void onScreenOn() {
                Log.d("HomeActivity", "onScreenOn");
                // 判断是否在锁屏页面开启了锁屏按钮
                if (sharedPreferences.getBoolean(Common.BTN_TF_KEY, false)) {
                    // 判断屏幕是否解锁
                    if (sharedPreferences.getBoolean(Common.TF_KEY, false)) {
                        // 启动并跳转到锁屏页
//                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        HomeActivity.this.startActivity(intent);
                    }
                }
            }

            /**
             * 手机已锁屏回调
             */
            @Override
            public void onScreenOff() {
                Log.d("HomeActivity", "onScreenOff");
                // 如果手机已经锁屏了就把tf改成true
                // editor.putBoolean(Common.TF_KEY, true);
                // editor.commit();
                BaseApplication.destoryActivity(Common.MAIN_ACTIVITY);
            }

            /**
             * 手机已经解锁的回调
             */
            @Override
            public void onUserPresent() {
                Log.d("HomeActivity", "onUserPresent***************8");
                // 如果手机已经解锁了就把tf改成false
                // editor.putBoolean(Common.TF_KEY, false);
                // editor.commit();

                // 启动并跳转到锁屏页
                if (sharedPreferences.getBoolean(Common.BTN_TF_KEY, false)) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    HomeActivity.this.startActivity(intent);
                }
            }
        });

        // 当此页面加载的时候先显示复习界面Fragmnet
        studyFragment = new StudyFragment();
        setShowFragment(studyFragment);
    }

    /**
     * 点击不同的按钮显示不同的Fragment
     * @param fragment
     */
    public void setShowFragment(Fragment fragment) {
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.commit();
    }

    /**
     * 用户点击了“复习”  xml中设置点击事件
     * @param view
     */
    public void clickRevise(View view) {
        if (studyFragment == null) {
            studyFragment = new StudyFragment();
        }
        setShowFragment(studyFragment);
    }

    /**
     * 用户点击了“设置” xml中设置点击事件
     * @param view
     */
    public void clickSet(View view) {
        if (setFragment == null) {
            setFragment = new SetFragment();
        }
        setShowFragment(setFragment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wrong_btn:
                // 用户点击了“错题本”
                Toast.makeText(this, "点击了错题本", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // 解除广播
        if (screenListener != null) {
            screenListener.unregisterReceiver();
        }
        super.onDestroy();
    }

    /**
     * 用户点击了LEAlertDialog上的 确定按钮
     */
    @Override
    public void doConfirm() {
        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" +
                        getPackageName())), Common.SYSTEM_ALERT_WINDOW_CODE);
    }

    /**
     * 用户点击了LEAlertDialog上的 取消按钮
     */
    @Override
    public void doCancel() {
        Toast.makeText(this, "您没有打开浮窗权限将无法正常使用!", Toast.LENGTH_SHORT).show();
    }
}

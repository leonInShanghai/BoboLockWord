package com.bobo.bobolockword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 公众号：IT波 on 2021/6/13 Copyright © Leon. All rights reserved.
 * Functions: 屏幕监听类
 */
public class ScreenListener {
    private Context context;
    // 定义一个广播（动态注册）
    private ScreenBroadcastReceiver mScreenReceiver;
    // 定义的接口
    private ScreenStateListener mScreenStateListener;

    // 构造方法
    public ScreenListener(Context context) {
        this.context = context;
        // 实例化广播
        this.mScreenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * 自定义关于屏幕的接口
     */
    public interface ScreenStateListener {
        // 手机屏幕点亮
        void onScreenOn();

        // 手机屏幕关闭
        void onScreenOff();

        // 手机屏幕解锁
        void onUserPresent();
    }

    /**
     * 获取屏幕的状态
     */
    private void getScreenState() {
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()) {
            if (mScreenStateListener != null) {
                // FIXME:临时注释
                // mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }
    /**
     * 内部类广播 public static
     */
    class ScreenBroadcastReceiver extends BroadcastReceiver {

        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();

            if (mScreenStateListener == null) {
                Log.e("ScreenListener", "Waring : mScreenStateListener is null");
                return;
            }

            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                mScreenStateListener.onScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mScreenStateListener.onScreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                mScreenStateListener.onUserPresent();
            }

        }
    }

    /**
     * 开始监听广播状态
     * @param listener
     */
    public void begin(ScreenStateListener listener) {
        mScreenStateListener = listener;
        // 注册监听
        registerListener();
        // 获取当前屏幕状态并通过接口回调出去
        getScreenState();
    }

    /**
     * 启动屏幕广播接收器
     */
    private void registerListener() {
        // filter |菲欧特| 录波器的意识
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 注册广播
        context.registerReceiver(mScreenReceiver, filter);
    }

    /**
     * 解除注册广播
     */
    public void unregisterReceiver() {
        if (mScreenReceiver != null) {
            context.unregisterReceiver(mScreenReceiver);
        }
    }
}

package com.bobo.bobolockword;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by 公众号：IT波 on 2021/6/13 Copyright © Leon. All rights reserved.
 * Functions: 全局唯一应用程序类 它的生命周期贯穿整个app的生命周期
 */
public class BaseApplication extends Application {

    // 创建一个Map的集合用来把activity添加到map里面
    private static Map<String, Activity> destoryMap = new HashMap<>();

    /**
     * 添加到销毁map
     */
    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 因为程序默认都是启动锁屏页这个方法很有必要
     * 根据String activityName销毁指定activity
     * @param activityName
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        for (String key : keySet) {
            destoryMap.get(key).finish();
        }
    }
}

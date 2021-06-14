package com.bobo.bobolockword;

/**
 * Created by 公众号：IT波 on 2021/6/6 Copyright © Leon. All rights reserved.
 * Functions:
 */
public class Common {
    /**
     * SharedPreference key 全局使用唯一
     */
    public static final String SP_KEY = "bobo_lock_word";

    /**
     * 本地持久化保存答对题目的个数 这个没有用上
     */
    public static final String ALREADY_MASTERED = "alreadyMastered";

    /**
     * 本地持久化保存答对题目计数的key
     */
    public static final String RIGHT_COUNT = "right_count";

    /**
     * 本地持久化保存答错题目计数的key
     */
    public static final String ANSWER_WRONG = "answer_wrong";

    /**
     * 本地持久化保存 难度等级的key
     */
    public static final String DIFFICULTY_LEVEL = "difficulty_level";

    /**
     * 难度Spinner的默认选项key
     */
    public static final String DIFFICULTY_KEY = "difficulty";

    /**
     * 解锁题的个数Spinner的默认选项key
     */
    public static final String ALL_NUMBER_KEY = "all_number";

    /**
     * 每日新题数Spinner的默认选项key
     */
    public static final String NEW_NUMBER_KEY = "new_number";

    /**
     * 复习题数Spinner的默认选项key
     */
    public static final String REVISE_NUM_KEY = "revise_num";

    /**
     * 设置页Switch开关按钮状态保存
     */
    public static final String BTN_TF_KEY = "btn_tf";

    /**
     * 判断屏幕是否解锁
     */
    public static final String TF_KEY = "tf";

    /**
     * MainActivity的名字,用于关闭MainActivity
     */
    public static final String MAIN_ACTIVITY = "MainActivity";

    /**
     * 申请浮窗权限的key
     */
    public static final int SYSTEM_ALERT_WINDOW_CODE = 614;

}

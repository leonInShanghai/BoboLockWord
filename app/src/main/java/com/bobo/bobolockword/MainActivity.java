package com.bobo.bobolockword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bobo.bobolockword.entity.greendao.CET4Entity;
import com.bobo.bobolockword.entity.greendao.CET4EntityDao;
import com.bobo.bobolockword.entity.greendao.DaoMaster;
import com.bobo.bobolockword.entity.greendao.DaoSession;
import com.example.assetsbasedata.AssetsDatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by 公众号：IT波 on 2021/5/23 Copyright © Leon. All rights reserved.
 * Functions: 锁屏背单词首页
 */
public class MainActivity extends Activity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    private TextView timeText;
    private TextView dateText;
    private TextView wordText;
    // symbol [ˈsɪmbl] （念：绅宝） 译：符号（音标符号）
    private TextView symbolText;

    // 播放单词声音的小喇叭
    private ImageView playVoice;

    private String mMonth;
    private String mDay;
    private String mWeek;
    private String mHours;
    private String mMinute;

    private TextToSpeech textToSpeech;

    // 键盘管理类
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;

    // 词义选择答案
    private RadioGroup radioGroup;
    private RadioButton radioOne;
    private RadioButton radioTwo;
    private RadioButton radioThree;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor = null;

    private int j = 0;
    private List<Integer> list;
    // Entity [ˈentəti] : 实体
    private List<CET4Entity> datas;
    private int k;

    private float x1 = 0;
    private float y1 = 0;
    private float x2 = 0;
    private float y2 = 0;

    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoMaster dbMaster;
    private DaoSession mDaoSession;
    private DaoSession dbSession;

    // 对应表，由java代码生成的，对数据库内相应的表操作使用此对象
    private CET4EntityDao questionDao;
    private CET4EntityDao dbDao;

    private static final int DELAYED = 600;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == DELAYED) {
                // FIXME: 新增加
                unlocked();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置沉浸式状态栏
        LETrtStBarUtil.setTransparentToolbar(this);

        // 使得锁屏界面显示在手机的最上方
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        init();
    }

    // 初始化view及所必须的对象
    private void init() {
        sharedPreferences = getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        list = new ArrayList<Integer>();

        // 获取管理对象因为数据库需要管理对象才能够获取
        AssetsDatabaseManager.initManager(this);
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db1 = mg.getDatabase("word.db");
        // 对数据库进行操作
        mDaoMaster = new DaoMaster(db1);
        mDaoSession = mDaoMaster.newSession();
        questionDao = mDaoSession.getCET4EntityDao();
        datas = questionDao.queryBuilder().list();

        // 生成一个随机数
        Random r = new Random();
        int i;
        while (list.size() < 10) {
            i = r.nextInt(datas == null ? 0 : datas.size());
            if (!list.contains(i)) {
                list.add(i);
            }
        }

        // 得到键盘管理对象
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");


        /**
         * 创建回答错的单词数据库
         * 此DevOpenHelper类继承自SQLiteOpenHelper 见：https://blog.csdn.net/weixin_38231395/article/details/108417831
         * 第一个参数上下文
         * 第二个参数name：数据库的名字
         * 第三个参数，factory：游标工厂  传null使用默认
         */
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "wrong.db", null);

        // 创建数据库
        db = helper.getWritableDatabase();
        dbMaster = new DaoMaster(db);
        dbSession = dbMaster.newSession();
        dbDao = dbSession.getCET4EntityDao();

        // UI控件初始化
        // 显示：13:38
        timeText = findViewById(R.id.time_text);

        // 显示：11月10日 星期六
        dateText = findViewById(R.id.date_text);

        // 显示：parent
        wordText = findViewById(R.id.word_text);

        // 显示音标的Textview
        symbolText = findViewById(R.id.symbol_text);

        // 播放单词声音的小喇叭
        playVoice = findViewById(R.id.play_vioce);

        // 小喇叭点击事件监听
        playVoice.setOnClickListener(this);

        // 加载单词选项父控件
        radioGroup = findViewById(R.id.choose_group);
        radioOne = findViewById(R.id.choose_btn_one);
        radioTwo = findViewById(R.id.choose_btn_two);
        radioThree = findViewById(R.id.choose_btn_three);
        // RadioGroup点击事件的监听
        radioGroup.setOnCheckedChangeListener(this);

        // 初始化语音
        initPlayVoice();

        // 获取数据
        getDBData(datas);
    }

    /**
     * 初始化语音播报
     */
    private void initPlayVoice() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("MainActivity","-----数据丢失或不支持------------------");
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();

        //  获取日期时间
        Calendar calendar = Calendar.getInstance();
        mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

        /**
         * 为了显示好看给个位数小时前面加个0
         */
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            // mHours = "0" + calendar.get(Calendar.HOUR);
            mHours = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        } else if (calendar.get(Calendar.HOUR_OF_DAY) > 12){
            // mHours = String.valueOf(calendar.get(Calendar.HOUR));
            mHours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY) - 12);
        } else {
            mHours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }

        /**
         * 为了显示好看给个位数分钟前面加个0
         */
        if (calendar.get(Calendar.MINUTE) < 10) {
            mMinute = "0" + calendar.get(Calendar.MINUTE);
        } else {
            mMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        }

        /**
         * 获取星期阿拉伯数字转换为英文
         */
        if ("1".equals(mWeek)) {
            mWeek = "Sunday";
        } else if ("2".equals(mWeek)) {
            mWeek = "Monday";
        } else if ("3".equals(mWeek)) {
            mWeek = "Tuesday";
        } else if ("4".equals(mWeek)) {
            mWeek = "Wednesday";
        } else if ("5".equals(mWeek)) {
            mWeek = "Thursday";
        } else if ("6".equals(mWeek)) {
            mWeek = "Friday";
        } else if ("7".equals(mWeek)) {
            mWeek = "Saturday";
        }

        timeText.setText(mHours + " : " + mMinute);
        dateText.setText(mMonth + " 月 " + mDay + " 日 " + mWeek + " ");
        dateText.setOnClickListener(this);

        // 添加到销毁map
        BaseApplication.addDestoryActivity(this, Common.MAIN_ACTIVITY);
    }

    private void btnGetText(String msg, RadioButton button) {
        // 如果当前文本显示的和数据库中对应的索引是相符的
        if (msg.equals(datas.get(k).getChina())) {
            // 词文本颜色设置为绿色
            wordText.setTextColor(Color.GREEN);
            // 音标文本颜色设置为绿色
            symbolText.setTextColor(Color.GREEN);
            // Radio按钮颜色设置为绿色
            button.setTextColor(Color.GREEN);

            // 答对的单词+1
            int num = sharedPreferences.getInt(Common.RIGHT_COUNT, 0) + 1;
            editor.putInt(Common.RIGHT_COUNT, num);
            editor.commit();
        } else {
            // 词文本颜色设置为红色
            wordText.setTextColor(Color.RED);
            // 音标文本颜色设置为红色
            symbolText.setTextColor(Color.RED);
            // Radio按钮颜色设置为红色
            button.setTextColor(Color.RED);

            // 答错的单词加1
            int wrong = sharedPreferences.getInt(Common.ANSWER_WRONG, 0);
            editor.putInt(Common.ANSWER_WRONG, wrong + 1);
            editor.commit();
        }

        mHandler.sendEmptyMessageDelayed(DELAYED, DELAYED);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.play_vioce:
                // 用户点击了播放声音的小喇叭
                String text = wordText.getText().toString();
                // speechSynthesizer.startSpeaking(text, this);
                // 设置声调，1是正常，0.5f男声，くわい
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.date_text:
                // 设置声调，1是正常，0.5f男声，くわい
                textToSpeech.speak(mWeek, TextToSpeech.QUEUE_FLUSH, null);
                break;
        }
    }

    /**
     * RadioGroup 选中的事件
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        radioGroup.setClickable(false);
        switch (checkedId) {
            case R.id.choose_btn_one:
                // 从第三位开始截取，A: 答案
                String msg = radioOne.getText().toString().substring(3);
                btnGetText(msg, radioOne);
                break;
            case R.id.choose_btn_two:
                // 从第三位开始截取，A: 答案
                String msg2 = radioTwo.getText().toString().substring(3);
                btnGetText(msg2, radioTwo);
                break;
            case R.id.choose_btn_three:
                // 从第三位开始截取，A: 答案
                String msg3 = radioThree.getText().toString().substring(3);
                btnGetText(msg3, radioThree);
                break;
        }
    }

    /**
     * 还原单词选项的颜色
     */
    private void setTextColor() {
        radioOne.setChecked(false);
        radioTwo.setChecked(false);
        radioThree.setChecked(false);
        radioOne.setTextColor(Color.WHITE);
        radioTwo.setTextColor(Color.WHITE);
        radioThree.setTextColor(Color.WHITE);
        wordText.setTextColor(Color.WHITE);
        symbolText.setTextColor(Color.WHITE);
    }

    /**
     * 解锁方法
     */
    private void unlocked() {
        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent1);
        // FIXME:此处需要解锁权限
        kl.disableKeyguard();
        finish();
    }

    /**
     * 设置汉语答案
     */
    private void setChina(List<CET4Entity> datas, int j) {
        /**
         * 随机产生几个随机数，这里面产生几个随机数，是用于解锁单词
         * 因为词demo的数据库里面仅仅20个单词，所以产生的随机数也是20以内
         */
//        Random r = new Random();
//        List<Integer> listInt = new ArrayList<>();
//        int i;
//        while (listInt.size() < 4) {
//            i = r.nextInt(20);
//            if (!listInt.contains(i)) {
//                listInt.add(i);
//            }
//        }

        /**
         * 以下的判断是给这个单词设置三个选项，设置单词选项是有规律的
         * 三个选项，分别是正确的，正确的前一个，正确的后一个
         * 将这三个解释设置到单词选项上，以下为实现逻辑
         *
         * random.nextInt(3)代表生成0~2的随机数，后面+1就变成了1~3的随机数了 打乱正确答案的顺序
         */
        Random answer = new Random();
        int answerInt = answer.nextInt(3) + 1;

        Log.d("MainActivity","正确答案是 ：" + answerInt + ": " + datas.get(k).getChina());
        switch (answerInt) {
            case 1:
                radioOne.setText("A: " + datas.get(k).getChina());
                if (k - 1 > 0) {
                    radioTwo.setText("B: " + datas.get(k - 1).getChina());
                } else {
                    radioTwo.setText("B: " + datas.get(k + 2).getChina());
                }

                if (k + 1 < (datas == null ? 0 : datas.size())) {
                    radioThree.setText("C: " + datas.get(k + 1).getChina());
                } else {
                    radioThree.setText("C: " + datas.get(k - 1).getChina());
                }
                break;

            case 2:
                radioTwo.setText("B: " + datas.get(k).getChina());
                if (k - 1 > 0) {
                    radioOne.setText("A: " + datas.get(k - 1).getChina());
                } else {
                    radioOne.setText("A: " + datas.get(k + 2).getChina());
                }

                if (k + 1 < (datas == null ? 0 : datas.size())) {
                    radioThree.setText("C: " + datas.get(k + 1).getChina());
                } else {
                    radioThree.setText("C: " + datas.get(k - 1).getChina());
                }
                break;
            case 3:
                radioThree.setText("C: " + datas.get(k).getChina());
                if (k - 1 > 0) {
                    radioTwo.setText("B: " + datas.get(k - 1).getChina());
                } else {
                    radioTwo.setText("B: " + datas.get(k + 2).getChina());
                }

                if (k + 1 < (datas == null ? 0 : datas.size())) {
                    radioOne.setText("A: " + datas.get(k + 1).getChina());
                } else {
                    radioOne.setText("A: " + datas.get(k - 1).getChina());
                }
                break;
        }
        // if (listInt.get(0) < 7) {

//        } else if (listInt.get(0) < 14) {
//
//        }
    }

    /**
     * 设置数据库数据
     * @param datas
     */
    private void getDBData(List<CET4Entity> datas) {
        k = list.get(j);
        // 设置单词
        wordText.setText(this.datas.get(k).getWord());
        // 设置音标
        symbolText.setText(this.datas.get(k).getEnglish());
        setChina(this.datas, k);
    }

    /**
     * 设置下一道题
     */
    private void getNextData() {
        j++;
        int i = sharedPreferences.getInt(Common.ALL_NUMBER_KEY, 2);
        if (i >= j) {
            getDBData(datas);
            setTextColor();
            // 已经学习的单词+1  FIXME:答对
            // int num = sharedPreferences.getInt(Common.RIGHT_COUNT, 0) + 1;
            // editor.putInt(Common.RIGHT_COUNT, num);
        } else {
            // 解锁
            unlocked();
        }
    }

    /**
     * 用户手势划动事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 当用户手指按下屏幕记录 x1 y1 坐标
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            x2 = event.getX();
            y2 = event.getY();

            if (y1 - y2 > 200) {
                // 向上划 已掌握单词数量+1
                // int num = sharedPreferences.getInt(Common.ALREADY_MASTERED, 0) + 1;
                // editor.putInt(Common.ALREADY_MASTERED, num);
                // editor.commit();
                // Toast.makeText(this, "已掌握", Toast.LENGTH_SHORT).show();
                getNextData();
                // FIXME: 新增加
                unlocked();
            } else if (y2 - y1 > 200) {
                // Toast.makeText(this, "待加功能...", Toast.LENGTH_SHORT).show();
                // FIXME: 新增加
                unlocked();
            } else if (x1 - x2 > 200) {
                // 向左划 获取下一条数据
                getNextData();
                // FIXME: 新增加
                unlocked();
            } else if (x2 - x1 > 200) {
                // 向右划，解锁
                unlocked();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 用户按下了返回键
        unlocked();
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null){
            mHandler.removeMessages(DELAYED);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }
}
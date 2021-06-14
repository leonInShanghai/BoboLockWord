package com.bobo.bobolockword;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bobo.bobolockword.entity.greendao.DaoMaster;
import com.bobo.bobolockword.entity.greendao.DaoSession;
import com.bobo.bobolockword.entity.greendao.WisdomEntity;
import com.bobo.bobolockword.entity.greendao.WisdomEntityDao;
import com.example.assetsbasedata.AssetsDatabaseManager;

import java.util.List;
import java.util.Random;

/**
 * Created by 公众号：IT波 on 2021/6/6 Copyright © Leon. All rights reserved.
 * Functions: 复习界面
 */
public class StudyFragment extends Fragment {

    // 学习难度
    private TextView difficultyTv;
    // 名人名句英语
    private TextView wisdomEnglish;
    // 名人名句汉语
    private TextView wisdomChinese;
    // 已学习题数（答对+答错）
    private TextView alreadyStudyText;
    // 已经掌握题数-答对题数
    private TextView alreadyMasteredStudyText;
    // 答错题数
    private TextView wrongText;

    private SharedPreferences sharedPreferences;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    // 对应的表由Java生成的，对数据库内相应的表操作使用此对象
    private WisdomEntityDao questionDao;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.study_fragment_layout, null);
        sharedPreferences = getActivity().getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
        // 学习难度
        difficultyTv = view.findViewById(R.id.difficulty);
        // 名人名句英语
        wisdomEnglish = view.findViewById(R.id.wisdom_english);
        // 名人名句汉语
        wisdomChinese = view.findViewById(R.id.wisdom_chinese);
        // 答对的题
        alreadyMasteredStudyText = view.findViewById(R.id.right_count);
        // 已学习题数（答对+答错）
        alreadyStudyText = view.findViewById(R.id.total_count);
        // 答错题数数
        wrongText = view.findViewById(R.id.wrong_count);

        // 初始化
        AssetsDatabaseManager.initManager(getActivity());

        // 获取数据管理对象
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("wisdom.db");
        mDaoMaster = new DaoMaster(db1);
        mDaoSession = mDaoMaster.newSession();
        questionDao = mDaoSession.getWisdomEntityDao();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // 显示当前英语难度
        difficultyTv.setText(sharedPreferences.getString(Common.DIFFICULTY_LEVEL, "英语四级"));

        // 获取数据集合
        List<WisdomEntity> datas = questionDao.queryBuilder().list();
        Random random = new Random();
        // 获取一个10以内的随机数
        int i = random.nextInt(10);
        // 随机显示一条英语名人名句
        wisdomEnglish.setText(datas.get(i).getEnglish());
        wisdomChinese.setText(datas.get(i).getChina());
        
        setText();
    }

    private void setText() {
        // 答对的单词+1
        // int rightCount = sharedPreferences.getInt(Common.ALREADY_MASTERED, 0);
        int rightCount = sharedPreferences.getInt(Common.RIGHT_COUNT, 0);
        // 已经掌握题数-答对题数
        alreadyMasteredStudyText.setText(rightCount + " 词");

        // 设置答错题目个数
        int wrongCount = sharedPreferences.getInt(Common.ANSWER_WRONG, 0);
        wrongText.setText(wrongCount + " 词");

        // 已学习题数（答对+答错）
        alreadyStudyText.setText(rightCount + wrongCount + " 词");
    }
}

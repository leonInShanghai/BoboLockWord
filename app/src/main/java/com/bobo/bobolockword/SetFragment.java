package com.bobo.bobolockword;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by 公众号：IT波 on 2021/6/12 Copyright © Leon. All rights reserved.
 * Functions: 设置Fragment
 * We don't konw what will happen tomorrow.
 * So don't stay mad for too long.
 * Learn to leave things behind.
 * Be with someone you love.
 * Somethings are just not worth it.
 * But you are worth it.
 */
public class SetFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences sharedPreferences;

    private SwitchButton switchButton;
    private Spinner spinnerDifficulty;
    private Spinner spinnerAllNumber;
    private Spinner spinnerNewNum;
    private Spinner spinnerReviseNum;

    private ArrayAdapter<String> adapterDifficulty;
    private ArrayAdapter<String> adapterAllNum;
    private ArrayAdapter<String> adapterNewNum;
    private ArrayAdapter<String> adapterReviseNum;

    private String[] difficulty = new String[]{"小学","初中","高中","四级","六级"};
    private String[] allNum = new String[]{"2道","4道","6道","8道"};
    private String[] newNum = new String[]{"10题","30题","50题","100题"};
    private String[] reviseNum = new String[]{"10题","30题","50题","100题"};

    private SharedPreferences.Editor editor = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.set_fragment_layout, null);
        // 初始化各个UI控件
        init(view);
        return view;
    }

    private void init(View view) {
        sharedPreferences = getActivity().getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // 第一列开关按钮
        switchButton = view.findViewById(R.id.switch_btn);
        switchButton.setOnClickListener(this);
        // 选择难度下拉框
        spinnerDifficulty = view.findViewById(R.id.spinner_difficulty);
        // 解锁题的个数下拉框
        spinnerAllNumber = view.findViewById(R.id.spinner_all_number);
        // 每日新题的个数下拉框
        spinnerNewNum = view.findViewById(R.id.spinner_new_number);
        // 每日复习题的个数
        spinnerReviseNum = view.findViewById(R.id.spinner_revise_number);

        // 选择难度下拉框适配器
        adapterDifficulty = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner_item, difficulty);
        spinnerDifficulty.setAdapter(adapterDifficulty);
        // 定义选择难度下的默认选项
        setSpinnerItemSelectedByValue(spinnerDifficulty, sharedPreferences.getString(Common.DIFFICULTY_KEY, "四级"));
        // 设置难度Spinner下拉监听事件
        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                // 获取到选择内容
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString(Common.DIFFICULTY_KEY, msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 解锁题的个数下拉框适配器
        adapterAllNum = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner_item, allNum);
        spinnerAllNumber.setAdapter(adapterAllNum);
        setSpinnerItemSelectedByValue(spinnerAllNumber, sharedPreferences.getString(Common.ALL_NUMBER_KEY, "2道"));
        spinnerAllNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString(Common.ALL_NUMBER_KEY, msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 每日新题的个数下拉框的适配器
        adapterNewNum = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner_item, newNum);
        spinnerNewNum.setAdapter(adapterNewNum);
        setSpinnerItemSelectedByValue(spinnerNewNum, sharedPreferences.getString(Common.NEW_NUMBER_KEY, "10题"));
        spinnerNewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString(Common.NEW_NUMBER_KEY, msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 每日复习题的个数适配器
        adapterReviseNum = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner_item, reviseNum);
        spinnerReviseNum.setAdapter(adapterReviseNum);
        setSpinnerItemSelectedByValue(spinnerReviseNum, sharedPreferences.getString(Common.REVISE_NUM_KEY, "10题"));
        spinnerReviseNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString(Common.REVISE_NUM_KEY, msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 显示下拉框默认值
     * @param spinner
     * @param value
     */
    private void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter adapter = spinner.getAdapter();
        int k = adapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(adapter.getItem(i).toString())){
                spinner.setSelection(i,true);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // 获取Switch的状态
        if (sharedPreferences.getBoolean(Common.BTN_TF_KEY, false)) {
            // 开关状态应为开
            switchButton.openSwitch();
        } else {
            // 开关状态应为关
            switchButton.closeSwitch();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_btn:
                // switchButton点击事件的处理
                if (switchButton.isSwitchOpen()) {
                    switchButton.closeSwitch();
                    editor.putBoolean(Common.BTN_TF_KEY, false);
                    Toast.makeText(getActivity(),"关闭", Toast.LENGTH_SHORT).show();
                } else {
                    switchButton.openSwitch();
                    editor.putBoolean(Common.BTN_TF_KEY, true);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "开启", Toast.LENGTH_SHORT).show();
                    }
                }
                editor.commit();
                break;
        }
    }
}

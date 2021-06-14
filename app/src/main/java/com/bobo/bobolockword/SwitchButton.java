package com.bobo.bobolockword;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by 公众号：IT波 on 2021/6/12 Copyright © Leon. All rights reserved.
 * Functions: 自定义SwitchButton 非安卓SwitchButton
 */
public class SwitchButton extends FrameLayout {

    private ImageView openImage;
    private ImageView closeImage;

    public SwitchButton(@NonNull Context context) {
        this(context, null);
    }

    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 通过context获取属性
         */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);

        // 画出开关的打开状态
        Drawable openDrawable = typedArray.getDrawable(R.styleable.SwitchButton_switchOpenImage);

        // 画出开关的关闭状态
        Drawable closeDrawable = typedArray.getDrawable(R.styleable.SwitchButton_switchCloseImage);

        int switchStatus = typedArray.getInt(R.styleable.SwitchButton_switchStatus, 0);

        // 调用结束后务必调用recycle（）方法，否则这次的设定会对下次的使用造成影响
        typedArray.recycle();

        // 加载布局文件
        LayoutInflater.from(context).inflate(R.layout.switch_button, this);
        openImage = findViewById(R.id.iv_switch_open);
        closeImage = findViewById(R.id.iv_switch_close);

        if (openDrawable != null) {
            openImage.setImageDrawable(openDrawable);
        }

        if (closeDrawable != null) {
            closeImage.setImageDrawable(closeDrawable);
        }

        // 判断开关的状态
        if (switchStatus == 1) {
            // 执行关闭的方法
            closeSwitch();
        }
    }

    public boolean isSwitchOpen() {
        return openImage.getVisibility() == VISIBLE;
    }

    public void openSwitch() {
        openImage.setVisibility(VISIBLE);
        closeImage.setVisibility(INVISIBLE);
        // Toast.makeText(getContext(),"开启", Toast.LENGTH_SHORT).show();
    }

    public void closeSwitch() {
        openImage.setVisibility(INVISIBLE);
        closeImage.setVisibility(VISIBLE);
        // Toast.makeText(getContext(),"关闭", Toast.LENGTH_SHORT).show();
    }
}

package com.example.foodordering.util;

/**
 * Created by xch on 2017/6/26.
 */

/**
 * 自定义Listener
 *
 * 用于实现Dialog的复用
 *
 */
public interface IAlertDialogButtonListener {

    /**
     * 实现对话框的点击事件
     */
    void onDialogOkButtonClick();

    void onDialogCancelButtonClick();

}
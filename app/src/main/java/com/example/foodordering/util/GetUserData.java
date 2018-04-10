package com.example.foodordering.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.foodordering.bean.User;

/**
 * Created by xch on 2017/5/17.
 */

public class GetUserData {
    private String userId, address, name, email, nickname, sex, avatar, phoneNumber;//头像
    private int statusCode;
    private SharedPreferences sharedPreferences;
    public User user = new User();

    public User getUser(Context context) {
        //读取xml文件到
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = sharedPreferences.getString("userId", "");
        address = sharedPreferences.getString("address", "");
        name = sharedPreferences.getString("name", "");
        email = sharedPreferences.getString("email", "");
        nickname = sharedPreferences.getString("nickname", "");
        sex = sharedPreferences.getString("sex", "");
        avatar = sharedPreferences.getString("avatar", "");
        phoneNumber = sharedPreferences.getString("phoneNumber", "");

        if (sharedPreferences != null) {
            user.setId(userId);
            user.setName(name);
            user.setPhoneNumber(phoneNumber);
            user.setSex(sex);
            user.setNickname(nickname);
            user.setAddress(address);
            user.setEmail(email);
            user.setAvatar(avatar);
        }

        return user;
    }

    public int getStatusCode(Context context) {
        statusCode = 0;
        //读取xml文件到
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        statusCode = sharedPreferences.getInt("statusCode", 0);
        return statusCode;
    }
}

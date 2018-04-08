package com.example.foodordering.service;

import android.util.Log;

import com.example.foodordering.request.RequestManager;
import com.example.foodordering.request.Requests;
import com.example.foodordering.util.Util;

/**
 * Created by xch on 2017/4/7.
 */

public class PostUtility {
    private static RequestManager requestManager = new Requests();
    private static String requestUrl = Util.Url;

    /**
     * 登录验证，提交用户名密码
     *
     * @param userName
     * @param pwd
     * @return
     */
    public static String postLogin(String userName, String pwd) {
        String postForm = null;
        try {
            postForm = requestManager.postForm(
                    requestUrl + "Users/UserLogin",
                    "PhoneNumber", userName,
                    "Password", pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postForm;
    }

    /**
     * 提交订单
     *
     * @param userId
     * @param Business
     * @param DeliveryInfo
     * @param Contact
     * @param OrderAmount
     * @param OrderNote
     * @param OrderAddress
     * @param ProductId
     * @param Count
     * @param Longitude    经度
     * @param Latitude     纬度
     * @return
     */
    public static String postSubmitOrder(String userId, String Business, String DeliveryInfo, String Contact, String OrderAmount, String OrderNote, String OrderAddress, String ProductId, int Count, String Longitude, String Latitude) {
        String postForm = null;
        String BusinessId = Business.substring(Business.indexOf("-") + 1);
        try {

            postForm = requestManager.postForm(requestUrl + "UserOpera/SubmitOrder",
                    "ProductId", ProductId,
                    "OrderAmount", OrderAmount,
                    "Count", Count + "",
                    "UserId", userId,
                    "BusinessId", BusinessId,
                    "DeliveryInfo", DeliveryInfo,
                    "Contact", Contact,
                    "OrderNote", OrderNote,
                    "OrderAddress", OrderAddress,
                    "Longitude", Longitude,
                    "Latitude", Latitude
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postForm;
    }

    /**
     * 添加地址
     *
     * @param UserId      用户id
     * @param Address     收货地址
     * @param ReceiveName 收件人
     * @param Sex         性别
     * @param Contact     电话
     * @param Longitude   经度
     * @param Latitude    纬度
     * @return
     */
    public static String postInsertAddress(String UserId, String Address, String ReceiveName, String Sex, String Contact, String Longitude, String Latitude) {
        String postForm = null;
        try {
            postForm = requestManager.postForm(requestUrl + "Addresses/AddAddresses",
                    "Id", "1",
                    "UserId", UserId,
                    "Address", Address,
                    "ReceiveName", ReceiveName,
                    "Sex", Sex,
                    "Contact", Contact,
                    "Longitude", Longitude,
                    "Latitude", Latitude
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postForm;
    }



    /**
     * 注册
     *
     * @param PhoneNumber
     * @param Name
     * @param Password
     * @param NickName
     * @param Sex
     * @param Address
     * @return
     */
    public static String postRegister(String PhoneNumber, String Name, String Password, String NickName, String Sex, String Address) {
        String postForm = null;
        try {

            postForm = requestManager.postForm(requestUrl + "UserOpera/UserRegister",
                    "Id", "1",//默认传一个Id
                    "PhoneNumber", PhoneNumber,
                    "Name", Name,
                    "Password", Password,
                    "NickName", NickName,
                    "Sex", Sex,
                    "Address", Address
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postForm;
    }

    /**
     * 修改个人信息
     *
     * @param Id
     * @param Name
     * @param NickName
     * @param Sex
     * @param Address
     * @param EmailAddress
     * @return
     */
    public static String postChangeInfo(String Id, String Name, String NickName, String Sex, String Address, String EmailAddress) {
        String postForm = null;
        try {

            postForm = requestManager.postForm(requestUrl + "UserOpera/UserChangeInfo",
                    "UserId", Id,
                    "Email", EmailAddress,
                    "Name", Name,
                    "NickName", NickName,
                    "Sex", Sex,
                    "Address", Address
            );
        } catch (Exception e) {
            Log.e("postChangeInfo", Id);
            Log.e("postChangeInfo", EmailAddress);
            Log.e("postChangeInfo", Name);
            Log.e("postChangeInfo", NickName);
            Log.e("postChangeInfo", Sex);
            Log.e("postChangeInfo", Address);
            e.printStackTrace();
        }
        return postForm;
    }


    /**
     * 删除订单
     *
     * @param OrderId
     * @param UserId
     * @return
     */
    public static String postDeleteOrder(String OrderId, String UserId) {
        String postForm = null;
        try {
            postForm = requestManager.postForm(requestUrl + "UserOpera/DeleteOrder",
                    "OrderId", OrderId,
                    "UserId", UserId
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postForm;
    }

    /**
     * 确认订单
     *
     * @param OrderId
     * @param UserId
     * @return
     */
    public static String postCompleteOrder(String OrderId, String UserId) {
        String postForm = null;
        try {
            postForm = requestManager.postForm(requestUrl + "UserOpera/CompleteOrder",
                    "OrderId", OrderId,
                    "UserId", UserId
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postForm;
    }

    /**
     * 评价订单
     *
     * @param OrderId
     * @param UserId
     * @param Rating
     * @param CommentContent
     * @return
     */
    public static String postCommitCommentOrder(String OrderId, String UserId, String Rating, String CommentContent) {
        String postForm = null;
        try {
            postForm = requestManager.postForm(requestUrl + "UserOpera/CommitCommentOrder",
                    "OrderId", OrderId,
                    "UserId", UserId,
                    "Rating", Rating,
                    "CommentContent", CommentContent
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postForm;
    }
}

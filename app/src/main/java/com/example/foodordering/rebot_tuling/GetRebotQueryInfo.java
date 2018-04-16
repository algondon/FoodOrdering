package com.example.foodordering.rebot_tuling;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;

public class GetRebotQueryInfo {
    public static final String API_KEY = "3cb3543a6eeeba1365e8fbb4325e7e4b";
    public static final String html_url = "http://www.tuling123.com/openapi/api?key=";
    static String result = "";

    public static String getMsg(String msg) {
        HttpGet get = new HttpGet(html_url + API_KEY + "&info=" + URLEncoder.encode(msg));
        try {
            HttpResponse response = new DefaultHttpClient().execute(get);
            if (response.getStatusLine().getStatusCode() == 200)
                result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

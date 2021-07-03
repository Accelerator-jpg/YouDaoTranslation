package com.example.okhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client=new OkHttpClient.Builder()
            .build();
    String out;
    String q;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        text = findViewById(R.id.toText);
        TextView from=findViewById(R.id.fromText);
        View post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    q=from.getText().toString();
                    if(q.length()==0) return;
                    HttpRequestPost();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void HttpRequestPost() throws Exception {
        Calendar cal = Calendar.getInstance();

        String url="https://openapi.youdao.com/api";

        String from="AUTO";
        String to="AUTO";
        String appKey="01d8ea09098a4dce";
        String salt= UUID.randomUUID().toString().replace("-", "");
        String input;
        String curtime= String.valueOf(getSecondTimestamp(cal.getTime()));
        if(q.length()>20){
            input=q.substring(0,9)+q.length()+q.substring(q.length()-10,q.length()-1);
        }else{
            input=q;
        }
        String signType="v3";
        String signRaw=appKey+input+salt+curtime+"Nil2vsYRskhRq2dynLP2stTRoLpiO9qM";
        String sign=Sha256Utils.getSHA256(signRaw);

        Map m=new HashMap<>();
        m.put("q",q);
        m.put("from",from);
        m.put("to",to);
        m.put("appKey",appKey);
        m.put("salt",salt);
        m.put("sign",sign);
        m.put("signType",signType);
        m.put("curtime",curtime);

        RequestBody requestBody=new FormBody.Builder()
                .add("q",q)
                .add("from",from)
                .add("to",to)
                .add("appKey",appKey)
                .add("salt",salt)
                .add("sign",sign)
                .add("signType",signType)
                .add("curtime",curtime)
                .build();

        Request request=new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        final Call call=client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("EEE",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result=response.body().string();
                Map maps = (Map)JSON.parse(result);
//                for (Object map : maps.entrySet()){
//                    System.out.println(((Map.Entry)map).getKey()+"     " + ((Map.Entry)map).getValue());
//                }
//                Log.e("EEE",result);
                out=maps.get("translation").toString();
                text.setText(out.substring(2,out.length()-2));
            }
        });
    }


    private void HttpRequestGet() {
        String url="https://dict.youdao.com/";
        String plus="w/eng/你好/#keyfrom=dict2.index";

        Request request=new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("EEE",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result=response.body().string();
                Log.e("EEE",result);
                TextView text = findViewById(R.id.toText);
                text.setText(result);
            }
        });
    }
    public static int getSecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0,length-3));
        } else {
            return 0;
        }
    }
}
package com.google.ar.core.examples.java.augmentedimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.google.ar.core.examples.java.augmentedimage.rendering.AugmentedImageRenderer;

public class SubActivity extends AppCompatActivity {
    private static String TAG = "MyApp";
    public String omikuji;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        int omikuji_result;
        Intent intent = getIntent();
        omikuji_result = intent.getIntExtra("kekka",10);//値を受け取るok
        Log.d("MyApp", "omikuji_result=" + omikuji_result);//debug

        //String omikuji;//条件分岐
        if(omikuji_result == 0){
            omikuji = "大凶";
        }else if(omikuji_result == 1){
            omikuji = "末吉";
        }
        else if(omikuji_result == 2){
            omikuji = "小吉";
        }
        else if(omikuji_result == 3){
            omikuji = "吉";
        }
        else{
            omikuji = "大吉";
        }
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText("あなたは"+omikuji+"です！LINEでシェアしてクーポンコードをゲットしよう！");


    }

    public void onQupon(View v){
        String url_message = "https://line.me/R/msg/text/?"+"あなたは"+omikuji+"でした！みんなもおみくじにトライしてクーポンをゲットしよう！";
        Uri uri = Uri.parse(url_message);
        Intent intent1 = new Intent(Intent.ACTION_VIEW,uri);
        Intent intent2 = new Intent(this,qupon.class);
        startActivity(intent2);
        startActivity(intent1);
    }
}
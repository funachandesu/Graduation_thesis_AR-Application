package com.google.ar.core.examples.java.augmentedimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class qupon extends AppCompatActivity {
    public String omikuji;//条件分岐
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qupon);

        int omikuji_result;
        Intent intent = getIntent();
        omikuji_result = intent.getIntExtra("kekka",10);//値を受け取るok
        Log.d("MyApp", "omikuji_result=" + omikuji_result);//debug


        if(omikuji_result == 0){
            omikuji = "0000";//大凶
        }else if(omikuji_result == 1){
            omikuji = "1111";//末吉
        }
        else if(omikuji_result == 2){
            omikuji = "2222";//小吉
        }
        else if(omikuji_result == 3){
            omikuji = "3333";//吉
        }
        else{
            omikuji = "4444";//大吉
        }
        TextView textView = (TextView)findViewById(R.id.textView3);
        textView.setText("クーポンコードは"+omikuji+"です！");
    }
    public void onQupon2(View v){
        Uri uri = Uri.parse("https://line.me/R/ti/p/@396qhocl");
        Intent intent1 = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent1);
    }

    public void copy_b(View v){
        TextView textView_c = (TextView)findViewById(R.id.textView3);
        //クリップボードに格納するItemを作成
        ClipData.Item item = new ClipData.Item(omikuji);
        //MIMETYPEの作成
        String[] mimeType = new String[1];
        mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST;
        //クリップボードに格納するClipDataオブジェクトの作成
        ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
        //クリップボードにデータを格納
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(cd);
    }

}
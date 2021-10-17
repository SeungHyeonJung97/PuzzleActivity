package com.example.puzzleactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class IntroActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imageView = findViewById(R.id.iv_intro);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(msg.what == 1){
                    Glide.with(IntroActivity.this)
                            .load(R.drawable.karina_intro2)
                            .into(imageView);
                }else if(msg.what == 2){
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try{
                    for(int i=0;i<2;i++){
                        if(!(msg.what == 1)){
                            Thread.sleep(2000);
                            msg.what = 1;
                            handler.sendEmptyMessage(msg.what);
                        }else{
                            Thread.sleep(2000);
                            msg.what = 2;
                            handler.sendEmptyMessage(msg.what);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
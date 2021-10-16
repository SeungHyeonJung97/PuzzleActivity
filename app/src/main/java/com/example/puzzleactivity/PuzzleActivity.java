package com.example.puzzleactivity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class PuzzleActivity extends AppCompatActivity {

    // 이미지를 담을 배열
    ImageView iv[] = new ImageView[9];
    ProgressBar pb_timer;
    Timer timer;
    TimerTask timerTask;
    public TextView tv_timer;
    boolean toggle_timer = false;
    Handler mHandler;
    int blank_index = 8;

//    GestureDetector gesture;
//    public float downX, downY, upX, upY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        // 기기마다 display의 size가 다를 수 있으므로, display의 size를 구한다.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // 구한 size를 ImageSettings에 넘겨주어, ImageSettings()에서 Puzzle의 Image를 Setting 한다.
        ImageSettings(size);

        pb_timer = (ProgressBar) findViewById(R.id.pb_timer);
        tv_timer = (TextView) findViewById(R.id.tv_timer);

        initProgress();

        for (int i = 0; i < iv.length; i++) {
            int value_i = i;
            iv[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!toggle_timer) {
                        startTimerThread();
                    }
                    if (Math.abs(blank_index - value_i) == 1 || Math.abs(blank_index - value_i) == 3) {
                        SwapImage(value_i, blank_index);
                    }
                }
            });
        }

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                tv_timer.setText(msg.arg1 + " : " + msg.arg2);
                return true;
            }
        });
    }

    private void initProgress() {
        pb_timer.setMax(300);
        pb_timer.setProgress(300);
    }

    private void startTimerThread() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                PuzzleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pb_timer.getProgress() != 0) {
                            decreaseBar();
                        }
                    }
                });
                toggle_timer = true;
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    private void decreaseBar() {
        new Thread() {
            @Override
            public void run() {
                if (!(Thread.currentThread().isInterrupted())) {
                    int currentProgress = pb_timer.getProgress();
                    if (currentProgress > 0) {
                        currentProgress = currentProgress - 1;
                    }
                    if (currentProgress <= 0) {
                        Log.d("Puzzle", "Game Over");
                        this.interrupt();
                    }
                    pb_timer.setProgress(currentProgress);

                    /***
                     * 이 부분이 textview를 수정하는 부분입니다 !
                     */
                    Message message = new Message();
                    message.arg1 = (currentProgress / 60);
                    message.arg2 = (currentProgress - ((currentProgress / 60) * 60));
                    mHandler.sendMessage(message);
                }
            }

            @Override
            public void interrupt() {
                Log.d("interrupt", "issue");
            }
        }.start();
    }


    private void ImageSettings(Point size) {
        iv[0] = (ImageView) findViewById(R.id.iv_karina001);
        iv[1] = (ImageView) findViewById(R.id.iv_karina002);
        iv[2] = (ImageView) findViewById(R.id.iv_karina003);
        iv[3] = (ImageView) findViewById(R.id.iv_karina004);
        iv[4] = (ImageView) findViewById(R.id.iv_karina005);
        iv[5] = (ImageView) findViewById(R.id.iv_karina006);
        iv[6] = (ImageView) findViewById(R.id.iv_karina007);
        iv[7] = (ImageView) findViewById(R.id.iv_karina008);
        iv[8] = (ImageView) findViewById(R.id.iv_blank);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.karina_001)
                .override(size.x / 3, size.y / 3)
                .into(iv[0]);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.karina_002)
                .override(size.x / 3, size.y / 3)
                .into(iv[1]);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.karina_003)
                .override(size.x / 3, size.y / 3)
                .into(iv[2]);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.karina_004)
                .override(size.x / 3, size.y / 3)
                .into(iv[3]);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.karina_005)
                .override(size.x / 3, size.y / 3)
                .into(iv[4]);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.karina_006)
                .override(size.x / 3, size.y / 3)
                .into(iv[5]);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.karina_007)
                .override(size.x / 3, size.y / 3)
                .into(iv[6]);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.karina_008)
                .override(size.x / 3, size.y / 3)
                .into(iv[7]);

        Glide.with(PuzzleActivity.this)
                .load(R.drawable.blank)
                .override(size.x / 3, size.y / 3)
                .into(iv[8]);
    }

    public void SwapImage(int s_index, int b_index) {
        /***  blank( 빈 화면 ) imageView( 바꾸고자 하는 화면 )
         * 바꾸고자 하는 화면인 imageView의 각 좌표를 구하고, 각각 left, right, top, bottom에 담아놓은 후, imageView를 blank의 좌푯값으로 재구성한다.
         * 그리고나서, blank 또한 저장해놓은 left, right, top, bottom 값을 이용해 재구성한다.
         */

        Drawable temp = iv[s_index].getDrawable();
        iv[s_index].setImageDrawable(iv[b_index].getDrawable());
        iv[b_index].setImageDrawable(temp);
        blank_index = s_index;
        Log.d("blank_index", "" + b_index);
        Log.d("select_index", "" + s_index);
    }
}
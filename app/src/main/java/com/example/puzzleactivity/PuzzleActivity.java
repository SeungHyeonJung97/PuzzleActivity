package com.example.puzzleactivity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzleActivity extends AppCompatActivity {

    // 이미지를 담을 배열
    private ImageView iv[] = new ImageView[9];
    private Drawable correct_answer[] = new Drawable[9];
    private ProgressBar pb_timer;
    private Timer timer;
    private TimerTask timerTask;
    private Button btn_check;
    private TextView tv_timer;
    private boolean toggle_timer = false;
    private boolean answer = false;
    private Handler mHandler;
    private int count = 0;
    private int blank_index = 8;
    private DatabaseReference mDatabase;

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
        btn_check = (Button) findViewById(R.id.btn_check);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // ProgressBar 초기화
        initProgress();

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***
                 * 처음 시작 버튼이 눌리고 나서, btn_check가 "정답 확인" 으로 바뀌었으면 그 때부터 게임이 시작된걸로 판단해,
                 * 유저가 퍼즐을 맞추고 btn_check를 한번 더 누를 경우 iv[] 배열의 값과 정답을 저장해놓은 correct[] 배열의 값을 비교해
                 * 정답이라면 게임을 종료한다.
                 */
                if (btn_check.getText().equals("정답 확인")) {
                    boolean isCorrect = false;
                    for (int i = 0; i < iv.length; i++) {
                        if (iv[i].getDrawable().equals(correct_answer[i])) {
                            isCorrect = true;
                        } else {
                            isCorrect = false;
                            break;
                        }
                    }
                    if (isCorrect) {
                        answer = true;
                        saverScore(count, pb_timer.getProgress());
                        Toast.makeText(PuzzleActivity.this, "시도한 횟수" + count + "남은 시간" + pb_timer.getProgress() + "초", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PuzzleActivity.this, "오답입니다 !", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!toggle_timer) {
                    /***
                     * 이미지를 섞기 위한 if문
                     */

                    // 시작 버튼이 눌렸을 때 정답을 correct_answer 배열에 저장해놓는다.
                    for (int i = 0; i < iv.length; i++) {
                        correct_answer[i] = iv[i].getDrawable();
                    }

                    /***
                     * startTimerThread()를 통해 Timer를 작동시키고,
                     * Random 함수를 이용해 퍼즐을 섞는다. random의 범위는 0~3 까지이고, 0이 나오면 퍼즐을 오른쪽으로 옮기고,
                     * 1이 나오면 퍼즐을 위로 옮기고, 1이 나오면 퍼즐을 왼쪽으로 옮기고, 2가 나오면 퍼즐을 오른쪽으로 옮긴다.
                     * 이 때, puzzle의 index를 벗어나면 안되기 때문에 퍼즐의 범위를 벗어날 경우, 반대쪽으로 움직인다. (ex. 위로 옮겼을 때 index가 벗어나면 아래로)
                     * 또한, 행의 마지막 열에서 다음 행의 첫번째 행으로 이동하거나, 반대의 경우 둘의 차이값도 절대값으로 1이므로, 3으로 나누었을 때 나머지를 이용해 0혹은 2면
                     * 다른 방향으로 이동하도록 지정해준다.
                     */
                    startTimerThread();
                    for (int i = 0; i < 50; i++) {
                        Random random = new Random();
                        int rand_num = random.nextInt(4);
                        if (rand_num == 0)
                            rand_num = blank_index - 3 >= 0 ? blank_index - 3 : blank_index + 3;
                        else if (rand_num == 1)
                            rand_num = blank_index + 3 >= iv.length ? blank_index - 3 : blank_index + 3;
                        else if (rand_num == 2) {
                            if (blank_index % 3 == 0) {
                                rand_num = blank_index + 1;
                            } else if (blank_index % 3 == 2) {
                                rand_num = blank_index - 1;
                            } else {
                                rand_num = blank_index - 1 >= 0 ? blank_index - 1 : blank_index + 1;
                            }
                        } else if (rand_num == 3) {
                            if (blank_index % 3 == 0) {
                                rand_num = blank_index + 1;
                            } else if (blank_index % 3 == 2) {
                                rand_num = blank_index - 1;
                            } else {
                                rand_num = blank_index + 1 >= iv.length ? blank_index - 1 : blank_index + 1;
                            }
                        }
                        SwapImage(rand_num, blank_index);
                    }
                    btn_check.setText("정답 확인");
                }
                /***
                 * 이미지를 선택했을 경우, 위에서 언급한 것과 같이 한 칸을 넘게 움직이려고 하거나, 각 행의 마지막 열과 그 다음 행의 첫번째 열로 이동하려는 경우를 방지하고,
                 * 아니라면 이미지를 교체해주고 시도한 횟수를 저장한다.
                 */
                for (int i = 0; i < iv.length; i++) {
                    int select_index = i;
                    iv[i].setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View v) {
                            if (Math.abs(blank_index - select_index) == 1 || Math.abs(blank_index - select_index) == 3) {
                                if (!((select_index % 3 == 0 && blank_index % 3 == 2) || (select_index % 3 == 2 && blank_index % 3 == 0))) {

                                    SwapImage(select_index, blank_index);
                                    count++;
                                }
                            }
                        }
                    });
                }
            }
        });

        /***
         * handler를 통해 받은 message로 tv_timer의 값을 갱신해준다.
         */
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                tv_timer.setText(msg.arg1 + " : " + msg.arg2);
                return true;
            }
        });
    }

    private void saverScore(int count, int time) {
        Record record = new Record(count, time);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Log.d("date",""+(date.getMonth()+1)+"/"+date.getDay()+"/"+date.getHours()+":"+date.getMinutes());
        String key = "" + (date.getMonth()+1) + "" + date.getDay() + "" + date.getHours() + "" + date.getMinutes();

        mDatabase.child("Record").child(key).setValue(record)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("PuzzleActivity", "save Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("PuzzleActivity", "save Failed");
            }
        });
    }

    /***
     * ProgressBar의 시간은 5분으로 지정. 5분부터 점차 줄어들도록 설정한다.
     */
    private void initProgress() {
        pb_timer.setMax(300);
        pb_timer.setProgress(300);
    }

    /***
     * timer는 1초마다 값이 줄어들게 되고, timer의 값이 0이 되면 게임을 자동으로 종료한다.
     */
    private void startTimerThread() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                PuzzleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ((pb_timer.getProgress() != 0 && !answer)) {
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
                     * Message에 분과 초를 계산해 handler로 전송한다.
                     */
                    Message message = new Message();
                    message.arg1 = (currentProgress / 60);
                    message.arg2 = (currentProgress - ((currentProgress / 60) * 60));
                    mHandler.sendMessage(message);
                }
            }

            @Override
            public void interrupt() {
                Log.d("Thread", "interrupt");
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
        /**  b_index( 빈 화면의 index ) s_index( 바꾸고자 하는 화면의 index )
         *   전달 받은 index를 통해 iv[] 배열의 값을 교체해주고, blank_index의 위치를 바꿔준다.
         */

        Drawable temp = iv[s_index].getDrawable();
        iv[s_index].setImageDrawable(iv[b_index].getDrawable());
        iv[b_index].setImageDrawable(temp);
        blank_index = s_index;
    }
}
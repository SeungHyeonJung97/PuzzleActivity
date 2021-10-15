package com.example.puzzleactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    // 이미지를 담을 배열
    ImageView iv[] = new ImageView[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 기기마다 display의 size가 다를 수 있으므로, display의 size를 구한다.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // 구한 size를 ImageSettings에 넘겨주어, ImageSettings()에서 Puzzle의 Image를 Setting 한다.
        ImageSettings(size);


        for (int i = 0; i < iv.length; i++) {
            iv[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView) v;
                    ImageView blank = (ImageView) findViewById(R.id.iv_blank);
                    if (Vert(imageView, blank) || Hori(imageView, blank)) {
                        SwapImage(imageView, blank);
                    }
                }
            });
        }
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

        Glide.with(MainActivity.this)
                .load(R.drawable.karina_001)
                .override(size.x / 3, size.y / 3)
                .into(iv[0]);

        Glide.with(MainActivity.this)
                .load(R.drawable.karina_002)
                .override(size.x / 3, size.y / 3)
                .into(iv[1]);

        Glide.with(MainActivity.this)
                .load(R.drawable.karina_003)
                .override(size.x / 3, size.y / 3)
                .into(iv[2]);

        Glide.with(MainActivity.this)
                .load(R.drawable.karina_004)
                .override(size.x / 3, size.y / 3)
                .into(iv[3]);

        Glide.with(MainActivity.this)
                .load(R.drawable.karina_005)
                .override(size.x / 3, size.y / 3)
                .into(iv[4]);

        Glide.with(MainActivity.this)
                .load(R.drawable.karina_006)
                .override(size.x / 3, size.y / 3)
                .into(iv[5]);

        Glide.with(MainActivity.this)
                .load(R.drawable.karina_007)
                .override(size.x / 3, size.y / 3)
                .into(iv[6]);

        Glide.with(MainActivity.this)
                .load(R.drawable.karina_008)
                .override(size.x / 3, size.y / 3)
                .into(iv[7]);

        Glide.with(MainActivity.this)
                .load(R.drawable.blank)
                .override(size.x / 3, size.y / 3)
                .into(iv[8]);
    }

    public void SwapImage(ImageView imageView, ImageView blank) {
        /***  blank( 빈 화면 ) imageView( 바꾸고자 하는 화면 )
         * 바꾸고자 하는 화면인 imageView의 각 좌표를 구하고, 각각 left, right, top, bottom에 담아놓은 후, imageView를 blank의 좌푯값으로 재구성한다.
         * 그리고나서, blank 또한 저장해놓은 left, right, top, bottom 값을 이용해 재구성한다.
         */

        int Left = imageView.getLeft();
        int Right = imageView.getRight();
        int Top = imageView.getTop();
        int Bottom = imageView.getBottom();

        imageView.layout(blank.getLeft(), blank.getTop(), blank.getRight(), blank.getBottom());
        blank.layout(Left, Top, Right, Bottom);
    }

    public boolean Hori(ImageView imageView, ImageView blank) {
        /***
         * 가로로 이동할 때 사용하는 함수
         * imageView의 top 값과 blank의 top 값을 비교한다. ( 같은 행인지 ? )
         * imageView의 left 값과 blank의 left 값을 비교한 값이 1칸의 width값만큼을 넘어서지 않는지 검사한다. ( 1칸만 움직이는지 ? )
         */
        int cha_width = 0;
        cha_width = imageView.getLeft() - blank.getLeft();
        return (imageView.getTop() == blank.getTop() && (Math.abs(cha_width) < ( imageView.getWidth() + 10 )));
    }

    public boolean Vert(ImageView imageView, ImageView blank) {
        /***
         * 세로로 이동할 때 사용하는 함수
         * imageView의 left 값과 blank의 left 값을 비교한다. ( 같은 열인지 ? )
         * imageView의 left 값과 blank의 left 값을 비교한 값이 1칸의 height값만큼을 넘어서지 않는지 검사한다. ( 1칸만 움직이는지 ? )
         */
        int cha_height = 0;
        cha_height = imageView.getTop() - blank.getTop();
        return (imageView.getLeft() == blank.getLeft() && (Math.abs(cha_height) < ( imageView.getHeight() + 10 )));
    }
}
package daodemo.hc.com.pictureunion;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int COMPILE_PICTURE = 1;
    MyImageView iv_picture;
    ArrayList<String> pictureList = new ArrayList<>();
    String[] picString = {"R.drawable.heng1", "R.drawable.heng2", "R.drawable.shu1", "R.drawable.shu2"};
    int[] picId = {R.mipmap.heng1, R.mipmap.heng2, R.mipmap.shu1, R.mipmap.shu2};
RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();

    }

    private void initData() {
        pictureList.addAll(Arrays.asList(picString).subList(0, picId.length));
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    new ConmPilePic().run();
                    break;
            }

        }
    };

    private void initView() {
        iv_picture = findViewById(R.id.iv_picture);
        rl = findViewById(R.id.rl);
        Message msg = new Message();
        msg.what = COMPILE_PICTURE;
        mHandler.sendMessage(msg);
    }

    private Bitmap getNewBitmap() {
        Resources res = getResources();
        Bitmap a = BitmapFactory.decodeResource(res, picId[3]);
        Bitmap b = BitmapFactory.decodeResource(res, picId[0]);
        Bitmap newBitmap = creatNewBitMap(a, b);
        return newBitmap;
    }

    private Bitmap creatNewBitMap(Bitmap bmp1, Bitmap bmp2) {
        Bitmap retBmp;
        int width = bmp1.getWidth();
        int h2 = 0;
        if (bmp2.getWidth() != width) {
            h2 = bmp2.getHeight() * width / bmp2.getWidth();
            retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + h2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            Bitmap newSizeBmp2 = resizeBitmap(bmp2, width, h2);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(newSizeBmp2, 0, bmp1.getHeight(), null);
        } else {
            h2 = bmp1.getHeight() + bmp2.getHeight();
            retBmp = Bitmap.createBitmap(width, h2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        }
        //setPicSize(width,h2);
        return retBmp;
    }

    private void setPicSize(int width, int h2) {
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(width,h2);
        rl.setLayoutParams(rlParam);
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private class ConmPilePic implements  Runnable{
        @Override
        public void run() {
            Bitmap newBitmap = getNewBitmap();
            iv_picture.setImageBitmap(newBitmap);
        }
    }
}

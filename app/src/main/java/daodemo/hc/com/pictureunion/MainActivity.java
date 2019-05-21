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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int COMPILE_PICTURE = 1;
    MyImageView iv_picture;
    ArrayList<String> pictureList = new ArrayList<>();
    String[] picString = {"R.drawable.heng1", "R.drawable.heng2", "R.drawable.shu1", "R.drawable.shu2"};
    int[] picId = {R.mipmap.heng1, R.mipmap.heng2, R.mipmap.shu1, R.mipmap.shu2};

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
        Message msg = new Message();
        msg.what = COMPILE_PICTURE;
        mHandler.sendMessage(msg);
    }

    private Bitmap getNewBitmap() {
        Resources res = getResources();


//        Bitmap a = BitmapFactory.decodeResource(res, picId[1]);
//
//        Bitmap b = BitmapFactory.decodeResource(res, picId[0]);
//        Bitmap c = BitmapFactory.decodeResource(res, picId[2]);
//        Bitmap d = BitmapFactory.decodeResource(res, picId[3]);
        Bitmap a = getCompressBitmap(res, picId[1]);
        Bitmap b = getCompressBitmap(res, picId[0]);
        Bitmap c = getCompressBitmap(res, picId[2]);
        Bitmap d = getCompressBitmap(res, picId[3]);
//        BitmapFactory.Options opts=new BitmapFactory.Options();
//        opts.inTempStorage = new byte[100 * 1024];
//        opts.inPurgeable = true;
//        opts.inSampleSize = 4;

        Bitmap newBitmap = creatNewBitMap(a, b);
        Bitmap newBitmap1 = creatNewBitMap(c, d);
        Bitmap newBitmap2 = creatNewBitMap(newBitmap, newBitmap1);
        return newBitmap2;
    }

    private Bitmap getCompressBitmap(Resources res, int i) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Bitmap a = BitmapFactory.decodeResource(res, i);
//        a.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] bytes = baos.toByteArray();
//        return  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        options.inSampleSize = 2;
//        Bitmap a = BitmapFactory.decodeResource(res, i);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        a.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//        byte[] datas = baos.toByteArray();
//        return BitmapFactory.decodeByteArray(datas, 0, datas.length,options);
        Bitmap image = BitmapFactory.decodeResource(res, i);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩

    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private Bitmap creatNewBitMap(Bitmap bmp1, Bitmap bmp2) {
        Bitmap retBmp;
        int width = bmp1.getWidth();
        int h2 = 0;
        if (bmp2.getWidth() != width) {
            h2 = bmp2.getHeight() * width / bmp2.getWidth();
            retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + h2, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(retBmp);
            Bitmap newSizeBmp2 = resizeBitmap(bmp2, width, h2);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(newSizeBmp2, 0, bmp1.getHeight(), null);
        } else {
            h2 = bmp1.getHeight() + bmp2.getHeight();
            retBmp = Bitmap.createBitmap(width, h2, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(retBmp);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        }
        return retBmp;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private class ConmPilePic implements Runnable {
        @Override
        public void run() {
            Bitmap newBitmap = getNewBitmap();
            iv_picture.setImageBitmap(newBitmap);
        }
    }
}

package com.yavorivanov.asyncview2bitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new ASyncView2BitmapRunnable(new ASyncView2BitmapListener() {
            @Override
            public void onComplete(Bitmap bitmap) {
                final BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                final ImageView imageView = (ImageView) findViewById(R.id.image_view);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.setBackground(bitmapDrawable);
                } else {
                    imageView.setBackgroundDrawable(bitmapDrawable);
                }
            }
        }));
        thread.start();
    }

    private interface ASyncView2BitmapListener {

        void onComplete(Bitmap bitmap);

    }

    private final class ASyncView2BitmapRunnable implements Runnable {

        private final ASyncView2BitmapListener ASyncView2BitmapListener;

        public ASyncView2BitmapRunnable(ASyncView2BitmapListener ASyncView2BitmapListener) {
            this.ASyncView2BitmapListener = ASyncView2BitmapListener;
        }

        @Override
        public void run() {
            TextView textView = new TextView(getApplicationContext());
            textView.layout(0, 0, 170, 100);
            textView.setBackgroundColor(getResources().getColor(android.R.color.black));
            textView.setText("TEST");
            textView.setTextSize(24);
            textView.setTextColor(getResources().getColor(android.R.color.white));

            Bitmap bitmap = Bitmap.createBitmap(170, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            textView.draw(canvas);

            // simulate a lengthy operation
            // this will throw a CalledFromWrongThreadException
            SystemClock.sleep(TimeUnit.SECONDS.toMillis(7));
            ASyncView2BitmapListener.onComplete(bitmap);

        }
    }

}

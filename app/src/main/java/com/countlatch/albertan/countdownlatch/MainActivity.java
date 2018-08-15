package com.countlatch.albertan.countdownlatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;


/*
*

A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.

A CountDownLatch is initialized with a given count. The await methods block until the current count reaches zero due to invocations of the countDown() method,
after which all waiting threads are released and any subsequent invocations of await return immediately. This is a one-shot phenomenon -- the count cannot be reset.
 If you need a version that resets the count, consider using a CyclicBarrier.
* */

public class MainActivity extends AppCompatActivity{

    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Lion_waiting_in_Namibia.jpg/440px-Lion_waiting_in_Namibia.jpg";

    public byte[] imageData;


    private static CountDownLatch _latch;
    private static int N = 6;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            getImage();
        }catch (InterruptedException e){


        }

    }



    private void getImage()throws InterruptedException{

        int N =10;

        CountDownLatch doneSignal = new CountDownLatch(N);
        MyTaskExecutor e = new MyTaskExecutor();

        for (int i = 0; i < N; ++i) { // create and start threads
            e.execute(new WorkerRunnable(doneSignal, i));
        }

        doneSignal.await();           // wait for all to finish



        ImageView latchImage = findViewById(R.id.latch_image);

        Bitmap bmap  = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);


        latchImage.setImageBitmap(bmap);



    }

    class MyTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }


    class WorkerRunnable implements Runnable {
        private final CountDownLatch doneSignal;
        private final int i;
        WorkerRunnable(CountDownLatch doneSignal, int i) {
            this.doneSignal = doneSignal;
            this.i = i;
        }
        public void run() {
            try {
                doWork();
                doneSignal.countDown();
            } catch (Exception ex) {

                Log.e("", ex.toString());
            } // return;
        }

        void doWork() {


            try {
                URL url = new URL(imageUrl);
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }

                out.close();
                in.close();

                byte[] response = out.toByteArray();
                imageData = response;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }




}

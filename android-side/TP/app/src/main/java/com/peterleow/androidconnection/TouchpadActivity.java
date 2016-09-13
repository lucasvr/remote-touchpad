package com.peterleow.androidconnection;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TouchpadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchpad);

        Intent intent = getIntent();
        final String ipAddr = intent.getStringExtra("ipaddress");
        final int port= intent.getIntExtra("port", 5000);

        final GestureOverlayView touchpad = (GestureOverlayView)findViewById(R.id.touchPad);
        final Button leftClick = (Button)findViewById(R.id.leftClickButton);
        final Button rightClick = (Button)findViewById(R.id.rightClickButton);

        final int corePoolSize = 3;
        final int maximumPoolSize = 4;
        final long keepAliveTime = 1000;
        final TimeUnit unit = TimeUnit.SECONDS;
        final int workQueueDefaultCapacity = 1000;

        new Thread(){
            @Override
            public void run() {
                try {
                    final Socket socket = new Socket(ipAddr, port);
                    final Lock lock = new ReentrantLock();

                    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(workQueueDefaultCapacity);
                    final AbstractExecutorService threadPool = new ThreadPoolExecutor(
                            corePoolSize,
                            maximumPoolSize,
                            keepAliveTime,
                            unit,
                            workQueue);

                    leftClick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            threadPool.execute(new CommunicatorThread(socket, "left", lock));
                        }
                    });

                    rightClick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            threadPool.execute(new CommunicatorThread(socket, "right", lock));
                        }
                    });


                    touchpad.addOnGestureListener(new GestureOverlayView.OnGestureListener() {
                        @Override
                        public void onGestureStarted(GestureOverlayView overlay, MotionEvent ev) {
                            threadPool.execute(new CommunicatorThread(socket, "start", lock));
                        }

                        @Override
                        public void onGesture(GestureOverlayView overlay, MotionEvent ev) {
                            final int historySize = ev.getHistorySize();
                            final int pointerCount = ev.getPointerCount();
                            StringBuilder message = new StringBuilder();

                            for (int h = 0; h < historySize; h++) {
                                for (int p = 0; p < pointerCount; p++) {
                                    message.append(ev.getHistoricalX(p, h) + " " + ev.getHistoricalY(p, h) + " ");
                                }
                            }

                            threadPool.execute(new CommunicatorThread(socket, message.toString(), lock));
                        }

                        @Override
                        public void onGestureEnded(GestureOverlayView overlay, MotionEvent ev) {
                            threadPool.execute(new CommunicatorThread(socket, "end", lock));
                        }

                        @Override
                        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent ev) {
                            threadPool.execute(new CommunicatorThread(socket, "cancel", lock));
                        }
                    });
                } catch (IOException ioe) {
                    ioe.getStackTrace();
                }
            }
        }.start();
    }
}

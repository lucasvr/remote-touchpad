package com.peterleow.androidconnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Socket;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Communication extends Activity {
    short REDIRECTED_SERVERPORT = 5000;
    private Socket s = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communcation);

        final EditText msg = (EditText) findViewById(R.id.etMsg);
        Button send = (Button) findViewById(R.id.bSend);
        final TextView convo = (TextView) findViewById(R.id.tvConvo);
        convo.setText("");
        final TextView status = (TextView) findViewById(R.id.tvStatus);
        status.setText("");
        Button connect = (Button) findViewById(R.id.btnConnect);
        final EditText ipaddress = (EditText) findViewById(R.id.address);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Communication.this, TouchpadActivity.class);
                myIntent.putExtra("ipaddress", ipaddress.getText().toString());
                myIntent.putExtra("port", REDIRECTED_SERVERPORT);
                Communication.this.startActivity(myIntent);
            }
        });
    }
}


package com.peterleow.androidconnection;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.*;

public class Communcation extends Activity{
    private Socket s = null;
    short REDIRECTED_SERVERPORT = 5000;

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
                status.setText("Estabilishing the connection to " + ipaddress.getText().toString());
                new Thread() {
                    public void run() {
                        try {
                            s = new Socket(InetAddress.getByName(ipaddress.getText().toString()), REDIRECTED_SERVERPORT);
                            status.setText("Established connection..");
                        } catch (IOException e) {
                            status.setText("Failed the connection" + e.getMessage());
                        }
                    }
                }.start();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString();
                new Thread() {
                    public void run() {
                        PrintWriter outp = null;
                        BufferedReader inp = null;
                        String serverMsg = null;

                        try {
                            outp = new PrintWriter(s.getOutputStream(), true);
                            inp = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        } catch (IOException e) {
                            status.setText("Couldn't initate the buffers ");
                        }

                        outp.write("Salut in pulea mea !");
                    }
                }.start();
            }
        });
    }
}


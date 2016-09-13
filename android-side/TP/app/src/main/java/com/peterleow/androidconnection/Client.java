package com.peterleow.androidconnection;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by arotaru on 8/2/16.
 */
public class Client extends AsyncTask<Void, Void, Void> {
    String dstAddress;
    int dstPort;
    String response = "";
    TextView status;

    Client(String addr, int port, TextView textResponse) {
        dstAddress = addr;
        dstPort = port;
        this.status = textResponse;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            PrintStream printer = new PrintStream(socket.getOutputStream(), true);
            //BufferedWriter cout = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            printer.write("Salut!".getBytes());
            printer.write("Please work!!!!!!".getBytes());
            //status.setText("Finished");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}

package com.peterleow.androidconnection;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.locks.Lock;

/**
 * Created by arotaru on 9/10/16.
 */
public class CommunicatorThread implements Runnable {
    protected Socket socket;
    protected String messageToSend;
    protected Lock lock;

    public CommunicatorThread(Socket s, String m, Lock l) {
        socket = s;
        messageToSend = m;
        lock = l;
    }

    public CommunicatorThread(Socket s, String m) {
        this(s, m, null);
    }



    @Override
    public void run() {
        try {
            PrintStream printer = new PrintStream(socket.getOutputStream(), true);
            if (lock != null) {
                lock.lock();
                printer.write(messageToSend.getBytes());
                lock.unlock();
            } else {
                printer.write(messageToSend.getBytes());
            }


        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

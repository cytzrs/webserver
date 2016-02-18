package com.servertest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by cytzr on 2016/1/23.
 */
public class WebServer {

    public void serverStart(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                new Processor(socket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] arguments) {
        int port = -1;
        if(arguments.length > 0) {
            port = Integer.parseInt(arguments[0]);
        }
        new WebServer().serverStart(port);
    }
}

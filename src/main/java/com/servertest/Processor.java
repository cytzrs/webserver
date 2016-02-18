package com.servertest;

import java.io.*;
import java.net.Socket;

/**
 * Created by cytzr on 2016/1/23.
 */
public class Processor extends Thread {

    private Socket socket;
    private InputStream in;
    private PrintStream out;
    private final static String WEB_ROOT = "C:\\Users\\cytzr\\IdeaProjects\\webserver\\src\\main\\resources";

    public Processor(Socket socket) {
        this.socket = socket;
        try {
            in = socket.getInputStream();
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String parse(InputStream in) {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        String fileName = "";
        try {
            String httpMessage = buffer.readLine();
            String[] context = httpMessage.split(" ");
            if(context.length != 3) {
                sendErrorMessage(400, "Client query error!");
                return null;
            }
            System.out.println("Code: " + context[0] + " requested file: " + context[1] + " Http version: " + context[2]);
            fileName = context[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public void sendFile(String fileName) {
        File file = new File(Processor.WEB_ROOT + fileName);
        if(!file.exists()) {
            sendErrorMessage(404, "File not found!");
            return ;
        }
        try {
            InputStream in = new FileInputStream(file);
            byte[] content = new byte[(int) file.length()];
            in.read(content);

            out.println("HTTP/1.1 200 queryfile");
            out.println("content-length:" + content.length);
            out.println();
            out.write(content);
            out.flush();
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendErrorMessage(int errorCode, String errorMessage) {
        System.out.println("Error Code: " + errorCode + " \nErrorMessage: " + errorMessage);

        out.println("HTTP/1.1 " + errorCode + " " + errorMessage);
        out.println("content-type: text/html");
        out.println();

        out.println("<html>");
        out.println("<title>Error Message: ");
        out.println("</title>");
        out.println("<script type='text/javascript'>");
        out.println("function test(value) { alert(value); }");
        out.println("</script>");
        out.println("<body>");
        out.println("This is just a webserver test.");
        out.println("<input type='button' value='add' onclick='test(123)' />");
        out.println("</body>");
        out.println("</html>");

        out.flush();
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String fileName = parse(in);
        sendFile(fileName);
    }
}

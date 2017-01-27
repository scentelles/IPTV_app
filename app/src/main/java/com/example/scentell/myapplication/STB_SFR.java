package com.example.scentell.myapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by scentell on 27/01/2017.
 */







public class STB_SFR {
    public static final T_RETURN = 12;
    public static final T_1 = 19;
    public static final T_2 = 20;
    public static final T_3 = 21;
    public static final T_4 = 22;
    public static final T_5 = 23;
    public static final T_6 = 24;
    public static final T_7 = 25;
    public static final T_8 = 26;
    public static final T_9 = 27;
    public static final T_0 = 28;
    public static final T_UP = 7;
    public static final T_DOWN = 11;
    public static final T_RIGHT = 10;
    public static final T_LEFT = 8;
    public static final T_REC = 17;
    public static final T_FORWARD = 15;
    public static final T_PLAY = 14;
    public static final T_PAUSE = 14;
    public static final T_REWIND = 13;
    public static final T_OK = 9;
    public static final T_SFR = 6;
    public static final T_ON = 5;
    public static final T_OFF = 5;
    public static final T_VPLUS = 18;
    public static final T_VMOINS = 16;


    private Socket socketSend;
    private Socket socketReceive;
    private static final port_STB_distant = 50000;
    private static final port_STB_local = 50001;
    private static final String STB_IP = "192.168.1.70";
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    public boolean sendKeypress(int STBKeyCode) {
        try {
            socketSend = new Socket(STB_IP, port_STB_distant);
            socketReceive = new Socket(STB_IP, port_STB_local);
            outputStream = new DataOutputStream(socketSend.getOutputStream());
            inputStream = new DataInputStream(socketReceive.getInputStream());

        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }
        String command = STBKeyCode + " 1\n";

try{
        outputStream.writeBytes(command);
        inputStream.read();
        String responseLine = inputStream.toString();

        outputStream.close();
        inputStream.close();
        socketSend.close();
        socketReceive.close();
}catch(IOException e){
    System.err.println("IO exception");

        }


    }






}

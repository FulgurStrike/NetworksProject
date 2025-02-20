package sendrecieve;

import java.net.*;
import java.io.*;
import java.util.Arrays;

import CMPC3M06.AudioPlayer;

public class Receiver implements Runnable {

    static DatagramSocket receivingSocket;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        int port = 55555;
        boolean running = true;
        AudioPlayer player = null;

        try {
            player = new AudioPlayer();
        }catch(Exception e)
        {
            return;
        }
        try {
            receivingSocket = new DatagramSocket(port);
            receivingSocket.setSoTimeout(500);
        } catch (SocketException e) {
            System.out.println("ERROR Receiver: Could not open UDP packet to send from");
            e.printStackTrace();
            System.exit(0);
        }

        byte[] buffer =new byte[512];

        while(running){
            DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);

            try {
                receivingSocket.receive(packet);
            }catch(SocketTimeoutException e){
                System.out.println(".");
            }catch(Exception e) {
                System.out.println("ERROR : something else");
                e.printStackTrace();
            }
            try{
                if (packet.getLength() > 0){
                    byte[] audioBlock = Arrays.copyOf(packet.getData(),packet.getLength());

                    player.playBlock(audioBlock);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }



//        for (int i = 0; i < 999; i++) {
//            try {
//                byte[] buffer = new byte[200];
//                DatagramPacket packet = new DatagramPacket(buffer, 0, 80);
//
//                receivingSocket.setSoTimeout(30);
//
//                try {
//                    receivingSocket.receive(packet);
//                    String str = new String(buffer);
//                    System.out.println(str);
//                } catch (SocketTimeoutException e) {
//                    System.out.println(".");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                System.out.println("ERROR Receiver: Some IO error occured");
//                e.printStackTrace();
//
//            }
//        }
        receivingSocket.close();
    }
}

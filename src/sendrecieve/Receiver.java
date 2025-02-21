package sendrecieve;

import java.net.*;
import java.io.*;
import CMPC3M06.AudioPlayer;

import java.nio.ByteBuffer;
import java.util.Iterator;


public class Receiver implements Runnable {

    static DatagramSocket receivingSocket;
    private AudioPlayer player;

    public void audioPlayer() throws Exception{
       player = new AudioPlayer();
    }
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        int port = 55555;

        try {
            receivingSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("ERROR Receiver: Could not open UDP packet to send from");
            e.printStackTrace();
            System.exit(0);
        }

        boolean running = true;

        while(running){
            try{
                byte[] buffer = new byte[514];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);


                receivingSocket.receive(packet);

                ByteBuffer byteBuffer = ByteBuffer.wrap(packet.getData());
                short sequenceNumber = byteBuffer.getShort();

                byte[] audioBlock = new byte[512];
                byteBuffer.get(2, audioBlock);
                if (packet.getLength() > 0){
                    player.playBlock(audioBlock);
                    System.out.println("received audioblock " + sequenceNumber + " of size of : " + audioBlock.length + " bytes");
                }
            } catch(IOException e){
                System.out.println("ERROR : Receiver : Some random IO error has occurred");
                e.printStackTrace();
            }
        }
        receivingSocket.close();
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
//        receivingSocket.close();
    }
}

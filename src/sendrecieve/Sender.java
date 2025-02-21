package sendrecieve;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioRecorder;

import javax.xml.crypto.Data;

public class Sender implements Runnable {
    static DatagramSocket sendingSocket;
    private AudioRecorder recorder;

    public void audioRecorder()throws Exception{
        recorder = new AudioRecorder();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        int port = 55555;
        InetAddress clientIP = null;
        try {
            //temp localhost
            clientIP =InetAddress.getByName("localhost");
        } catch(UnknownHostException e) {
            System.err.println("ERROR : TextSender : could not find client IP ");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            sendingSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("ERROR Sender 1: Could not open UDP packet to send from");
            e.printStackTrace();
            System.exit(0);
        }

        int runTime = 10;

        for (int i=0;i<Math.ceil(runTime/0.032);i++){
            try{
                byte[] audioBlock = recorder.getBlock();

                // Allocates a 514 byte long byte buffer
                ByteBuffer buffer = ByteBuffer.allocate(514);
                if(audioBlock != null){

                    // First 2 bytes of the packet will be a short representing the sequence number
                    buffer.putShort((short) i);

                    // Remaining bits will be the audio block
                    buffer.put(audioBlock);
                    DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.capacity(), clientIP,port);
                    sendingSocket.send(packet);
                    System.out.println("audio packet sent, size :" + buffer.capacity() + " bytes");
                }
            } catch(IOException e){
                System.out.println("Error : TextSender: Some random IO error has occurred");
                e.printStackTrace();
            }
        }
        recorder.close();
        sendingSocket.close();
        System.out.println("Sending Socket is now closed");
       // for (int i = 0; i < 999; i++) {
       //     try {
       //         String str = "Packet " + i;

       //         byte[] buffer = str.getBytes();

       //         DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, port);

       //         sendingSocket.send(packet);
       //     }catch (IOException e) {
       //         System.out.println("ERROR Sender 2: Some random IO error occured");
       //         e.printStackTrace();
       //     }
       // }
       // sendingSocket.close();
    }
}

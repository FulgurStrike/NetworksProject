package sendrecieve;

import java.net.*;
import java.io.*;


public class Receiver implements Runnable {

    static DatagramSocket receivingSocket;

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

        int count = 1;
        for (int i = 0; i < 999; i++) {
            try {
                byte[] buffer = new byte[200];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 80);

                receivingSocket.setSoTimeout(30);

                try {
                    receivingSocket.receive(packet);
                    String str = new String(buffer);
                    System.out.println(str);
                    count++;
                    if (count == 999) {

                    }
                } catch (SocketTimeoutException e) {
                    System.out.println(".");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("ERROR Receiver: Some IO error occured");
                e.printStackTrace();

            }
        }

        receivingSocket.close();
    }
}

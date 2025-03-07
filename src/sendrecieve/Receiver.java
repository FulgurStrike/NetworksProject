package sendrecieve;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import CMPC3M06.AudioPlayer;

import java.nio.ByteBuffer;
import java.util.Random;


public class Receiver implements Runnable {


    private static final int MODULUS =1000007;
    private static final int S_KEY = 11111;
    static DatagramSocket receivingSocket;
    private AudioPlayer player;

    private final String p = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74"
            + "020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374"
            + "FE1356D6D51C245E485B576625E7EC6F44C42E9A63A36210000000000090563E0"
            + "B7F1A5A6F9A6C5E939A6E317955DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4"
            + "187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A41"
            + "87B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A418"
            + "7B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187"
            + "B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B"
            + "8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8"
            + "AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8A"
            + "C3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC"
            + "3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3"
            + "B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B"
            + "5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5"
            + "DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5D"
            + "CB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DC";
    private final BigInteger g = BigInteger.valueOf(2);
    private final Random rand = new Random(System.currentTimeMillis());
    private final BigInteger y = new BigInteger(2048, rand);
    private final int authHeader = 768452;

    public static long calcAuthenticator(byte[] audioBlock){
        int checkSum =0;
        for(byte b: audioBlock){
            checkSum +=b;
        }
        checkSum +=S_KEY;
        checkSum %= MODULUS;
        return Integer.toUnsignedLong(checkSum);
    }

    public void audioPlayer() throws Exception{
       player = new AudioPlayer();
    }
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    // Key exchange method using TCP sockets. Receiver acts as the client
    public BigInteger keyExchange() {
        BigInteger K = BigInteger.valueOf(0);
        try {
            // Listens for incoming server requests on the specified port
            ServerSocket serverSocket = new ServerSocket(5000);

            // Accepts and opens a connection with the server
            Socket socket = serverSocket.accept();

            // OutputStream receives data from Server output stream
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Input stream sends data to Server output stream
            DataInputStream input = new DataInputStream(socket.getInputStream());

            BigInteger P = new BigInteger(p, 16);

            BigInteger R = g.modPow(y, P);

            // Sends the value of R back to the server
            output.writeUTF(R.toString());
            output.flush();

            // Reads the value of X sent from the server
            BigInteger R2 = new BigInteger(input.readUTF());

            K = R2.modPow(y, P).subtract(BigInteger.valueOf(1));

            return K;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return K;
    }

    public byte[] enforceCorrectBlockSize(byte[] block, int size) {
        byte[] paddedBlock = new byte[size];
        System.arraycopy(block, 0, paddedBlock, size - block.length, block.length);

        return paddedBlock;
    }

    public byte[] decryption(BigInteger key, byte[] audioBlock) {
        BigInteger audioBlockInt = new BigInteger(audioBlock);
        BigInteger decryptedBlock = audioBlockInt.xor(key);
        byte[] decryptedBlockArray = decryptedBlock.toByteArray();

        if (decryptedBlockArray.length < 512) {
            BigInteger paddedBlock = new BigInteger(enforceCorrectBlockSize(decryptedBlockArray, 512));

            return enforceCorrectBlockSize(paddedBlock.toByteArray(), 512);
        }

        return decryptedBlock.toByteArray();
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

        BigInteger symKey = keyExchange();;

        boolean running = true;

        while (running){
            try {
                byte[] buffer = new byte[526];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);


                receivingSocket.receive(packet);

                // Wraps the packet into a ByteBuffer for more functionality
                ByteBuffer byteBuffer = ByteBuffer.wrap(packet.getData());

                // Transfers the first 2 bytes in the byte buffer which will be the sequence number
                short sequenceNumber = byteBuffer.getShort();
                long receiveAuthenticator = byteBuffer.getLong();


                byte[] audioBlock = new byte[512];
                // Retrieves the rest of packet bytes which is the entire audio block
                byteBuffer.get(audioBlock);

                long expectedAuthenticator = calcAuthenticator(audioBlock);
                long receivedAuthenticator = receiveAuthenticator & 0xFFFFFFFFFFFFFFFFL; // Make sure it is unsigned


                System.out.println("Received packet with sequence number: " + sequenceNumber);
                System.out.println("Received checksum: " + receivedAuthenticator);
                System.out.println("Expected checksum: " + expectedAuthenticator);
                if (receivedAuthenticator == expectedAuthenticator) {
                    System.out.println("valid message, playing audioBlock");


                    byte[] decryptedBlock = decryption(symKey, audioBlock);

                    if (packet.getLength() > 0) {
                        player.playBlock(decryptedBlock);
                        System.out.println("received audioblock " + sequenceNumber + " of size of : " + audioBlock.length + " bytes");
                    }
                }else{
                    System.out.println("Message has been tampered with or isn't valid. Disregarding the packet "+ sequenceNumber + ":"+receivedAuthenticator + ":"+expectedAuthenticator);
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

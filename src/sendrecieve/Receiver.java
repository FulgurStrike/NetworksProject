package sendrecieve;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import CMPC3M06.AudioPlayer;
import senderbased.Interleaver;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import uk.ac.uea.cmp.voip.DatagramSocket2;
public class Receiver implements Runnable {


    private static final int MODULUS =65536;
    private static final int S_KEY = 11111;
    static DatagramSocket2 receivingSocket;
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

    public static long calcAuthenticator(byte[] audioBlock) {
        int checkSum = 0;
        for (byte b : audioBlock) {
            checkSum += Byte.toUnsignedInt(b);  // Summing up byte values
        }
        checkSum += S_KEY;  // Adding the secret key
        checkSum %= MODULUS;  // Modulo to ensure the checksum fits within the modulus
        return Integer.toUnsignedLong(checkSum);  // Convert to unsigned long
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
            receivingSocket = new DatagramSocket2(port);
        } catch (SocketException e) {
            System.out.println("ERROR Receiver: Could not open UDP packet to send from");
            e.printStackTrace();
            System.exit(0);
        }

        BigInteger symKey = keyExchange();

        short lastReceivedSeqNum = -1;

        boolean running = true;

        while (running){
            try {
                DatagramPacket packet;
                ArrayList<ArrayList<DatagramPacket>> packetMatrix = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    ArrayList<DatagramPacket> packets = new ArrayList<>();
                    for (int j = 0; j < 3; j++) {
                        byte[] buffer = new byte[526];
                        packet = new DatagramPacket(buffer, 0, buffer.length);

                        receivingSocket.receive(packet);
                        packets.add(packet);
                    }
                    packetMatrix.add(packets);
                }

                packetMatrix = Interleaver.deinterleave(packetMatrix);

                for (ArrayList<DatagramPacket> packets : packetMatrix) {
                    for (DatagramPacket p : packets) {
                        // Wraps the packet into a ByteBuffer for more functionality
                        ByteBuffer byteBuffer = ByteBuffer.wrap(p.getData());
                        byteBuffer.getInt();
                        byteBuffer.getShort();
                        // Transfers the first 2 bytes in the byte buffer which will be the sequence number
                        long receiveAuthenticator = byteBuffer.getLong();
                        // Check if the value is negative and correct it (if needed)
                        if (receiveAuthenticator < 0) {
                            receiveAuthenticator = receiveAuthenticator+ (1L << 64); // Convert to positive unsigned equivalent
                        }

                // Wraps the packet into a ByteBuffer for more functionality
                ByteBuffer byteBuffer = ByteBuffer.wrap(packet.getData());
                byteBuffer.getInt();
                short sequenceNumber = byteBuffer.getShort();
                // Transfers the first 2 bytes in the byte buffer which will be the sequence number
                long receiveAuthenticator = byteBuffer.getLong();
                // Check if the value is negative and correct it (if needed)
                if (receiveAuthenticator < 0) {
                    receiveAuthenticator = receiveAuthenticator+ (1L << 64); // Convert to positive unsigned equivalent
                }

                byte[] audioBlock = new byte[512];
                // Retrieves the rest of packet bytes which is the entire audio block
                byteBuffer.get(audioBlock);


                        byte[] audioBlock = new byte[512];
                        // Retrieves the rest of packet bytes which is the entire audio block
                        byteBuffer.get(audioBlock);

                        long expectedAuthenticator = calcAuthenticator(audioBlock);
                        long receivedAuthenticator = receiveAuthenticator & 0xFFFFFFFFFFFFFFFFL; // Make sure it is unsigned


                        //System.out.println("Received packet with sequence number: " + sequenceNumber);
                        if (receivedAuthenticator == expectedAuthenticator) {
                            System.out.println("valid message, playing audioBlock");

                //System.out.println("Received packet with sequence number: " + sequenceNumber);
                if (receivedAuthenticator == expectedAuthenticator) {

                        if(sequenceNumber == lastReceivedSeqNum + 1) {
                            lastReceivedSeqNum = sequenceNumber;  // Update last received sequence number
                        } else {
                            // Packet loss occurred, handle missing packets
                            System.out.println("Packet loss occurred. Splicing");

                            // Loop to find the next available packet (if any)
                            while (sequenceNumber != lastReceivedSeqNum + 1) {
                                System.out.println("Missing packet with sequence number: " + (lastReceivedSeqNum + 1));
                                receivingSocket.receive(packet);  // Receive next packet

                                byteBuffer = ByteBuffer.wrap(packet.getData());
                                byteBuffer.getInt();  // Skip header
                                sequenceNumber = byteBuffer.getShort();  // Get new sequence number
                                receiveAuthenticator = byteBuffer.getLong();  // Get new authenticator

                                if (receiveAuthenticator < 0) {
                                    receiveAuthenticator = receiveAuthenticator + (1L << 64);  // Handle negative authenticator
                                }

                                byteBuffer.get(audioBlock);  // Get the audio block data
                                expectedAuthenticator = calcAuthenticator(audioBlock);
                                receivedAuthenticator = receiveAuthenticator & 0xFFFFFFFFFFFFFFFFL;  // Ensure it's unsigned

                                // If packet is valid and has the correct sequence number, play it
                                if (receivedAuthenticator == expectedAuthenticator && sequenceNumber == lastReceivedSeqNum + 1) {
                                    byte[] decryptedBlock = decryption(symKey, audioBlock);
                                    if (packet.getLength() > 0) {
                                        player.playBlock(decryptedBlock);
                                        System.out.println("received audioblock " + sequenceNumber + " of size of : " + audioBlock.length + " bytes");
                                    }
                                    lastReceivedSeqNum = sequenceNumber;  // Update last received sequence number
                                    break;  // Exit loop after finding the valid packet
                                }
                                lastReceivedSeqNum = sequenceNumber;
                            }
                        }
                        byte[] decryptedBlock = decryption(symKey, audioBlock);
                    if (packet.getLength() > 0) {
                        player.playBlock(decryptedBlock);
                        System.out.println("received audioblock " + sequenceNumber + " of size of : " + audioBlock.length + " bytes");


                            byte[] decryptedBlock = decryption(symKey, audioBlock);

                            if (p.getLength() > 0) {
                                player.playBlock(decryptedBlock);
                                //System.out.println("received audioblock " + sequenceNumber + " of size of : " + audioBlock.length + " bytes");
                            }
                        }else{
                            //System.out.println("Message has been tampered with or isn't valid. Disregarding the packet "+ sequenceNumber + ":"+receivedAuthenticator + ":"+expectedAuthenticator);
                        }
                    }
                }

            } catch(IOException e){
                System.out.println("ERROR : Receiver : Some random IO error has occurred");
                e.printStackTrace();
            }
        }
        receivingSocket.close();
    }
}

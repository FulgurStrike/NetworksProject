package senderbased;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class Interleaver {
    public static ArrayList<ArrayList<DatagramPacket>> interleave(ArrayList<ArrayList<DatagramPacket>> matrix) {

        int n = matrix.size();

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                DatagramPacket tmp = matrix.get(i).get(j);
                matrix.get(i).set(j, matrix.get(j).get(i));
                matrix.get(j).set(i, tmp);
            }
        }

        for (ArrayList b : matrix) {
            Collections.reverse(b);
        }

        return matrix;
    }

    public static ArrayList<ArrayList<DatagramPacket>> deinterleave(ArrayList<ArrayList<DatagramPacket>> matrix) {

        int n = matrix.size();

        for (ArrayList b : matrix) {
            Collections.reverse(b);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                DatagramPacket tmp = matrix.get(i).get(j);
                matrix.get(i).set(j, matrix.get(j).get(i));
                matrix.get(j).set(i, tmp);
            }
        }

        return matrix;
    }
}


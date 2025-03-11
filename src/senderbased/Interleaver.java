
package senderbased;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Interleaver {

    private int chunkSize; // Size of each chunk for splicing
    private int rows;      // Number of rows used for interleaving

    // Constructor to initialize chunk size and rows for interleaving
    public Interleaver(int chunkSize, int rows) {
        this.chunkSize = chunkSize;
        this.rows = rows;
    }

    // Interleave function takes a block of data (audio block) and applies interleaving
    public byte[] interleave(byte[] audioData) {
        // Split the data into chunks (splitting is done at the receiver side)
        List<byte[]> chunks = splice(audioData);
        int numChunks = chunks.size();

        // Calculate number of columns (equal to the chunk size)
        int columns = chunks.get(0).length;

        // Initialize the matrix to hold rows and columns for interleaving
        byte[][] matrix = new byte[rows][columns];

        // Fill the matrix with chunks (rows of data)
        for (int i = 0; i < numChunks; i++) {
            byte[] chunk = chunks.get(i);
            for (int j = 0; j < chunk.length; j++) {
                int row = i % rows;
                matrix[row][j] = chunk[j];
            }
        }

        // Create the interleaved data by reading the matrix column-wise
        byte[] interleaved = new byte[audioData.length];
        int index = 0;
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                interleaved[index++] = matrix[row][col];
            }
        }

        return interleaved;
    }

    // Helper method to splice the data (split into chunks)
    private List<byte[]> splice(byte[] audioData) {
        List<byte[]> chunks = new ArrayList<>();
        int dataSize = audioData.length;

        for (int i = 0; i < dataSize; i += chunkSize) {
            int remaining = Math.min(chunkSize, dataSize - i);
            byte[] chunk = new byte[remaining];
            System.arraycopy(audioData, i, chunk, 0, remaining);
            chunks.add(chunk);
        }

        return chunks;
    }

    // De-interleave function to reverse the interleaving (for receiver side)
    public byte[] deinterleave(byte[] interleavedData) {
        // Calculate number of chunks and columns
        int numChunks = interleavedData.length / chunkSize;
        int columns = chunkSize;

        // Reconstruct the matrix from the interleaved data
        byte[][] matrix = new byte[rows][columns];
        int index = 0;
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                matrix[row][col] = interleavedData[index++];
            }
        }

        // Return the matrix
        byte[] interleaved = new byte[interleavedData.length];
        int tempIndex = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                interleaved[tempIndex] = matrix[i][j];
                tempIndex++;
            }
        }
        return interleaved;
    }
}

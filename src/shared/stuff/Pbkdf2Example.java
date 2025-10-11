package shared.stuff;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A quick example implementation of a compute engine to be used in the API.
 * This algorithm is a simplified version of PBKDF2.
 */
public class Pbkdf2Example {

  /**
   * Computes a simplified PBKDF2-style derived integer. This method repeatedly
   * hashes the input integer using SHA-256 in a nested loop and reduces the
   * result into the range 1..Integer.MAX_VALUE-1.
   *
   * @param input
   *          a positive integer input greater than 0
   * @return a derived integer such that 1 <= result < Integer.MAX_VALUE
   * @throws NoSuchAlgorithmException
   *           if SHA-256 is not supported
   */
  public static int compute(int input) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");

    // Only need 4 bytes since we will reduce to Integer range
    byte[] value = ByteBuffer.allocate(4).putInt(input).array();

    int outerIterations = 1000;
    int innerIterations = 10;

    for (int i = 0; i < outerIterations; i++) {
      for (int j = 0; j < innerIterations; j++) {
        md.reset();
        md.update(value);
        value = md.digest();
      }
    }

    // Take first 4 bytes as int
    int result = ByteBuffer.wrap(value, 0, 4).getInt();

    // Ensure result is in 1..Integer.MAX_VALUE-1
    return Math.floorMod(result, Integer.MAX_VALUE - 1) + 1;
  }

  /**
   * Main method for testing the simplified PBKDF2 computation.
   *
   * @param args
   *          command-line arguments (not used)
   * @throws NoSuchAlgorithmException
   *           if SHA-256 is not supported
   */
  public static void main(String[] args) throws NoSuchAlgorithmException {

    int input = 1;
    System.out.println(Pbkdf2.compute(input));

  }
}

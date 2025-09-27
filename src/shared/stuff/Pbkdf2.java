package shared.stuff;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A simplified PBKDF2-style computation
 * 
 * in nested loops it repeatedly hashes a value (beginning with the initial
 * input) and at the end ensures the result is between 1 and Integer.MAX_VALUE-1
 */
public class Pbkdf2 {

  private static final int OUTER_ITERATIONS = 1000;
  private static final int INNER_ITERATIONS = 10;

  /**
   * Computes a modified PBKDF2-style integer value
   * 
   * @param input
   *          a positive integer greater than 0
   * 
   * @return a derived integer in the range 1..Integer.MAX_VALUE-1
   * 
   * @throws NoSuchAlgorithmException
   *           if SHA-256 is not supported
   */
  public static int compute(int input) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] value = intToBytes(input);

    for (int i = 0; i < OUTER_ITERATIONS; i++) {
      for (int j = 0; j < INNER_ITERATIONS; j++) {
        md.reset();
        md.update(value);
        value = md.digest();
      }
    }

    return normalizeResult(value);
  }

  /**
   * Converts an integer into a 4-byte array
   */
  private static byte[] intToBytes(int input) {
    return ByteBuffer.allocate(4).putInt(input).array();
  }

  /**
   * Ensures the first 4 bytes of the hash are between 1 and
   * Integer.MAX_VALUE-1.
   */
  private static int normalizeResult(byte[] value) {
    int result = ByteBuffer.wrap(value, 0, 4).getInt();
    return Math.floorMod(result, Integer.MAX_VALUE - 1) + 1;
  }
}

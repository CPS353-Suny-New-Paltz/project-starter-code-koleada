package shared.stuff;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Fast PBKDF2-style computation
 *
 * Performance Improvements: 1. Removed redundant md.reset() in inner loop
 * (reset once per outer iteration) 2. Reuse single MessageDigest instance per
 * compute
 */
public class FastPbkdf2 {

  private static final int OUTER_ITERATIONS = 1000;
  private static final int INNER_ITERATIONS = 10;

  public static BigInteger compute(BigInteger input)
      throws NoSuchAlgorithmException {

    byte[] value = bigIntToBytes(input);

    MessageDigest md = MessageDigest.getInstance("SHA-256");

    for (int i = 0; i < OUTER_ITERATIONS; i++) {

      for (int j = 0; j < INNER_ITERATIONS; j++) {
        // completely removed redundant .reset() method upon learning more about
        // MessageDigest, i leanred its alreaady handeled by .digest
        md.update(value);
        value = md.digest();
      }
    }

    return normalizeResult(value);
  }

  private static byte[] bigIntToBytes(BigInteger input) {
    // preserve original behavior (fixed size), but now for BigInteger
    byte[] bytes = input.toByteArray();
    if (bytes.length >= 32) {
      return bytes;
    }

    // left-pad to 32 bytes for SHA-256 input consistency
    byte[] padded = new byte[32];
    System.arraycopy(bytes, 0, padded, 32 - bytes.length, bytes.length);
    return padded;
  }

  private static BigInteger normalizeResult(byte[] value) {
    // turn entire digest into BigInteger
    BigInteger out = new BigInteger(1, value);

    // keep the same "range normalization" idea but using BigInteger
    BigInteger mod = BigInteger.valueOf(Integer.MAX_VALUE - 1L);
    BigInteger normalized = out.mod(mod).add(BigInteger.ONE);

    return normalized;
  }
}

package shared.stuff;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class InitDatabase {

  private static final String DB_URL = "jdbc:sqlite:auth.db"; // create auth.db
                                                              // in project root
  private static final String ADMIN_USERNAME = "admin";
  private static final String ADMIN_PASSWORD = "admin"; // will be hashed

  public static void main(String[] args) {
    try (Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement()) {

      System.out.println("Connected to SQLite DB.");

      // Create users table
      String createTableSQL = """
          CREATE TABLE IF NOT EXISTS users (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              username TEXT NOT NULL UNIQUE,
              password_hash TEXT NOT NULL
          );
          """;
      stmt.execute(createTableSQL);
      System.out.println("Users table created.");

      // Insert admin account
      String insertSQL = "INSERT OR IGNORE INTO users(username, password_hash) VALUES(?, ?)";
      try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
        pstmt.setString(1, ADMIN_USERNAME);
        pstmt.setString(2, hashPassword(ADMIN_PASSWORD));
        pstmt.executeUpdate();
        System.out.println("Admin account created with username 'admin'.");
      }

    } catch (SQLException | NoSuchAlgorithmException
        | InvalidKeySpecException e) {
      e.printStackTrace();
    }
  }

  // PBKDF2 password hashing
  public static String hashPassword(String password)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    int iterations = 65536;
    int keyLength = 256;
    char[] passwordChars = password.toCharArray();
    byte[] salt = "fixedSalt1234567".getBytes(); // for simplicity in this test
                                                 // DB; can randomize per user
                                                 // later

    PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, iterations,
        keyLength);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] hash = skf.generateSecret(spec).getEncoded();

    return Base64.getEncoder().encodeToString(hash);
  }
}

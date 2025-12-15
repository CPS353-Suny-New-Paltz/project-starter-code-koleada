package shared.stuff;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTest {
  public static void main(String[] args) {
    // SQLite connection string
    String url = "jdbc:sqlite:test.db"; // this will create test.db in your
                                        // project folder

    // SQL statements
    String createTableSQL = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT, password TEXT);";
    String insertSQL = "INSERT INTO users(username, password) VALUES('alice', 'password123');";
    String selectSQL = "SELECT id, username, password FROM users;";

    try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement()) {

      System.out.println("Connected to SQLite successfully.");

      stmt.execute(createTableSQL);
      stmt.execute(insertSQL);

      ResultSet rs = stmt.executeQuery(selectSQL);
      while (rs.next()) {
        System.out.println("User: " + rs.getString("username") + ", Password: "
            + rs.getString("password"));
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}

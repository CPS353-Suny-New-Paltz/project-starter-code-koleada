package network.api;

import java.util.Objects;

/**
 * Simple login request outline
 */
public final class LoginRequest {
  private final String username;
  private final String hashedPassword;

  public LoginRequest(String username, String password) {
    this.username = Objects.requireNonNull(username);
    this.hashedPassword = Objects.requireNonNull(password);
  }

  public String getUsername() {
    return username;
  }
  public String getHashedPassword() {
    return hashedPassword;
  }
}

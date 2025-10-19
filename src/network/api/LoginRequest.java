package network.api;

import java.util.Objects;

/**
 * Simple login request outline
 */
public final class LoginRequest {
  private final String username;
  private final String hashedPassword;

  public LoginRequest(String username, String password) {

    // validate params are not null
    this.username = Objects.requireNonNull(username, "Username cannot be null");
    this.hashedPassword = Objects.requireNonNull(password,
        "Password cannot be null");
  }

  public String getUsername() {
    return username;
  }
  public String getHashedPassword() {
    return hashedPassword;
  }
}

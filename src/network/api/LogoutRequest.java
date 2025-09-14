package network.api;

import java.util.Objects;

/**
 * Logout request requires the session token.
 */
public final class LogoutRequest {
  private final String sessionToken;

  public LogoutRequest(String sessionToken) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
  }

  public String getSessionToken() {
    return sessionToken;
  }
}

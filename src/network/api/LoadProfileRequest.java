package network.api;

import java.util.Objects;

/**
 * Load user's profile using session token
 */
public final class LoadProfileRequest {
  private final String sessionToken;

  public LoadProfileRequest(String sessionToken) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
  }

  public String getSessionToken() {
    return sessionToken;
  }
}

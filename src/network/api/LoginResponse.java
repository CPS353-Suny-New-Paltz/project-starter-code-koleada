package network.api;

import java.util.Objects;

import shared.stuff.ApiStatus;

/**
 * Outline for the response returned on successful login
 */
public final class LoginResponse {
  private final String sessionToken;
  private final String userId;
  private final ApiStatus status;

  public LoginResponse(String sessionToken, String userId, ApiStatus status) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
    this.userId = Objects.requireNonNull(userId);
    this.status = status;
  }

  public String getSessionToken() {
    return sessionToken;
  }
  public String getUserId() {
    return userId;
  }

  /**
   * Returns true is status is SUCCESS
   * 
   * @return true or false
   */
  public boolean success() {
    return status == ApiStatus.SUCCESS;
  }
}

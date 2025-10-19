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
    // can be null if there is an error when logging in
    this.userId = userId;
    this.status = status;
  }

  public String getSessionToken() {
    return sessionToken;
  }
  public String getUserId() {
    return userId;
  }
  public ApiStatus getStatus() {

    return status;
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

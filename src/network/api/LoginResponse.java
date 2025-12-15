package network.api;

import shared.stuff.ApiStatus;

/**
 * Outline for the response returned on successful login
 */
public final class LoginResponse {
  private final String sessionToken;
  private final String userId;
  private final ApiStatus status;
  private final String message;

  public LoginResponse(String sessionToken, String userId, ApiStatus status) {
    this.sessionToken = sessionToken;
    // can be null if there is an error when logging in
    this.userId = userId;
    this.status = status;
    this.message = null;
  }

  public LoginResponse(String sessionToken, String userId, ApiStatus status,
      String message) {
    this.sessionToken = sessionToken;
    // can be null if there is an error when logging in
    this.userId = userId;
    this.status = status;
    this.message = message;
  }

  public String getSessionToken() {
    return sessionToken;
  }
  public String getUserId() {
    return userId;
  }
  public String getMessage() {
    return message;
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

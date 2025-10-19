package network.api;

import shared.stuff.ApiStatus;

/**
 * Simple logout acknowledgement.
 */
public final class LogoutResponse {

  private final ApiStatus status;
  private final String message;

  public LogoutResponse(ApiStatus status) {
    this.status = status;
    this.message = null;
  }

  public LogoutResponse(ApiStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public boolean success() {
    return this.status == ApiStatus.SUCCESS;
  }

  public ApiStatus getStatus() {
    return status;
  }
}

package process.api;

import shared.stuff.ApiStatus;

public class StoreResponse<T> {

  private final ApiStatus status;
  private final String message;

  public StoreResponse(ApiStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public StoreResponse(ApiStatus status) {
    this.status = status;
    this.message = null;
  }

  public boolean success() {
    return status == ApiStatus.SUCCESS;
  }

  public ApiStatus getStatus() {
    return this.status;
  }
  public String getMessage() {
    return this.message;
  }
}

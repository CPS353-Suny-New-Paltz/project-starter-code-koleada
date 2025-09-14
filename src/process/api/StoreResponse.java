package process.api;

import shared.stuff.ApiStatus;

public class StoreResponse<T> {

  ApiStatus status;
  String message;

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
}

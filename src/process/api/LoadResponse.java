package process.api;

import shared.stuff.ApiStatus;

public class LoadResponse<T> {

  ApiStatus status;
  DataBatch<T> data;
  String message;

  public LoadResponse(ApiStatus status, DataBatch<T> data, String message) {
    this.status = status;
    this.data = data;
    this.message = message;
  }

  public boolean success() {
    return status == ApiStatus.SUCCESS;
  }
}

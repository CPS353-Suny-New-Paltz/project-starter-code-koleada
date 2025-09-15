package process.api;

import shared.stuff.ApiStatus;

public class LoadResponse<T> {

  private final ApiStatus status;
  private final DataBatch<T> data;
  private final String message;

  public LoadResponse(ApiStatus status, DataBatch<T> data, String message) {
    this.status = status;
    this.data = data;
    this.message = message;
  }

  public boolean success() {
    return status == ApiStatus.SUCCESS;
  }
  public DataBatch<T> getData() {
    return this.data;
  }
  public String getMessage() {
    return this.message;
  }
  public ApiStatus getStatus() {
    return this.status;
  }
}

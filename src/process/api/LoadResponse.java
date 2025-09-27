package process.api;

import java.util.List;

import shared.stuff.ApiStatus;

public class LoadResponse {

  private final ApiStatus status;
  private final List data;
  private final String message;

  public LoadResponse(ApiStatus status, List data, String message) {
    this.status = status;
    this.data = data;
    this.message = message;
  }

  public boolean success() {
    return status == ApiStatus.SUCCESS;
  }
  public List getData() {
    return this.data;
  }
  public String getMessage() {
    return this.message;
  }
  public ApiStatus getStatus() {
    return this.status;
  }
}

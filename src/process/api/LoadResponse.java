package process.api;

import java.util.List;

import shared.stuff.ApiStatus;
import shared.stuff.DataBatch;

public class LoadResponse {

  private final ApiStatus status;
  private final DataBatch<List> data;
  private final String message;

  public LoadResponse(ApiStatus status, DataBatch<List> data, String message) {
    this.status = status;
    this.data = data;
    this.message = message;
  }

  public boolean success() {
    return status == ApiStatus.SUCCESS;
  }
  public DataBatch<List> getData() {
    return this.data;
  }
  public String getMessage() {
    return this.message;
  }
  public ApiStatus getStatus() {
    return this.status;
  }
}

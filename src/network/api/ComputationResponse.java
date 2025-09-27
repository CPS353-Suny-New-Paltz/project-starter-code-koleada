package network.api;

import java.util.List;

import shared.stuff.ApiStatus;
import shared.stuff.DataBatch;

/**
 * 
 * Response from a computation, containing results and status.
 */
public class ComputationResponse {
  private final ApiStatus status;
  private final DataBatch<List> results;
  private final String message;

  public ComputationResponse(ApiStatus status, DataBatch<List> resultBatch,
      String message) {
    this.status = status;
    this.results = resultBatch;
    this.message = message;
  }

  public ApiStatus getStatus() {
    return status;
  }

  public DataBatch<List> getResults() {
    return results;
  }

  public String getMessage() {
    return message;
  }
}
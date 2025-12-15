package network.api;

import java.math.BigInteger;
import java.util.List;

import shared.stuff.ApiStatus;

/**
 * 
 * Response from a computation, containing results and status.
 */
public class ComputationResponse {
  private final ApiStatus status;
  private final List<BigInteger> results;
  private final String message;

  public ComputationResponse(ApiStatus status, List<BigInteger> resultBatch,
      String message) {
    this.status = status;
    this.results = resultBatch;
    this.message = message;
  }

  public ApiStatus getStatus() {
    return status;
  }

  public List<BigInteger> getResults() {
    return results;
  }

  public String getMessage() {
    return message;
  }
}

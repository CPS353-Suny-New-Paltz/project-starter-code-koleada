package process.api;

import java.math.BigInteger;
import java.util.List;

import network.api.Delimiter;
import shared.stuff.ApiStatus;

/**
 * Response from a load operation
 */
public class LoadResponse {

  private final ApiStatus status;
  private final List<BigInteger> payload;
  private final Delimiter delimiter;
  private final String message;

  public LoadResponse(ApiStatus status, List<BigInteger> data,
      Delimiter delimiter, String message) {
    this.status = status;
    this.payload = data;
    this.delimiter = delimiter;
    this.message = message;
  }

  public boolean success() {
    return status == ApiStatus.SUCCESS;
  }

  public List<BigInteger> getPayload() {
    return this.payload;
  }

  public String getMessage() {
    return this.message;
  }

  public ApiStatus getStatus() {
    return this.status;
  }

  public Delimiter getDelimiter() {
    return delimiter;
  }
}

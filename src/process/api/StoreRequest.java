package process.api;

import java.math.BigInteger;
import java.util.List;

import network.api.Delimiter;
import shared.stuff.Resource;

/**
 * This class creates a request to tell the data storage system to store some
 * data
 */
public class StoreRequest {

  private final Resource destination;
  private final List<BigInteger> payload;
  private final Delimiter delimiter;

  public StoreRequest(Resource destination, List<BigInteger> data,
      Delimiter delimiter) {
    if (destination == null || data == null || delimiter == null) {
      throw new IllegalArgumentException(
          "destination, data, or delimiter is null");
    }
    this.destination = destination;
    this.payload = data;
    this.delimiter = delimiter;
  }

  public Resource getDestination() {
    return destination;
  }

  public List<BigInteger> getPayload() {
    return payload;
  }

  public Delimiter getDelimiter() {
    return delimiter;
  }
}

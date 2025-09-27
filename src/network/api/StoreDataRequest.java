package network.api;

import java.util.List;
import java.util.Objects;

import shared.stuff.Resource;

/**
 * Basic request to store bytes of data in a given Resource
 */
public final class StoreDataRequest {
  private final String sessionToken;
  private final Resource destination;
  private final List payload;
  private final Delimiter delimiter;

  public StoreDataRequest(String sessionToken, Resource destination,
      List payload, Delimiter delimiter) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
    this.destination = Objects.requireNonNull(destination);
    this.payload = Objects.requireNonNull(payload);
    this.delimiter = delimiter;
  }
  public StoreDataRequest(String sessionToken, Resource destination,
      List dataBatch) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
    this.destination = Objects.requireNonNull(destination);
    this.payload = Objects.requireNonNull(dataBatch);
    this.delimiter = Delimiter.defaultDelimiter();
  }

  public String getSessionToken() {
    return sessionToken;
  }
  public Resource getDestination() {
    return destination;
  }
  public List getPayload() {
    return payload;
  }
  public Delimiter getDelimiter() {
    return delimiter;
  }
}

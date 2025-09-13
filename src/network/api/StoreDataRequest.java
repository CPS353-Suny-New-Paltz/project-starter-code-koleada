package network.api;

import java.util.Objects;

import shared.stuff.Resource;

/**
 * Basic request to store bytes of data in a given Resource
 */
public final class StoreDataRequest {
  private final String sessionToken;
  private final Resource destination;
  private final byte[] payload;
  private final Delimiter delimiter;

  public StoreDataRequest(String sessionToken, Resource destination,
      byte[] payload, Delimiter delimiter) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
    this.destination = Objects.requireNonNull(destination);
    this.payload = Objects.requireNonNull(payload);
    this.delimiter = delimiter;
  }
  public StoreDataRequest(String sessionToken, Resource destination,
      byte[] payload) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
    this.destination = Objects.requireNonNull(destination);
    this.payload = Objects.requireNonNull(payload);
    this.delimiter = Delimiter.defaultDelimiter();
  }

  public String getSessionToken() {
    return sessionToken;
  }
  public Resource getDestination() {
    return destination;
  }
  public byte[] getPayload() {
    return payload;
  }
  public Delimiter getDelimiter() {
    return delimiter;
  }
}

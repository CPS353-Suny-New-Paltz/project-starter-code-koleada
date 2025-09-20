package network.api;

import java.util.Objects;

import shared.stuff.Resource;

/**
 * Basic request to load(read) bytes of data from a provided Resource
 */
public final class LoadDataRequest {
  private final String sessionToken;
  private final Resource source;
  private final Delimiter delimiter;

  public LoadDataRequest(String sessionToken, Resource source,
      Delimiter delimiter) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
    this.source = Objects.requireNonNull(source);
    this.delimiter = delimiter;
  }
  public LoadDataRequest(String sessionToken, Resource source) {
    this.sessionToken = Objects.requireNonNull(sessionToken);
    this.source = Objects.requireNonNull(source);
    this.delimiter = Delimiter.defaultDelimiter();
  }

  public String getSessionToken() {
    return sessionToken;
  }
  public Resource getSource() {
    return source;
  }
  public Delimiter getDelimiter() {
    return delimiter;
  }
}

package process.api;

import network.api.Delimiter;
import shared.stuff.Resource;

/**
 * This class creates a request to load data from a given resource
 */
public class LoadRequest {

  private final Resource source;
  private final Delimiter delimiter;

  public LoadRequest(Resource source, Delimiter delimiter) {
    this.source = source;
    this.delimiter = delimiter;
  }

  public Resource getSource() {
    return this.source;
  }
  public Delimiter getDelimiter() {
    return this.delimiter;
  }
}

package process.api;

import java.util.List;

import network.api.Delimiter;
import shared.stuff.Resource;

/**
 * This class creates a request to tell the data storage system to store some
 * data
 */
public class StoreRequest {

  private final Resource destination;
  private final List data;
  private final Delimiter delimiter;

  public StoreRequest(Resource destination, List data, Delimiter delimiter) {
    this.destination = destination;
    this.data = data;
    this.delimiter = delimiter;
  }

  public Resource getDestination() {
    return destination;
  }
  public List getData() {
    return data;
  }
  public Delimiter getDelimiter() {
    return delimiter;
  }
}

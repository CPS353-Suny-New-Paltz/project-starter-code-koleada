package process.api;

import shared.stuff.Resource;

/**
 * This class creates a request to load data from a given resource
 */
public class LoadRequest {

  private final Resource source;

  public LoadRequest(Resource source) {
    this.source = source;
  }

  public Resource getSource() {
    return this.source;
  }
}

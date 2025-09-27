package api.implementations;

import java.util.List;

import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.ProcessApi;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.DataBatch;
import shared.stuff.Resource;

/**
 * Implementation of the ProcessApi interface
 */
public class ProcessAPI implements ProcessApi {

  // need to know which resource for storing and loading data
  private Resource resource;

  public ProcessAPI(Resource resource) {
    this.resource = resource;
  }

  @Override
  public LoadResponse load(LoadRequest request) {
    // empty implementation returning failure and no data
    return new LoadResponse(ApiStatus.ERROR, new DataBatch<List>(),
        "Not Implemented");
  }

  @Override
  public StoreResponse store(StoreRequest request) {
    return new StoreResponse(ApiStatus.ERROR, "Not Implemented");
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

}

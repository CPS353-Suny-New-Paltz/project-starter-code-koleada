package api.implementations;

import java.util.ArrayList;

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
public class ProcessAPI<T> implements ProcessApi {

  // need to know which resource for storing and loading data
  private Resource resource;

  private final DataBatch buffer; // A buffer if needed

  public ProcessAPI(Resource resource) {
    this.resource = resource;

    // example buffer
    this.buffer = new DataBatch<ArrayList<T>>(new ArrayList<T>());
  }

  @Override
  public LoadResponse<T> load(LoadRequest request) {
    // empty implementation returning failure and no data
    return new LoadResponse<T>(ApiStatus.ERROR, buffer, "Not Implemented");
  }

  @Override
  public StoreResponse store(StoreRequest request) {
    return new StoreResponse(ApiStatus.ERROR, "Not Implemented");
  }

  public Resource getResource() {
    return resource;
  }
  public DataBatch getBuffer() {
    return buffer;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

}

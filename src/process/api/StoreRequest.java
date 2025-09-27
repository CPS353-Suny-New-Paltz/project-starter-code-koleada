package process.api;

import java.util.List;

import shared.stuff.DataBatch;
import shared.stuff.Resource;

/**
 * This class creates a request to tell the data storage system to store some
 * data
 * 
 * @param <T>
 *          Generic for the DataBatch
 */
public class StoreRequest {

  private final Resource destination;
  private final DataBatch<List> data;

  public StoreRequest(Resource destination, DataBatch<List> data) {
    this.destination = destination;
    this.data = data;
  }

  public Resource getDestination() {
    return destination;
  }
  public DataBatch<List> getData() {
    return data;
  }
}

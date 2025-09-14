package process.api;

import shared.stuff.Resource;

/**
 * This class creates a request to tell the data storage system to store some
 * data
 * 
 * @param <T>
 *          Generic for the DataBatch
 */
public class StoreRequest<T> {

  Resource destination;
  DataBatch<T> data;

  public StoreRequest(Resource destination, DataBatch<T> data) {
    this.destination = destination;
    this.data = data;
  }
}

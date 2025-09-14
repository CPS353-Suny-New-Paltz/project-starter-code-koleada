package process.api;

import project.annotations.ProcessAPI;

/**
 * API between the data storage system and the Job I/O section of the compute
 * engine Responsible for reading from and writing to user-specified input and
 * output sources
 */
@ProcessAPI
public interface ProcessApi {

  /**
   * Loads data from the storage system for the compute engine.
   *
   * @param request
   *          the load request
   * @return a load response containing the retrieved data or error details
   */
  LoadResponse load(LoadRequest request);

  /**
   * Stores data produced by the compute engine into the storage system.
   *
   * @param request
   *          the store request
   * @return a store response containing status of the operation
   */
  StoreResponse store(StoreRequest request);

}
package tests;

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
 * Test-only in-memory implementation of ProcessApi that works specifically with
 * ArrayList<Integer> as the input data format
 */
public class InMemoryProcessAPI implements ProcessApi {

  TestInputConfig inputConfig;
  TestOutputConfig outputConfig;
  Resource resource;

  public InMemoryProcessAPI(Resource resource, TestInputConfig inputConfig,
      TestOutputConfig outputConfig) {
    this.resource = resource;
    this.inputConfig = inputConfig;
    this.outputConfig = outputConfig;
  }

  /**
   * Read the in-memory input list and return it in a LoadResponse as
   * DataBatch<ArrayList<Integer>>
   */
  @Override
  public LoadResponse<ArrayList<Integer>> load(LoadRequest request) {
    ArrayList<Integer> copy = new ArrayList<>(inputConfig.inputData);
    DataBatch<ArrayList<Integer>> batch = new DataBatch<>(copy);
    return new LoadResponse<>(ApiStatus.SUCCESS, batch, "Loaded successfully");
  }

  /**
   * Store the provided DataBatch contents to the in-memory output
   */
  @Override
  public StoreResponse store(StoreRequest request) {
    DataBatch<ArrayList<Integer>> batch = (DataBatch<ArrayList<Integer>>) request
        .getData();
    if (batch != null) {
      ArrayList<Integer> list = batch.getDataSource();
      for (Integer i : list) {
        if (i == null) {
          outputConfig.writeData("null");
        } else {
          outputConfig.writeData(i.toString());
        }
      }
    } else {
      outputConfig.writeData("null");
    }
    return new StoreResponse(ApiStatus.SUCCESS, "Stored successfully");
  }

}

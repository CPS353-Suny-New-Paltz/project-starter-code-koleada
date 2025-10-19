package tests;

import java.util.ArrayList;
import java.util.List;

import network.api.Delimiter;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.ProcessApi;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * A simple in-memory implementation of NetworkApi for testing, using
 * TestInputConfig and TestOutputConfig for storage
 */
public class InMemoryDataStore implements ProcessApi {

  TestOutputConfig outputConfig;
  Delimiter defaultDelimiter = Delimiter.defaultDelimiter();
  Resource<List<Integer>> resource;

  public InMemoryDataStore(TestInputConfig input, TestOutputConfig output) {
    this.outputConfig = output;
    this.resource = new Resource(ResourceType.CUSTOM, input.inputData);
  }

  /**
   * This function stores date in the TestOutputConfig specified in the users
   * request. It splits the user provided data based on the specified delimiter
   * or the default delimiter then adds it to TestOutputConfig as individual
   * elements
   */
  public StoreResponse storeData(StoreRequest req) {

    Delimiter delimiter;
    if (req.getDelimiter() == null) {
      delimiter = defaultDelimiter;
    } else {
      delimiter = req.getDelimiter();
    }

    List<?> payloadStr = req.getPayload();

    // Clear old output and write new data
    outputConfig.setOutputData(new java.util.ArrayList<>());

    List<String> out = new TestOutputConfig().getOutputData();

    return new StoreResponse(ApiStatus.SUCCESS, resource,
        "Data stored successfully");
  }

  /**
   * This function reads data, in this case from TestInputConfig which is stored
   * in the resource field. It then separates each using the specified delimiter
   * and returns it to the user as an array of bytes
   */
  public LoadResponse loadData(LoadRequest req) {

    Delimiter delimiter;
    if (req.getDelimiter() == null) {
      delimiter = defaultDelimiter;
    } else {
      delimiter = req.getDelimiter();
    }

    // our resources 'data' is the inputConfig
    List input = resource.getData();

    // read data from InputConfig, append the delimiter
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < input.size(); i++) {
      builder.append(input.get(i));
      if (i < input.size() - 1) {
        builder.append(delimiter.getValue());
      }
    }

    // create byte array out of our string builder

    List<String> data = new ArrayList<String>();
    data.add(builder.toString());

    // return the byte[], no resource is needed because we would read from user
    // supplied resource
    return new LoadResponse(ApiStatus.SUCCESS, data, delimiter,
        "Data loaded successfully");
  }

  @Override
  public LoadResponse load(LoadRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StoreResponse store(StoreRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

}

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
  Resource<TestInputConfig> resource;

  public InMemoryDataStore(TestInputConfig input, TestOutputConfig output) {
    this.outputConfig = output;
    this.resource = new Resource(ResourceType.CUSTOM, input);
  }

  @Override
  public LoadResponse load(LoadRequest req) {

    Delimiter delimiter;
    if (req.getDelimiter() == null) {
      delimiter = defaultDelimiter;
    } else {
      delimiter = req.getDelimiter();
    }

    // our resources 'data' is the inputConfig
    TestInputConfig input = resource.getData();

    // read data from InputConfig, append the delimiter
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < input.inputData.size(); i++) {
      builder.append(input.inputData.get(i));
      if (i < input.inputData.size() - 1) {
        builder.append(delimiter.getValue());
      }
    }

    // create byte array out of our string builder

    List<String> data = new ArrayList<String>();
    data.add(builder.toString());

    // return the byte[], no resource is needed because we would read from user
    // supplied resource
    return new LoadResponse(ApiStatus.SUCCESS, data,
        "Data loaded successfully");
  }

  @Override
  public StoreResponse store(StoreRequest req) {
    Delimiter delimiter;
    if (req.getDelimiter() == null) {
      delimiter = defaultDelimiter;
    } else {
      delimiter = req.getDelimiter();
    }

    List payloadStr = req.getData();

    // Clear old output and write new data
    outputConfig.setOutputData(new java.util.ArrayList<>());

    List out = new TestOutputConfig().getOutputData();

    return new StoreResponse(ApiStatus.SUCCESS, "Data stored successfully");

  }

}

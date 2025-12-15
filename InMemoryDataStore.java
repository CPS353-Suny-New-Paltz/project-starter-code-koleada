package tests;

import java.math.BigInteger;
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
 * A simple in-memory implementation of ProcessApi for testing. Works with
 * BigInteger-based Resources and Load/Store responses.
 */
public class InMemoryDataStore implements ProcessApi {

  TestOutputConfig outputConfig;
  Delimiter defaultDelimiter = Delimiter.defaultDelimiter();
  Resource resource;

  public InMemoryDataStore(TestInputConfig input, TestOutputConfig output) {
    this.outputConfig = output;
    // input.inputData is List<BigInteger>
    this.resource = new Resource(ResourceType.CUSTOM, input.inputData);
  }

  public StoreResponse storeData(StoreRequest req) {
    Delimiter delimiter = req.getDelimiter() == null
        ? defaultDelimiter
        : req.getDelimiter();

    List<BigInteger> payload = req.getPayload();
    if (payload == null) {
      payload = new ArrayList<>();
    }

    // Store data in the outputConfig as strings (for backward compatibility)
    List<String> outputStrings = new ArrayList<>();
    for (BigInteger b : payload) {
      outputStrings.add(b.toString());
    }
    outputConfig.setOutputData(outputStrings);

    // Also update internal Resource for consistency
    resource.setData(new ArrayList<>(payload));

    return new StoreResponse(ApiStatus.SUCCESS, resource,
        "Data stored successfully");
  }

  public LoadResponse loadData(LoadRequest req) {
    Delimiter delimiter = req.getDelimiter() == null
        ? defaultDelimiter
        : req.getDelimiter();

    List<BigInteger> input = resource.getData();
    if (input == null) {
      input = new ArrayList<>();
    }

    return new LoadResponse(ApiStatus.SUCCESS, new ArrayList<>(input),
        delimiter, "Data loaded successfully");
  }

  @Override
  public LoadResponse load(LoadRequest request) {
    return loadData(request);
  }

  @Override
  public StoreResponse store(StoreRequest request) {
    return storeData(request);
  }
}
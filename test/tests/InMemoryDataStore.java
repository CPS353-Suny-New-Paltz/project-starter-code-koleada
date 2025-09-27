package tests;

import java.util.UUID;

import network.api.Delimiter;
import network.api.LoadDataRequest;
import network.api.LoadDataResponse;
import network.api.LoadProfileRequest;
import network.api.LoadProfileResponse;
import network.api.LoginRequest;
import network.api.LoginResponse;
import network.api.LogoutRequest;
import network.api.LogoutResponse;
import network.api.NetworkApi;
import network.api.StoreDataRequest;
import network.api.StoreDataResponse;
import shared.stuff.ApiStatus;
import shared.stuff.DataBatch;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * A simple in-memory implementation of NetworkApi for testing, using
 * TestInputConfig and TestOutputConfig for storage
 */
public class InMemoryDataStore implements NetworkApi {

  TestOutputConfig outputConfig;
  Delimiter defaultDelimiter = Delimiter.defaultDelimiter();
  Resource<TestInputConfig> resource;

  public InMemoryDataStore(TestInputConfig input, TestOutputConfig output) {
    this.outputConfig = output;
    this.resource = new Resource(ResourceType.CUSTOM, input);
  }

  /**
   * This function stores date in the TestOutputConfig specified in the users
   * request. It splits the user provided data based on the specified delimiter
   * or the default delimiter then adds it to TestOutputConfig as individual
   * elements
   */
  @Override
  public StoreDataResponse storeData(StoreDataRequest req) {

    Delimiter delimiter;
    if (req.getDelimiter() == null) {
      delimiter = defaultDelimiter;
    } else {
      delimiter = req.getDelimiter();
    }

    String payloadStr = new String(req.getPayload());
    String[] values = payloadStr.split(delimiter.getValue());

    // Clear old output and write new data
    outputConfig.setOutputData(new java.util.ArrayList<>());
    for (String v : values) {
      outputConfig.writeData(v.trim());
    }

    return new StoreDataResponse(ApiStatus.SUCCESS,
        new Resource(ResourceType.CUSTOM, new DataBatch<>(outputConfig)),
        "Data stored successfully");
  }

  /**
   * This function reads data, in this case from TestInputConfig which is stored
   * in the resource field. It then separates each using the specified delimiter
   * and returns it to the user as an array of bytes
   */
  @Override
  public LoadDataResponse loadData(LoadDataRequest req) {

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
    byte[] data = builder.toString().getBytes();

    // return the byte[], no resource is needed because we would read from user
    // supplied resource
    return new LoadDataResponse(ApiStatus.SUCCESS, data, delimiter,
        "Data loaded successfully");
  }

  // Unchanged NetworkApi methods
  @Override
  public LoginResponse login(LoginRequest req) {
    return new LoginResponse(UUID.randomUUID().toString(),
        UUID.randomUUID().toString(), ApiStatus.ERROR);
  }

  @Override
  public LogoutResponse logout(LogoutRequest req) {
    return new LogoutResponse(ApiStatus.ERROR);
  }

  @Override
  public LoadProfileResponse loadProfile(LoadProfileRequest req) {
    return new LoadProfileResponse(UUID.randomUUID().toString(), "testUser",
        ApiStatus.ERROR);
  }
}

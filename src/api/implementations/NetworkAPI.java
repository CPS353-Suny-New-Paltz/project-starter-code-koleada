package api.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import network.api.ComputationRequest;
import network.api.ComputationResponse;
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
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.DataBatch;
import shared.stuff.Resource;

/**
 * Implementation of the NetworkApi interface
 */
public class NetworkAPI implements NetworkApi {

  // will need to communicate with the ProcessAPI to pass instructions to the
  // Data Storage System
  private ProcessAPI readWrite;

  // Will also need to talk to the computation section to perform calculations,
  // get session keys, etc
  private ConceptualAPI compute;

  // default delimiter if user does not provide one
  private Delimiter defaultDelimiter = Delimiter.COMMA;

  private Resource resource;

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

  @Override
  public StoreDataResponse storeData(StoreDataRequest req) {

    StoreResponse resp = readWrite.store(new StoreRequest(req.getDestination(),
        new DataBatch(req.getPayload())));
    return new StoreDataResponse(resp.getStatus(), req.getDestination(),
        resp.getMessage());

  }

  @Override
  public LoadDataResponse loadData(LoadDataRequest req) {
    LoadResponse resp = readWrite.load(new LoadRequest(req.getSource()));

    return new LoadDataResponse(resp.getStatus(), resp.getData(),
        defaultDelimiter, resp.getMessage());
  }

  /**
   * does the computation: read input, run compute, write output, return results
   * as byte[]
   */
  public ComputationResponse compute(ComputationRequest request) {
    try {
      // Load integers from input resource
      LoadResponse loadResp = readWrite
          .load(new LoadRequest(request.getInputResource()));
      if (loadResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new DataBatch<List>(),
            "Failed to load input data");
      }

      // expect the input integers to be in a DataBatch<List<Integer>>, provided
      // in LoadResponse
      List<Integer> inputs = (List<Integer>) loadResp.getData().getDataSource();
      List<Integer> results = new ArrayList<>();

      // Run computation for each input
      for (int value : inputs) {
        results.add(compute.performComputation(value).getResult());
      }

      // Store results in output resource
      DataBatch<List> resultBatch = new DataBatch<>(results);
      StoreResponse storeResp = readWrite
          .store(new StoreRequest(request.getOutputResource(), resultBatch));

      if (storeResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new DataBatch<List>(),
            "Failed to store results");
      }

      // return ComputationResponse to the user, results stored in a
      // DataBatch<List>
      return new ComputationResponse(ApiStatus.SUCCESS, resultBatch,
          "Computation completed");

    } catch (Exception e) {
      return new ComputationResponse(ApiStatus.ERROR, new DataBatch<List>(),
          "Error: " + e.getMessage());
    }
  }

  public ProcessAPI getReadWrite() {
    return readWrite;
  }

  public void setReadWrite(ProcessAPI readWrite) {
    this.readWrite = readWrite;
  }

  public ConceptualAPI getCompute() {
    return compute;
  }

  public void setCompute(ConceptualAPI compute) {
    this.compute = compute;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

}

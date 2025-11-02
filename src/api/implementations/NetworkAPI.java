package api.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import network.api.LoginRequest;
import network.api.LoginResponse;
import network.api.LogoutRequest;
import network.api.LogoutResponse;
import network.api.NetworkApi;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.ProcessApi;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;

/**
 * Implementation of the NetworkApi interface
 */
public class NetworkAPI implements NetworkApi {


  private ProcessApi readWrite;

  private ConceptualAPI compute;

  public NetworkAPI() {
    // will need to communicate with the ProcessAPI to pass instructions to the
    // Data Storage System

    this.readWrite = new ProcessApiGrpcClient("localhost", 50052); // GRPC
                                                                   // process
                                                                   // client,
                                                                   // implements
                                                                   // PRocessApi


    // Will also need to talk to the computation section to perform
    // calculations,
    // get session keys, etc
    this.compute = new ConceptualAPI();
  }

  // default delimiter if user does not provide one
  private Delimiter defaultDelimiter = Delimiter.COMMA;

  private Resource resource;

  @Override
  public LoginResponse login(LoginRequest req) {
    try {
      if (req == null) {
        throw new IllegalArgumentException("req cannot be null");
      }

      return new LoginResponse(UUID.randomUUID().toString(),
          UUID.randomUUID().toString(), ApiStatus.ERROR);

    } catch (IllegalArgumentException e) {
      // null req
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      // Unexpected exceptions
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Error: " + e.getMessage());
    }

  }

  @Override
  public LogoutResponse logout(LogoutRequest req) {
    try {
      if (req == null) {
        throw new IllegalArgumentException("Request cannot be null");
      }
      return new LogoutResponse(ApiStatus.ERROR);

    } catch (IllegalArgumentException e) {
      // catch null req
      return new LogoutResponse(ApiStatus.ERROR,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      // catch all exceptions we dont expect
      return new LogoutResponse(ApiStatus.ERROR, "Error: " + e.getMessage());
    }

  }

  /**
   * does the computation: read input, run compute, write output, return results
   * as Arraylist<Integer>
   */
  @Override
  public ComputationResponse compute(ComputationRequest request) {

    if (request == null) {
      throw new IllegalArgumentException("Request cannot be null");
    }
    try {
      // Load integers from input resource
      LoadResponse loadResp = readWrite.load(
          new LoadRequest(request.getInputResource(), request.getDelimiter()));
      if (loadResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            loadResp.getMessage());
      }

      // expect the input integers to be in a List<Integer>, provided
      // in LoadResponse
      List<Integer> inputs = loadResp.getPayload();
      List<Integer> results = new ArrayList<>();

      // Run computation for each input
      for (int value : inputs) {
        results.add(compute.performComputation(value).getResult());
      }

      // Store results in output resource
      List resultBatch = new ArrayList<>(results);
      StoreResponse storeResp = readWrite.store(new StoreRequest(
          request.getOutputResource(), resultBatch, request.getDelimiter()));

      if (storeResp.getStatus() != ApiStatus.SUCCESS) {
        String msg = storeResp.getMessage();
        if (msg == null) {
          msg = "Failed to store results";
        }
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(), msg);
      }

      // return ComputationResponse to the user, results stored in a
      // List
      return new ComputationResponse(ApiStatus.SUCCESS, resultBatch,
          "Computation completed");

    } catch (IllegalArgumentException e) {
      // catch null request
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      // catch unexpected exceptions
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "Error: " + e.getMessage());
    }
  }

  // not exactly sure why i have to add this, but according to TestMultiUser i
  // do
  public List<String> processRequests(List<String> requests) {

    return new ArrayList<>(requests);
  }

  public ProcessApi getReadWrite() {
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

  /**
   * These do not belong in the network api, just leaving them commented out
   * incase i need this later.
   * 
   * @Override public StoreDataResponse storeData(StoreDataRequest req) {
   * 
   *           StoreResponse resp = readWrite.store(new
   *           StoreRequest(req.getDestination(), req.getPayload(),
   *           req.getDelimiter())); return new
   *           StoreDataResponse(resp.getStatus(), req.getDestination(),
   *           resp.getMessage());
   * 
   *           }
   * 
   * @Override public LoadDataResponse loadData(LoadDataRequest req) {
   *           LoadResponse resp = readWrite .load(new
   *           LoadRequest(req.getSource(), req.getDelimiter()));
   * 
   *           return new LoadDataResponse(resp.getStatus(), resp.getData(),
   *           defaultDelimiter, resp.getMessage()); }
   */
}

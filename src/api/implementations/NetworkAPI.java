package api.implementations;

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

    return new StoreDataResponse(ApiStatus.ERROR, resource, "Not Implemented");
  }

  @Override
  public LoadDataResponse loadData(LoadDataRequest req) {
    return new LoadDataResponse(ApiStatus.ERROR, "test data".getBytes(),
        Delimiter.defaultDelimiter(), "Not Implemented");
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

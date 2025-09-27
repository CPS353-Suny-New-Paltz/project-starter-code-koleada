package network.api;

import java.util.ArrayList;

import project.annotations.NetworkAPIPrototype;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * This class is the prototype for the functionality of our NetworkAPI
 */
public class NetworkApiPrototye {

  /**
   * This prototype method showcases how the network API is expected to be used
   * 
   * @param api
   */
  @NetworkAPIPrototype
  public void prototype(NetworkApi api) {

    // Login
    LoginResponse loginResp = api
        .login(new LoginRequest("alice", "hashedPassword123"));
    if (!loginResp.success()) {
      return;
    }

    String sessionToken = loginResp.getSessionToken();

    // Store Data
    ResourceType type = ResourceType.DATABASE;
    Resource resource = new Resource(type, "db://myDb");

    StoreDataResponse stoResponse = api.storeData(

        new StoreDataRequest(sessionToken, resource, new ArrayList<>()));

    // check is store was successful
    if (!(stoResponse.getStatus() == ApiStatus.SUCCESS)
        && stoResponse.getErrorMessage() != null) {
      System.out.println(stoResponse.getErrorMessage());
    }

    // Load Data
    LoadDataResponse loadResponse = api
        .loadData(new LoadDataRequest(sessionToken, resource));

    if (!(loadResponse.getStatus() == ApiStatus.SUCCESS)
        && loadResponse.getErrorMessage() != null) {
      System.out.println(loadResponse.getErrorMessage());
    } else {
      System.out.println(loadResponse.getPayload());
    }

    // Logout
    LogoutResponse logoutResp = api.logout(new LogoutRequest(sessionToken));
    System.out.println("Logout success: " + logoutResp.success());
  }
}

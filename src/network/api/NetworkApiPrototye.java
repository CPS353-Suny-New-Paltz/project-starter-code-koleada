package network.api;

import project.annotations.NetworkAPIPrototype;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * This class is the prototype for the functionality of our NetworkAPI
 */
public class NetworkApiPrototye {

  @NetworkAPIPrototype
  public void prototype(NetworkApi api) {

    // Login
    LoginResponse loginResp = api
        .login(new LoginRequest("alice", "hashedPassword123"));
    if (!loginResp.success()) {
      return;
    }

    String sessionToken = loginResp.getSessionToken();

    // Load profile
    LoadProfileResponse profileResp = api
        .loadProfile(new LoadProfileRequest(sessionToken));
    System.out.println("Profile loaded: " + profileResp.getDisplayName());

    // Store Data
    ResourceType type = ResourceType.DATABASE;
    Resource resource = new Resource(type, "db://myDb");

    StoreDataResponse stoResponse = api.storeData(
        new StoreDataRequest(sessionToken, resource, "Hello World".getBytes()));

    // check is store was successful
    if (!(stoResponse.status.equals(ApiStatus.SUCCESS))
        && stoResponse.errorMessage != null) {
      System.out.println(stoResponse.errorMessage);
    }

    // Load Data
    LoadDataResponse loadResponse = api
        .loadData(new LoadDataRequest(sessionToken, resource));

    if (!(loadResponse.status.equals(ApiStatus.SUCCESS))
        && loadResponse.errorMessage != null) {
      System.out.println(loadResponse.errorMessage);
    } else {
      System.out.println(loadResponse.payload);
    }

    // Logout
    LogoutResponse logoutResp = api.logout(new LogoutRequest(sessionToken));
    System.out.println("Logout success: " + logoutResp.success());
  }
}

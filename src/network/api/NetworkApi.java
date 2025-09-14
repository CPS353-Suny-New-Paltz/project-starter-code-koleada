package network.api;

import project.annotations.NetworkAPI;

/**
 * API between the user and the Job I/O portion of the compute engine
 * Responsible for logging the user in/out, viewing their profile, and
 * facilitating the reading and writing of data
 */
@NetworkAPI
public interface NetworkApi {

  /**
   * Handles user authentication
   * 
   * @param LoginRequest
   * @return LoginResponse
   */
  LoginResponse login(LoginRequest req);

  /**
   * Handles logging users out
   * 
   * @param LogoutRequest
   * @return LogoutResponse
   */
  LogoutResponse logout(LogoutRequest req);

  /**
   * Handles loading and returning a users profile
   * 
   * @param LoadProfileRequest
   * @return LoadProfileResponse
   */
  LoadProfileResponse loadProfile(LoadProfileRequest req);

  /**
   * Facilitates the storing of user submitted data into a user specified source
   * 
   * @param StoreDataRequest
   * @return StoreDataResponse
   */
  StoreDataResponse storeData(StoreDataRequest req);

  /**
   * Handles retrieving (loading) data from a user specified source
   * 
   * @param LoadDataRequest
   * @return LoadDataResponse
   */
  LoadDataResponse loadData(LoadDataRequest req);
}

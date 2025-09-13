package network.api;

public interface NetworkApi {

  LoginResponse login(LoginRequest req);

  LogoutResponse logout(LogoutRequest req);

  LoadProfileResponse loadProfile(LoadProfileRequest req);

  StoreDataResponse storeData(StoreDataRequest req);

  LoadDataResponse loadData(LoadDataRequest req);
}

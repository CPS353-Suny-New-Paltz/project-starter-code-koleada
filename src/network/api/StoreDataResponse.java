package network.api;

import shared.stuff.ApiStatus;
import shared.stuff.Resource;

/**
 * A basic response to a data store request
 */
public final class StoreDataResponse {
  ApiStatus status;
  Resource location;
  String errorMessage;

  public StoreDataResponse(ApiStatus status, Resource location,
      String errorMessage) {
    this.status = status;
    this.location = location;
    this.errorMessage = errorMessage;
  }

  public static StoreDataResponse success(Resource location) {
    return new StoreDataResponse(ApiStatus.SUCCESS, location, null);
  }

  public static StoreDataResponse error(ApiStatus status, String message) {
    return new StoreDataResponse(status, null, message);
  }

  public boolean success() {
    return this.status == ApiStatus.SUCCESS;
  }
}

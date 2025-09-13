package network.api;

import shared.stuff.ApiStatus;

/**
 * Response to a LoadDataRequest
 */
public final class LoadDataResponse {
  ApiStatus status;
  byte[] payload; // optional if error
  Delimiter delimiter; // optional
  String errorMessage; // optional

  public LoadDataResponse(ApiStatus status, byte[] payload, Delimiter delimiter,
      String errorMessage) {
    this.status = status;
    this.payload = payload;
    this.delimiter = delimiter;
    this.errorMessage = errorMessage;
  }

  /**
   * Create a LoadDataResponse with SUCCESS status code
   * 
   * @param data
   *          that was loaded
   * @param delimiter
   *          used in the data
   * @return LoadDataResponse
   */
  public static LoadDataResponse success(byte[] payload, Delimiter delimiter) {
    return new LoadDataResponse(ApiStatus.SUCCESS, payload, delimiter, null);
  }

  /**
   * Creates a LoadDataResponse when there is an error
   * 
   * @param status
   *          ApiStatus
   * @param message
   *          error message
   * @return LoadDataResponse
   */
  public static LoadDataResponse error(ApiStatus status, String message) {
    return new LoadDataResponse(status, null, null, message);
  }

  /**
   * Check if the response is successful
   * 
   * @return true if successful
   */
  public boolean success() {
    return this.status == ApiStatus.SUCCESS;
  }
}

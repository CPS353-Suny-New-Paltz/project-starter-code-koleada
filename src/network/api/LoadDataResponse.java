package network.api;

import java.util.List;

import shared.stuff.ApiStatus;

/**
 * Response to a LoadDataRequest
 */
public final class LoadDataResponse {
  private final ApiStatus status;
  private final List payload; // optional if error
  private final Delimiter delimiter; // optional
  private final String errorMessage; // optional

  public LoadDataResponse(ApiStatus status, List payload, Delimiter delimiter,
      String errorMessage) {
    this.status = status;
    this.payload = payload;
    this.delimiter = delimiter;
    this.errorMessage = errorMessage;

  }

  public ApiStatus getStatus() {
    return this.status;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public Delimiter getDelimiter() {
    return this.delimiter;
  }

  public List getPayload() {
    return this.payload;
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
  public static LoadDataResponse success(List payload, Delimiter delimiter) {
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

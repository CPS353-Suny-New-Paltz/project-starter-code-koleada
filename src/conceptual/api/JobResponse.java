package conceptual.api;

/**
 * A response from the computation area about a job's execution status and
 * result
 */
public class JobResponse<T> {
  public String jobId;
  public JobStatus status;
  public T result;
  public String errorMessage;

  // no error message constructor
  public JobResponse(String jobId, JobStatus status, T result) {
    this.jobId = jobId;
    this.status = status;
    this.result = result;
    this.errorMessage = null;
  }

  // constructor with error message
  public JobResponse(String jobId, JobStatus status, T result,
      String errorMessage) {
    this.jobId = jobId;
    this.status = status;
    this.result = result;
    this.errorMessage = errorMessage;
  }

  public String getJobId() {
    return jobId;
  }

  public JobStatus getStatus() {
    return status;
  }

  public T getResult() {
    return result;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}

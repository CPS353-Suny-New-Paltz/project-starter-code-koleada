package conceptual.api;

/**
 * A response from the computation area about a job's execution status and
 * result
 */
public class JobResponse {
  public String jobId;
  public JobStatus status;
  public int result;
  public String errorMessage;

  // no error message constructor
  public JobResponse(String jobId, JobStatus status, int result2) {
    this.jobId = jobId;
    this.status = status;
    this.result = result2;
    this.errorMessage = null;
  }

  // constructor with error message
  public JobResponse(String jobId, JobStatus status, int result,
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

  public int getResult() {
    return result;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}

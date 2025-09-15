package conceptual.api;

/**
 * A response from the computation area about a job's execution status and
 * result
 * 
 * @param <R>
 *          the type of the result produced by the job
 */
public class JobResponse<R> {
  public String jobId;
  public JobStatus status;
  public R result;
  public String errorMessage;

  // no error message constructor
  public JobResponse(String jobId, JobStatus status, R result) {
    this.jobId = jobId;
    this.status = status;
    this.result = result;
    this.errorMessage = null;
  }

  // constructor with error message
  public JobResponse(String jobId, JobStatus status, R result,
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

  public R getResult() {
    return result;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}

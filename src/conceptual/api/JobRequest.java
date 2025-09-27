package conceptual.api;

/**
 * Request to submit a job to the computation area
 * 
 * @param <T>
 *          the type of input data for the job
 */
public class JobRequest {
  public Job job;

  public JobRequest(Job job) {
    this.job = job;
  }

  public Job getJob() {
    return job;
  }
}

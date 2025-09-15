package conceptual.api;

/**
 * Request to submit a job to the computation area
 * 
 * @param <T>
 *          the type of input data for the job
 */
public class JobRequest<T> {
  public Job<T> job;

  public JobRequest(Job<T> job) {
    this.job = job;
  }

  public Job<T> getJob() {
    return job;
  }
}

package conceptual.api;

import project.annotations.ConceptualAPI;

/**
 * The interface for the ConceptualAPI
 */
@ConceptualAPI
public interface ConceptualApi<T> {

  /**
   * Submits a job request to the computation area of the computation engine
   * 
   * @param JobRequest
   *          the job request to submit
   * @return JobResponse indicating the status of the job
   */
  JobResponse<T> submitJob(JobRequest<T> request);

  /**
   * Checks the status of a job that has already been finished
   * 
   * @param jobId
   * 
   * @return JobResponse containing the status
   */
  JobResponse<T> checkStatus(String jobId);
}

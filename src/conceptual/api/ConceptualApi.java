package conceptual.api;

import java.security.NoSuchAlgorithmException;

import project.annotations.ConceptualAPI;

/**
 * The interface for the ConceptualAPI
 */
@ConceptualAPI
public interface ConceptualApi<T> {

  /**
   * Checks the status of a job that has already been finished
   * 
   * @param jobId
   * 
   * @return JobResponse containing the status, result will always be null for
   *         status checks
   */
  JobResponse<T> checkStatus(String jobId);

  /**
   * Performs the actual computations
   * 
   * @param input
   *          to the computation
   * @return result from the computation, null if computation failed
   * @throws NoSuchAlgorithmException
   */
  public JobResponse<T> performComputation(T input)
      throws NoSuchAlgorithmException;
}

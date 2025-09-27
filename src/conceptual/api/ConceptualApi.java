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
   * @return JobResponse containing the status, result will always be -1 for
   *         status checks
   */
  JobResponse checkStatus(String jobId);

  /**
   * Performs the actual computations
   * 
   * @param input
   *          to the computation
   * @return result from the computation, -1 if computation failed
   * @throws NoSuchAlgorithmException
   */
  public JobResponse performComputation(int input)
      throws NoSuchAlgorithmException;
}

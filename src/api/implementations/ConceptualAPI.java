package api.implementations;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import conceptual.api.ConceptualApi;
import conceptual.api.JobResponse;
import conceptual.api.JobStatus;
import shared.stuff.Pbkdf2;

/**
 * Implementation of the ConceptualApi interface
 */
public class ConceptualAPI implements ConceptualApi {

  // Tracks status of each job jobId -> JobStatus
  private Map<String, JobStatus> jobStatuses;

  public ConceptualAPI() {
    this.jobStatuses = new HashMap<>();
  }

  @Override
  public JobResponse checkStatus(String jobId) {
    try {
      if (jobId == null) {
        throw new IllegalArgumentException(
            "Cannot check status for null JobId");
      }
      JobStatus status = jobStatuses.get(jobId);
      return new JobResponse(jobId, status, -1);

    } catch (IllegalArgumentException e) {
      // catch null job request
      return new JobResponse(null, JobStatus.FAILED, -1);
    } catch (Exception e) {
      // catch unexpected
      return new JobResponse(null, JobStatus.FAILED, -1);
    }
  }

  /**
   * 
   * Run the PBKDF2-style computation on a single integer.
   * 
   * @param input
   *          integer input
   * 
   * @return computed result
   * 
   * @throws NoSuchAlgorithmException
   *           if SHA-256 is unavailable
   */
  @Override
  public JobResponse performComputation(int input) {

    // do not need to check if input is null because primitive type int cannot
    // be null

    String jobId = UUID.randomUUID().toString();
    jobStatuses.put(jobId, JobStatus.RUNNING);

    try {
      // PBKDF2 computation
      int result = Pbkdf2.compute(input);
      jobStatuses.put(jobId, JobStatus.COMPLETED);
      return new JobResponse(jobId, JobStatus.COMPLETED, result);

    } catch (NoSuchAlgorithmException e) {
      // can be thrown by calling .compute()
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse(jobId, JobStatus.FAILED, -1);

    } catch (Exception e) {
      // add to map and catch unexpected
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse(jobId, JobStatus.FAILED, -1);
    }
  }
}

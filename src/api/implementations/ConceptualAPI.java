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
public class ConceptualAPI<T> implements ConceptualApi {

  // Tracks status of each job jobId -> JobStatus
  private Map<String, JobStatus> jobStatuses;

  public ConceptualAPI() {
    this.jobStatuses = new HashMap<>();
  }

  @Override
  public JobResponse checkStatus(String jobId) {
    JobStatus status = jobStatuses.get(jobId);
    return new JobResponse(jobId, status, -1);
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
  public JobResponse performComputation(int input)
      throws NoSuchAlgorithmException {
    String jobId = UUID.randomUUID().toString();
    jobStatuses.put(jobId, JobStatus.RUNNING);

    try {
      int result = Pbkdf2.compute(input);
      jobStatuses.put(jobId, JobStatus.COMPLETED);
      return new JobResponse(jobId, JobStatus.COMPLETED, result);
    } catch (NoSuchAlgorithmException e) {
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse(jobId, JobStatus.FAILED, -1);
    }
  }
}

package api.implementations;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import conceptual.api.ConceptualApi;
import conceptual.api.JobResponse;
import conceptual.api.JobStatus;
import shared.stuff.FastPbkdf2;

/**
 * 
 * Implementation of the ConceptualApi interface
 */
public class ConceptualAPI implements ConceptualApi<BigInteger> {
  // Tracks status of each job jobId -> JobStatus
  private Map<String, JobStatus> jobStatuses;

  public ConceptualAPI() {
    // changed this to make it safe for multiple threads
    this.jobStatuses = new ConcurrentHashMap<>();
  }

  @Override
  public JobResponse<BigInteger> checkStatus(String jobId) {
    try {
      if (jobId == null) {
        throw new IllegalArgumentException(
            "Cannot check status for null JobId");
      }

      JobStatus status = jobStatuses.get(jobId);
      return new JobResponse<>(jobId, status, null);

    } catch (IllegalArgumentException e) {
      return new JobResponse<>(null, JobStatus.FAILED, null);
    } catch (Exception e) {
      return new JobResponse<>(null, JobStatus.FAILED, null);
    }
  }

  /**
   * Run the PBKDF2-style computation on a single BigInteger.
   */
  @Override
  public JobResponse<BigInteger> performComputation(BigInteger input) {

    if (input == null) {
      String jobId = UUID.randomUUID().toString();
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse<>(jobId, JobStatus.FAILED, null);
    }

    String jobId = UUID.randomUUID().toString();
    jobStatuses.put(jobId, JobStatus.RUNNING);

    try {
      BigInteger result = FastPbkdf2.compute(input);
      jobStatuses.put(jobId, JobStatus.COMPLETED);
      return new JobResponse<>(jobId, JobStatus.COMPLETED, result);

    } catch (NoSuchAlgorithmException e) {
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse<>(jobId, JobStatus.FAILED, null);

    } catch (Exception e) {
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse<>(jobId, JobStatus.FAILED, null);
    }
  }

  public JobResponse<BigInteger> performComputationFast(BigInteger input) {

    if (input == null) {
      String jobId = UUID.randomUUID().toString();
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse<>(jobId, JobStatus.FAILED, null);
    }

    String jobId = UUID.randomUUID().toString();
    jobStatuses.put(jobId, JobStatus.RUNNING);

    try {
      BigInteger result = FastPbkdf2.compute(input);
      jobStatuses.put(jobId, JobStatus.COMPLETED);
      return new JobResponse<>(jobId, JobStatus.COMPLETED, result);

    } catch (NoSuchAlgorithmException e) {
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse<>(jobId, JobStatus.FAILED, null);

    } catch (Exception e) {
      jobStatuses.put(jobId, JobStatus.FAILED);
      return new JobResponse<>(jobId, JobStatus.FAILED, null);
    }
  }
}

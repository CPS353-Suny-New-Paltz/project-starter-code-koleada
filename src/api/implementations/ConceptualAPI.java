package api.implementations;

import java.util.HashMap;
import java.util.Map;

import conceptual.api.ConceptualApi;
import conceptual.api.Job;
import conceptual.api.JobRequest;
import conceptual.api.JobResponse;
import conceptual.api.JobStatus;

/**
 * Implementation of the ConceptualApi interface
 */
public class ConceptualAPI<T> implements ConceptualApi {

  // Tracks submitted jobs jobId -> Job instance
  private Map<String, Job<T>> jobRegistry;

  // Tracks status of each job jobId -> JobStatus
  private Map<String, JobStatus> jobStatuses;

  public ConceptualAPI() {
    this.jobRegistry = new HashMap<>();
    this.jobStatuses = new HashMap<>();
  }

  @Override
  // paramaritized JobResponse just for testing purposes
  public JobResponse<String> submitJob(JobRequest request) {

    return new JobResponse<String>(request.getJob().getJobId(),
        JobStatus.FAILED, "TestResult");
  }

  @Override
  public JobResponse<T> checkStatus(String jobId) {
    return new JobResponse<T>(jobId, JobStatus.FAILED, null, "Not Implemented");
  }
}

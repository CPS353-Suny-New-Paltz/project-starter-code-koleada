package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.ConceptualAPI;
import conceptual.api.ConceptualApi;
import conceptual.api.ExampleJob;
import conceptual.api.Job;
import conceptual.api.JobRequest;
import conceptual.api.JobResponse;
import conceptual.api.JobStatus;

/**
 * Test suiute for the ConceptualAPI implementation
 */
public class TestConceptualAPI {

  private ConceptualApi<String> conceptualApi;

  /**
   * Create ConceptualAPI
   */
  @BeforeEach
  void setup() {
    conceptualApi = new ConceptualAPI<>();
  }

  /**
   * Tests the submitJob method of the conceptual API
   */
  @Test
  void testSubmitJob() {

    // create ExampleJob and use it to create Jobrequest
    Job<String> job = new ExampleJob<String>("TestJob", "TestData");
    JobRequest<String> req = new JobRequest<String>(job);

    JobResponse<String> resp = conceptualApi.submitJob(req);

    // testing ExampleJob constructor and getter
    assertEquals("TestData", job.getInput());

    assertNotNull(resp);
    assertEquals(job.getJobId(), resp.getJobId());
    assertEquals(JobStatus.FAILED, resp.getStatus());
    assertEquals("TestResult", resp.getResult());
  }

  /**
   * Tests the checkStatus method of the conceptual API
   */
  @Test
  void testCheckStatus() {
    Job<String> job = new ExampleJob<String>("TestJob", "TestData");
    String jobId = job.getJobId();

    JobResponse<String> response = conceptualApi.checkStatus(jobId);

    assertNotNull(response);
    assertNotNull(response.getJobId());
    assertEquals(JobStatus.FAILED, response.getStatus());
    assertNull(response.getResult());
  }
}

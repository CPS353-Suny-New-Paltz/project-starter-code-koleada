package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.ConceptualAPI;
import conceptual.api.ConceptualApi;
import conceptual.api.JobResponse;
import conceptual.api.JobStatus;

/**
 * Test suiute for the ConceptualAPI implementation
 */
public class TestConceptualAPI {

  private ConceptualApi conceptualApi;

  /**
   * Create ConceptualAPI
   */
  @BeforeEach
  void setup() {
    conceptualApi = new ConceptualAPI();
  }

  /**
   * Tests the submitJob method of the conceptual API
   * 
   * @throws NoSuchAlgorithmException
   */
  @Test
  void testPerformJob() throws NoSuchAlgorithmException {

    JobResponse resp = conceptualApi.performComputation(5);

    assertNotNull(resp);
    assertEquals(JobStatus.COMPLETED, resp.getStatus());
    assertEquals(1585103782, resp.getResult());
  }

  /**
   * Tests the checkStatus method of the conceptual API
   * 
   * @throws NoSuchAlgorithmException
   */
  @Test
  void testCheckStatus() throws NoSuchAlgorithmException {

    JobResponse response = conceptualApi.performComputation(5);

    String jobId = response.getJobId();

    assertNotNull(response);
    assertNotNull(response.getJobId());
    assertEquals(JobStatus.COMPLETED,
        conceptualApi.checkStatus(jobId).getStatus());
  }
}

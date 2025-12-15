package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.ConceptualAPI;
import conceptual.api.ConceptualApi;
import conceptual.api.JobResponse;
import conceptual.api.JobStatus;

/**
 * Test suite for the ConceptualAPI implementation
 */
public class TestConceptualAPI {

  private ConceptualApi<BigInteger> conceptualApi;

  /**
   * Create ConceptualAPI
   */
  @BeforeEach
  void setup() {
    conceptualApi = new ConceptualAPI();
  }

  /**
   * Tests the performComputation method of the conceptual API
   * 
   * @throws NoSuchAlgorithmException
   */
  @Test
  void testPerformJob() throws NoSuchAlgorithmException {

    JobResponse<BigInteger> resp = conceptualApi
        .performComputation(BigInteger.valueOf(5));

    assertNotNull(resp);
    assertEquals(JobStatus.COMPLETED, resp.getStatus());

    // FIX: Compare as BigInteger
    assertEquals(BigInteger.valueOf(1059424610), resp.getResult());
  }

  /**
   * Tests the checkStatus method of the conceptual API
   * 
   * @throws NoSuchAlgorithmException
   */
  @Test
  void testCheckStatus() throws NoSuchAlgorithmException {

    JobResponse<BigInteger> response = conceptualApi
        .performComputation(BigInteger.valueOf(5));

    String jobId = response.getJobId();

    assertNotNull(response);
    assertNotNull(response.getJobId());
    assertEquals(JobStatus.COMPLETED,
        conceptualApi.checkStatus(jobId).getStatus());
  }
}

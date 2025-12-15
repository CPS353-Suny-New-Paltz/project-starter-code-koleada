package conceptual.api;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import project.annotations.ConceptualAPIPrototype;

public class ConceptualApiPrototype {

  @ConceptualAPIPrototype
  public void prototype(ConceptualApi<BigInteger> api)
      throws NoSuchAlgorithmException {

    // Submit the job
    JobResponse<BigInteger> response = api
        .performComputation(BigInteger.valueOf(5));
    System.out.println("Submitted Job ID: " + response.getJobId());
    System.out.println("Initial Status: " + response.getStatus());

    // Check status later
    JobResponse<BigInteger> statusResponse = api
        .checkStatus(response.getJobId());
    System.out.println("Job Status: " + statusResponse.getStatus());

    // For prototype purposes, treat null result as "no result yet"
    if (statusResponse.getResult() != null) {
      System.out.println("Result: " + statusResponse.getResult());
    }
  }
}

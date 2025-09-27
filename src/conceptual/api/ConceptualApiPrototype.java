package conceptual.api;

import java.security.NoSuchAlgorithmException;

import project.annotations.ConceptualAPIPrototype;

public class ConceptualApiPrototype {

  @ConceptualAPIPrototype
  public void prototype(ConceptualApi api) throws NoSuchAlgorithmException {

    // Submit the job
    JobResponse response = api.performComputation(5);
    System.out.println("Submitted Job ID: " + response.getJobId());
    System.out.println("Initial Status: " + response.getStatus());

    // Check status later
    JobResponse statusResponse = api.checkStatus(response.getJobId());
    System.out.println("Job Status: " + statusResponse.getStatus());
    if (response.getResult() != -1) {
      System.out.println("Result: " + statusResponse.getResult());
    }
  }
}

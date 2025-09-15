package conceptual.api;

import project.annotations.ConceptualAPIPrototype;

public class ConceptualApiPrototype {

  @ConceptualAPIPrototype
  public void prototype(ConceptualApi api) {

    // Create a simple job (example: a number to be processed)
    Job<Integer> job = new ExampleJob<>("Square a number", 5);
    JobRequest<Integer> request = new JobRequest<>(job);

    // Submit the job
    JobResponse<?> response = api.submitJob(request);
    System.out.println("Submitted Job ID: " + response.getJobId());
    System.out.println("Initial Status: " + response.getStatus());

    // Check status later
    JobResponse<?> statusResponse = api.checkStatus(job.getJobId());
    System.out.println("Job Status: " + statusResponse.getStatus());
    if (statusResponse.getResult() != null) {
      System.out.println("Result: " + statusResponse.getResult());
    }
  }
}

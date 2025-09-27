package conceptual.api;

import java.util.UUID;

/**
 * An example implementation of the Job interface
 * 
 * @param <T>
 *          the type of input data
 */
public class ExampleJob implements Job {

  public String jobId;
  public String description;
  public int input;

  public ExampleJob(String description, int input) {
    this.jobId = UUID.randomUUID().toString();
    this.description = description;
    this.input = input;
  }

  @Override
  public String getJobId() {
    return jobId;
  }

  @Override
  public int getInput() {
    return input;
  }

  public String getDescription() {
    return description;
  }

}

package conceptual.api;

import java.util.UUID;

/**
 * An example implementation of the Job interface
 * 
 * @param <T>
 *          the type of input data
 */
public class ExampleJob<T> implements Job<T> {

  public String jobId;
  public String description;
  public T input;

  public ExampleJob(String description, T input) {
    this.jobId = UUID.randomUUID().toString();
    this.description = description;
    this.input = input;
  }

  @Override
  public String getJobId() {
    return jobId;
  }

  @Override
  public T getInput() {
    return input;
  }

  public String getDescription() {
    return description;
  }

}

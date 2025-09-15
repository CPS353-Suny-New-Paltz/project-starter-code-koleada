package conceptual.api;

/**
 * Represents a generic job that the computation area of the computation engine
 * will do
 * 
 * @param <T>
 *          the type of input data for the job
 */
public interface Job<T> {
  String getJobId();
  String getDescription();
  T getInput();
}
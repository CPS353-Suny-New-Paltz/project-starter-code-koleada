package conceptual.api;

/**
 * Represents a generic job that the computation area of the computation engine
 * will do
 */
public interface Job<T> {
  String getJobId();
  String getDescription();

  /**
   * Returns the input to the computation.
   * 
   * @return the input value (arbitrary-sized integer)
   */
  T getInput();
}

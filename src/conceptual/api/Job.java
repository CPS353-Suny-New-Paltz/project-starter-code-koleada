package conceptual.api;

/**
 * Represents a generic job that the computation area of the computation engine
 * will do
 */
public interface Job {
  String getJobId();
  String getDescription();
  int getInput();
}
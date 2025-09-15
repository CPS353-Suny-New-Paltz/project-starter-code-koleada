package process.api;

/**
 * Generic wrapper for a batch of data. Can hold any data source (List, Set,
 * Stream, etc.).
 */
public class DataBatch<T> {

  private final T dataSource;

  /**
   * @param dataSource
   *          The actual data source (e.g., List, Stream, Set, etc.)
   */
  public DataBatch(T dataSource) {
    this.dataSource = dataSource;
  }

  public T getDataSource() {
    return this.dataSource;
  }

}

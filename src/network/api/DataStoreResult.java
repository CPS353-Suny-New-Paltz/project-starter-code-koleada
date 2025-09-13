package network.api;

/** Result for store operations (use enum not boolean). */
public enum DataStoreResult {
  STORED, // success
  STORAGE_ERROR, // underlying storage error
  UNAUTHORIZED, // user not authorized
  NOT_ACCEPTABLE // payload too large, unsupported format. etc
}

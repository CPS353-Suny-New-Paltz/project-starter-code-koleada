package shared.stuff;

import java.util.List;
import java.util.Objects;

/**
 * A simple class that stores the ResourceType and URI(if needed) for later
 * access. It can also be used to store in-memoery references
 */
public class Resource<T> {
  private final ResourceType type;
  private final String uri; // optional for CUSTOM resources
  private List<T> data; // used for CUSTOM in-memory store/load

  // Construction with specified URI
  public Resource(ResourceType type, String uri) {
    this.type = Objects.requireNonNull(type);
    this.uri = uri;
  }

  // No URI constructor (eg for CUSTOM resources)
  public Resource(ResourceType type, List<T> data) {
    this.type = Objects.requireNonNull(type);
    this.uri = null;
    this.data = data;
  }

  public ResourceType getType() {
    return type;
  }
  public String getUri() {
    return uri;
  }
  public List<T> getData() {
    return data;
  }
  public void setData(List<T> data) {
    this.data = data;
  }

}

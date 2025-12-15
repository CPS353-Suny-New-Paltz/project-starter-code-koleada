package shared.stuff;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * A simple class that stores the ResourceType and URI(if needed) for later
 * access. It can also be used to store in-memoery references
 */
public class Resource {
  private final ResourceType type;
  private final String uri; // optional for CUSTOM resources
  private List<BigInteger> data; // used for CUSTOM in-memory store/load

  // Construction with specified URI
  public Resource(ResourceType type, String uri) {
    if (type == ResourceType.CUSTOM) {
      throw new IllegalArgumentException(
          "Resource Type is custom, but no list is provided");
    }
    this.type = Objects.requireNonNull(type);
    this.uri = uri;
  }

  // No URI constructor (eg for CUSTOM resources)
  public Resource(ResourceType type, List<BigInteger> data) {

    if (data == null && type == ResourceType.CUSTOM) {
      throw new IllegalArgumentException(
          "ResourceType is Custom, but data is null");
    }
    if (type != ResourceType.CUSTOM) {
      throw new IllegalArgumentException(
          "ResourceType is not Custom but list is provided");
    }
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
  public List<BigInteger> getData() {
    return data;
  }
  public void setData(List<BigInteger> data) {
    this.data = data;
  }

}

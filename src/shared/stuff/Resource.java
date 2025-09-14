package shared.stuff;

import java.util.Objects;

/**
 * A simple class that stores the ResourceType and URI(if needed) for later
 * access
 */
public class Resource {
  ResourceType type;
  String uri; // optional for CUSTOM resources

  // Construction with specified URI
  public Resource(ResourceType type, String uri) {
    this.type = Objects.requireNonNull(type);
    this.uri = uri;
  }

  // No URI constructor (eg for CUSTOM resources)
  public Resource(ResourceType type) {
    this.type = Objects.requireNonNull(type);
    this.uri = null;
  }
}

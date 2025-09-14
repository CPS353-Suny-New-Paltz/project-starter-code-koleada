package shared.stuff;

import java.util.Objects;
import java.util.Optional;

/**
 * A simple class that stores the ResourceType and URI(if needed) for later
 * access
 */
public class Resource {
  private final ResourceType type;
  private final String uri; // optional for CUSTOM

  public Resource(ResourceType type, String uri) {
    this.type = Objects.requireNonNull(type);
    this.uri = uri;
  }

  public ResourceType getType() {
    return type;
  }
  public Optional<String> getUri() {
    return Optional.ofNullable(uri);
  }
}

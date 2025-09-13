package network.api;

import java.util.Objects;

/**
 * Basic outline of a user profile caller gets after making a LoadProfileRequest
 */
public final class LoadProfileResponse {
  private final String userId;
  private final String displayName;

  public LoadProfileResponse(String userId, String displayName) {
    this.userId = Objects.requireNonNull(userId);
    this.displayName = displayName;
  }

  public String getUserId() {
    return userId;
  }
  public String getDisplayName() {
    return displayName;
  }
}

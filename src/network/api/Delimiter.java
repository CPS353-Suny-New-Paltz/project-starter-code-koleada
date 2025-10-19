package network.api;

/**
 * This enum controls the allowed delimiters and handles setting a default one
 * when not specified by the user
 */
public enum Delimiter {
  COMMA(","), COLON(":"), SEMICOLON(";"), PIPE("|");

  private final String value;

  // since its an enum, this essentially works as a private constructor,
  // ensuring only the tokens defined in this enum can be used to make
  // delimiters
  Delimiter(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  /**
   * Returns the default delimiter (comma).
   */
  public static Delimiter defaultDelimiter() {
    return COMMA;
  }
}

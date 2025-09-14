package network.api;

/**
 * This enum controls the allowed delimiters and handles setting a default one
 * when not specified by the user
 */
public final class Delimiter {
  private final char value;

  private Delimiter(char value) {
    this.value = value;
  }

  public char getValue() {
    return value;
  }

  public static final Delimiter COMMA = new Delimiter(',');
  public static final Delimiter COLON = new Delimiter(':');
  public static final Delimiter SEMICOLON = new Delimiter(';');
  public static final Delimiter PIPE = new Delimiter('|');

  /**
   * Default delimiter used if user does not specify. w
   */
  public static Delimiter defaultDelimiter() {
    return COMMA;
  }
}

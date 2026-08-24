package amp_libs.org.tomlj;

public final class TomlParseError extends RuntimeException {
   private final TomlPosition position;

   TomlParseError(String message, TomlPosition position) {
      super(message);
      this.position = position;
   }

   TomlParseError(String message, TomlPosition position, Throwable cause) {
      super(message, cause);
      this.position = position;
   }

   public TomlPosition position() {
      return this.position;
   }

   @Override
   public String toString() {
      return this.getMessage() + " (" + this.position + ")";
   }
}

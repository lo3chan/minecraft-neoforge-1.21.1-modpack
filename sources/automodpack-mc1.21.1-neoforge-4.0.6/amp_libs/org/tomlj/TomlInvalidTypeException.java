package amp_libs.org.tomlj;

public class TomlInvalidTypeException extends RuntimeException {
   TomlInvalidTypeException(String message) {
      super(message);
   }
}

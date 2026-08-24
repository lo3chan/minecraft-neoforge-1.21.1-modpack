package amp_libs.org.tomlj;

import java.util.List;

public interface TomlParseResult extends TomlTable {
   default boolean hasErrors() {
      return !this.errors().isEmpty();
   }

   List<TomlParseError> errors();
}

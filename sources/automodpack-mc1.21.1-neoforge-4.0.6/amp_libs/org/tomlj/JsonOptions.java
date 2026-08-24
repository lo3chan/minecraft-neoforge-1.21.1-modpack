package amp_libs.org.tomlj;

import java.util.EnumSet;

public enum JsonOptions {
   VALUES_AS_OBJECTS_WITH_TYPE,
   ALL_VALUES_AS_STRINGS;

   static EnumSet<JsonOptions> setFrom(JsonOptions[] options) {
      return options.length > 0 ? EnumSet.of(options[0], options) : EnumSet.noneOf(JsonOptions.class);
   }
}

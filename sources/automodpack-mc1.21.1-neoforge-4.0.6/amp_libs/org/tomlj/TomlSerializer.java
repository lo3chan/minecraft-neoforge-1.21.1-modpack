package amp_libs.org.tomlj;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

final class TomlSerializer {
   private TomlSerializer() {
   }

   static void toToml(TomlTable table, Appendable appendable) throws IOException {
      Objects.requireNonNull(table);
      Objects.requireNonNull(appendable);
      toToml(table, appendable, -2, "");
   }

   private static void toToml(TomlTable table, Appendable appendable, int indent, String path) throws IOException {
      for (Entry<String, Object> entry : table.entrySet().stream().sorted(Comparator.comparing(entryx -> {
         TomlType tomlTypex = TomlType.typeFor(entryx.getValue()).get();
         return !tomlTypex.equals(TomlType.TABLE) && (!tomlTypex.equals(TomlType.ARRAY) || !isTableArray((TomlArray)entryx.getValue())) ? 0 : 1;
      })).collect(Collectors.toList())) {
         String key = entry.getKey();
         Object value = entry.getValue();
         key = Toml.tomlEscape(key).toString();
         if (!key.matches("[a-zA-Z0-9_-]*")) {
            key = "\"" + key + "\"";
         }

         String newPath = (path.isEmpty() ? "" : path + ".") + key;
         Optional<TomlType> tomlType = TomlType.typeFor(value);

         assert tomlType.isPresent();

         boolean isTableArray = tomlType.get().equals(TomlType.ARRAY) && isTableArray((TomlArray)value);
         if (tomlType.get().equals(TomlType.TABLE)) {
            append(appendable, indent + 2, "[" + newPath + "]");
            appendable.append(System.lineSeparator());
         } else if (!isTableArray) {
            append(appendable, indent + 2, key + "=");
         }

         appendTomlValue(value, appendable, indent, newPath);
         if (!tomlType.get().equals(TomlType.TABLE) && !isTableArray) {
            appendable.append(System.lineSeparator());
         }
      }
   }

   static void toToml(TomlArray array, Appendable appendable) throws IOException {
      Objects.requireNonNull(array);
      Objects.requireNonNull(appendable);
      toToml(array, appendable, 0, "");
   }

   private static void toToml(TomlArray array, Appendable appendable, int indent, String path) throws IOException {
      boolean tableArray = isTableArray(array);
      if (!tableArray) {
         appendable.append("[");
         if (!array.isEmpty()) {
            appendable.append(System.lineSeparator());
         }
      }

      Iterator<Object> iterator = array.toList().iterator();

      while (iterator.hasNext()) {
         Object tomlValue = iterator.next();
         Optional<TomlType> tomlType = TomlType.typeFor(tomlValue);

         assert tomlType.isPresent();

         if (tomlType.get().equals(TomlType.TABLE)) {
            append(appendable, indent, "[[" + path + "]]");
            appendable.append(System.lineSeparator());
            toToml((TomlTable)tomlValue, appendable, indent, path);
         } else {
            indentLine(appendable, indent + 2);
            appendTomlValue(tomlValue, appendable, indent, path);
         }

         if (!tableArray) {
            if (iterator.hasNext()) {
               appendable.append(",");
            }

            appendable.append(System.lineSeparator());
         }
      }

      if (!tableArray) {
         append(appendable, indent, "]");
      }
   }

   private static void appendTomlValue(Object value, Appendable appendable, int indent, String path) throws IOException {
      Optional<TomlType> tomlType = TomlType.typeFor(value);

      assert tomlType.isPresent();

      switch ((TomlType)tomlType.get()) {
         case STRING:
            append(appendable, 0, "\"" + Toml.tomlEscape((String)value) + "\"");
            break;
         case INTEGER:
         case FLOAT:
            append(appendable, 0, value.toString());
            break;
         case OFFSET_DATE_TIME:
            append(appendable, 0, DateTimeFormatter.ISO_OFFSET_DATE_TIME.format((OffsetDateTime)value));
            break;
         case LOCAL_DATE_TIME:
            append(appendable, 0, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format((LocalDateTime)value));
            break;
         case LOCAL_DATE:
            append(appendable, 0, DateTimeFormatter.ISO_LOCAL_DATE.format((LocalDate)value));
            break;
         case LOCAL_TIME:
            append(appendable, 0, DateTimeFormatter.ISO_LOCAL_TIME.format((LocalTime)value));
            break;
         case BOOLEAN:
            append(appendable, 0, (Boolean)value ? "true" : "false");
            break;
         case ARRAY:
            toToml((TomlArray)value, appendable, indent + 2, path);
            break;
         case TABLE:
            toToml((TomlTable)value, appendable, indent + 2, path);
      }
   }

   private static void append(Appendable appendable, int indent, String line) throws IOException {
      indentLine(appendable, indent);
      appendable.append(line);
   }

   private static void indentLine(Appendable appendable, int indent) throws IOException {
      for (int i = 0; i < indent; i++) {
         appendable.append(' ');
      }
   }

   private static boolean isTableArray(TomlArray array) {
      for (Object tomlValue : array.toList()) {
         Optional<TomlType> tomlType = TomlType.typeFor(tomlValue);

         assert tomlType.isPresent();

         if (tomlType.get().equals(TomlType.TABLE)) {
            return true;
         }
      }

      return false;
   }
}

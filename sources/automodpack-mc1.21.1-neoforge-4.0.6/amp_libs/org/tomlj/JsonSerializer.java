package amp_libs.org.tomlj;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

final class JsonSerializer {
   private JsonSerializer() {
   }

   static void toJson(TomlTable table, Appendable appendable, Set<JsonOptions> options) throws IOException {
      Objects.requireNonNull(table);
      Objects.requireNonNull(appendable);
      toJson(table, appendable, options, 0);
      appendable.append(System.lineSeparator());
   }

   private static void toJson(TomlTable table, Appendable appendable, Set<JsonOptions> options, int indent) throws IOException {
      if (table.isEmpty()) {
         appendable.append("{}");
      } else {
         appendLine(appendable, "{");
         Iterator<Entry<String, Object>> iterator = table.entrySet().stream().iterator();

         while (iterator.hasNext()) {
            Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            append(appendable, indent + 2, "\"" + escape(key) + "\" : ");
            Object value = entry.getValue();

            assert value != null;

            appendTomlValue(value, appendable, options, indent);
            if (iterator.hasNext()) {
               appendable.append(",");
               appendable.append(System.lineSeparator());
            }
         }

         appendable.append(System.lineSeparator());
         append(appendable, indent, "}");
      }
   }

   static void toJson(TomlArray array, Appendable appendable, Set<JsonOptions> options) throws IOException {
      toJson(array, appendable, options, 0);
      appendable.append(System.lineSeparator());
   }

   private static void toJson(TomlArray array, Appendable appendable, Set<JsonOptions> options, int indent) throws IOException {
      if (array.isEmpty()) {
         appendable.append("[]");
      } else {
         appendable.append("[");
         Optional<TomlType> tomlType = Optional.empty();
         Iterator<Object> iterator = array.toList().iterator();

         while (iterator.hasNext()) {
            Object tomlValue = iterator.next();
            tomlType = TomlType.typeFor(tomlValue);

            assert tomlType.isPresent();

            if (tomlType.get().equals(TomlType.TABLE)) {
               toJson((TomlTable)tomlValue, appendable, options, indent);
            } else {
               appendable.append(System.lineSeparator());
               indentLine(appendable, indent + 2);
               appendTomlValue(tomlValue, appendable, options, indent);
            }

            if (iterator.hasNext()) {
               appendable.append(",");
            } else if (!tomlType.get().equals(TomlType.TABLE)) {
               appendable.append(System.lineSeparator());
            }
         }

         if (tomlType.isPresent() && tomlType.get().equals(TomlType.TABLE)) {
            appendable.append("]");
         } else {
            append(appendable, indent, "]");
         }
      }
   }

   private static void appendTomlValue(Object value, Appendable appendable, Set<JsonOptions> options, int indent) throws IOException {
      Optional<TomlType> tomlType = TomlType.typeFor(value);

      assert tomlType.isPresent();

      switch ((TomlType)tomlType.get()) {
         case ARRAY:
            toJson((TomlArray)value, appendable, options, indent + 2);
            return;
         case TABLE:
            toJson((TomlTable)value, appendable, options, indent + 2);
            return;
         default:
            if (options.contains(JsonOptions.VALUES_AS_OBJECTS_WITH_TYPE)) {
               appendable.append("{ \"type\": \"");
               appendable.append(typeName(tomlType.get()));
               appendable.append("\", \"value\": ");
               appendTomlValueLiteral(tomlType.get(), value, appendable, options);
               appendable.append(" }");
            } else {
               appendTomlValueLiteral(tomlType.get(), value, appendable, options);
            }
      }
   }

   private static String typeName(TomlType tomlType) {
      switch (tomlType) {
         case BOOLEAN:
            return "bool";
         case OFFSET_DATE_TIME:
            return "datetime";
         case LOCAL_DATE_TIME:
            return "datetime-local";
         case LOCAL_DATE:
            return "date-local";
         case LOCAL_TIME:
            return "time-local";
         default:
            return tomlType.typeName();
      }
   }

   private static void appendTomlValueLiteral(TomlType tomlType, Object value, Appendable appendable, Set<JsonOptions> options) throws IOException {
      switch (tomlType) {
         case BOOLEAN:
            if (options.contains(JsonOptions.ALL_VALUES_AS_STRINGS)) {
               appendable.append('"');
            }

            appendable.append((Boolean)value ? "true" : "false");
            if (options.contains(JsonOptions.ALL_VALUES_AS_STRINGS)) {
               appendable.append('"');
            }
            break;
         case OFFSET_DATE_TIME:
            appendable.append('"');
            appendable.append(((OffsetDateTime)value).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            appendable.append('"');
            break;
         case LOCAL_DATE_TIME:
            appendable.append('"');
            appendable.append(((LocalDateTime)value).format(DateTimeFormatter.ISO_DATE_TIME));
            appendable.append('"');
            break;
         case LOCAL_DATE:
            appendable.append('"');
            appendable.append(((LocalDate)value).format(DateTimeFormatter.ISO_DATE));
            appendable.append('"');
            break;
         case LOCAL_TIME:
            appendable.append('"');
            appendable.append(((LocalTime)value).format(DateTimeFormatter.ISO_TIME));
            appendable.append('"');
            break;
         case STRING:
            appendable.append('"');
            appendable.append(escape((String)value));
            appendable.append('"');
            break;
         case INTEGER:
            if (options.contains(JsonOptions.ALL_VALUES_AS_STRINGS)) {
               appendable.append('"');
            }

            appendable.append(value.toString());
            if (options.contains(JsonOptions.ALL_VALUES_AS_STRINGS)) {
               appendable.append('"');
            }
            break;
         case FLOAT:
            if (options.contains(JsonOptions.ALL_VALUES_AS_STRINGS)) {
               appendable.append('"');
            }

            if (Double.isNaN((Double)value)) {
               appendable.append("nan");
            } else if ((Double)value == 1.0 / 0.0) {
               appendable.append("+inf");
            } else if ((Double)value == -1.0 / 0.0) {
               appendable.append("-inf");
            } else {
               appendable.append(value.toString());
            }

            if (options.contains(JsonOptions.ALL_VALUES_AS_STRINGS)) {
               appendable.append('"');
            }
            break;
         default:
            throw new AssertionError("Attempted to output literal form of non-literal type " + tomlType.typeName());
      }
   }

   private static void append(Appendable appendable, int indent, String line) throws IOException {
      indentLine(appendable, indent);
      appendable.append(line);
   }

   private static void appendLine(Appendable appendable, String line) throws IOException {
      appendable.append(line);
      appendable.append(System.lineSeparator());
   }

   private static void indentLine(Appendable appendable, int indent) throws IOException {
      for (int i = 0; i < indent; i++) {
         appendable.append(' ');
      }
   }

   private static StringBuilder escape(String text) {
      StringBuilder out = new StringBuilder(text.length());

      for (int i = 0; i < text.length(); i++) {
         char ch = text.charAt(i);
         if (ch == '"') {
            out.append("\\\"");
         } else if (ch == '\\') {
            out.append("\\\\");
         } else if (ch >= ' ') {
            out.append(ch);
         } else {
            switch (ch) {
               case '\b':
                  out.append("\\b");
                  break;
               case '\t':
                  out.append("\\t");
                  break;
               case '\n':
                  out.append("\\n");
                  break;
               case '\u000b':
               default:
                  out.append("\\u").append(String.format("%04x", text.codePointAt(i)));
                  break;
               case '\f':
                  out.append("\\f");
                  break;
               case '\r':
                  out.append("\\r");
            }
         }
      }

      return out;
   }
}

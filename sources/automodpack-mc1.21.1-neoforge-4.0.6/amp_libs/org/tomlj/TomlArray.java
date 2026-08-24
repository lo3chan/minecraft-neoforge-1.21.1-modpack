package amp_libs.org.tomlj;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

public interface TomlArray {
   int size();

   boolean isEmpty();

   /** @deprecated */
   boolean containsStrings();

   /** @deprecated */
   boolean containsLongs();

   /** @deprecated */
   boolean containsDoubles();

   /** @deprecated */
   boolean containsBooleans();

   /** @deprecated */
   boolean containsOffsetDateTimes();

   /** @deprecated */
   boolean containsLocalDateTimes();

   /** @deprecated */
   boolean containsLocalDates();

   /** @deprecated */
   boolean containsLocalTimes();

   /** @deprecated */
   boolean containsArrays();

   /** @deprecated */
   boolean containsTables();

   Object get(int var1);

   TomlPosition inputPositionOf(int var1);

   default String getString(int index) {
      Object value = this.get(index);
      if (!(value instanceof String)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (String)value;
      }
   }

   default long getLong(int index) {
      Object value = this.get(index);
      if (!(value instanceof Long)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (Long)value;
      }
   }

   default double getDouble(int index) {
      Object value = this.get(index);
      if (!(value instanceof Double)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (Double)value;
      }
   }

   default boolean getBoolean(int index) {
      Object value = this.get(index);
      if (!(value instanceof Boolean)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (Boolean)value;
      }
   }

   default OffsetDateTime getOffsetDateTime(int index) {
      Object value = this.get(index);
      if (!(value instanceof OffsetDateTime)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (OffsetDateTime)value;
      }
   }

   default LocalDateTime getLocalDateTime(int index) {
      Object value = this.get(index);
      if (!(value instanceof LocalDateTime)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (LocalDateTime)value;
      }
   }

   default LocalDate getLocalDate(int index) {
      Object value = this.get(index);
      if (!(value instanceof LocalDate)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (LocalDate)value;
      }
   }

   default LocalTime getLocalTime(int index) {
      Object value = this.get(index);
      if (!(value instanceof LocalTime)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (LocalTime)value;
      }
   }

   default TomlArray getArray(int index) {
      Object value = this.get(index);
      if (!(value instanceof TomlArray)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (TomlArray)value;
      }
   }

   default TomlTable getTable(int index) {
      Object value = this.get(index);
      if (!(value instanceof TomlTable)) {
         throw new TomlInvalidTypeException("key at index " + index + " is a " + TomlType.typeNameFor(value));
      } else {
         return (TomlTable)value;
      }
   }

   List<Object> toList();

   default String toJson(JsonOptions... options) {
      return this.toJson(JsonOptions.setFrom(options));
   }

   default String toJson(EnumSet<JsonOptions> options) {
      StringBuilder builder = new StringBuilder();

      try {
         this.toJson(builder, options);
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }

      return builder.toString();
   }

   default void toJson(Appendable appendable, JsonOptions... options) throws IOException {
      this.toJson(appendable, JsonOptions.setFrom(options));
   }

   default void toJson(Appendable appendable, EnumSet<JsonOptions> options) throws IOException {
      JsonSerializer.toJson(this, appendable, options);
   }

   default String toToml() {
      StringBuilder builder = new StringBuilder();

      try {
         this.toToml(builder);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }

      return builder.toString();
   }

   default void toToml(Appendable appendable) throws IOException {
      TomlSerializer.toToml(this, appendable);
   }
}

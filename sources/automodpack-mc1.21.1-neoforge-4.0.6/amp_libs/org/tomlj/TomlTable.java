package amp_libs.org.tomlj;

import amp_libs.org.checkerframework.checker.nullness.qual.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface TomlTable {
   int size();

   boolean isEmpty();

   default boolean contains(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.contains(Parser.parseDottedKey(dottedKey));
   }

   default boolean contains(List<String> path) {
      try {
         return this.get(path) != null;
      } catch (TomlInvalidTypeException var3) {
         return false;
      }
   }

   Set<String> keySet();

   default Set<String> dottedKeySet() {
      return this.keyPathSet().stream().map(Toml::joinKeyPath).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   default Set<String> dottedKeySet(boolean includeTables) {
      return this.keyPathSet(includeTables).stream().map(Toml::joinKeyPath).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   default Set<List<String>> keyPathSet() {
      return this.keyPathSet(false);
   }

   Set<List<String>> keyPathSet(boolean var1);

   Set<Entry<String, Object>> entrySet();

   default Set<Entry<String, Object>> dottedEntrySet() {
      return this.entryPathSet()
         .stream()
         .map(e -> new SimpleEntry<>(Toml.joinKeyPath(e.getKey()), e.getValue()))
         .collect(Collectors.toCollection(LinkedHashSet::new));
   }

   default Set<Entry<String, Object>> dottedEntrySet(boolean includeTables) {
      return this.entryPathSet(includeTables)
         .stream()
         .map(e -> new SimpleEntry<>(Toml.joinKeyPath(e.getKey()), e.getValue()))
         .collect(Collectors.toCollection(LinkedHashSet::new));
   }

   default Set<Entry<List<String>, Object>> entryPathSet() {
      return this.entryPathSet(false);
   }

   Set<Entry<List<String>, Object>> entryPathSet(boolean var1);

   @Nullable
   default Object get(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.get(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   Object get(List<String> var1);

   @Nullable
   default TomlPosition inputPositionOf(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.inputPositionOf(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   TomlPosition inputPositionOf(List<String> var1);

   default boolean isString(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isString(Parser.parseDottedKey(dottedKey));
   }

   default boolean isString(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof String;
   }

   @Nullable
   default String getString(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getString(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default String getString(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof String)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (String)value;
      }
   }

   default String getString(String dottedKey, Supplier<String> defaultValue) {
      Objects.requireNonNull(dottedKey);
      return this.getString(Parser.parseDottedKey(dottedKey), defaultValue);
   }

   default String getString(List<String> path, Supplier<String> defaultValue) {
      Objects.requireNonNull(defaultValue);
      String value = this.getString(path);
      return value != null ? value : defaultValue.get();
   }

   default boolean isLong(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isLong(Parser.parseDottedKey(dottedKey));
   }

   default boolean isLong(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof Long;
   }

   @Nullable
   default Long getLong(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getLong(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default Long getLong(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof Long)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (Long)value;
      }
   }

   default long getLong(String dottedKey, LongSupplier defaultValue) {
      Objects.requireNonNull(dottedKey);
      return this.getLong(Parser.parseDottedKey(dottedKey), defaultValue);
   }

   default long getLong(List<String> path, LongSupplier defaultValue) {
      Objects.requireNonNull(defaultValue);
      Long value = this.getLong(path);
      return value != null ? value : defaultValue.getAsLong();
   }

   default boolean isDouble(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isDouble(Parser.parseDottedKey(dottedKey));
   }

   default boolean isDouble(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof Double;
   }

   @Nullable
   default Double getDouble(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getDouble(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default Double getDouble(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof Double)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (Double)value;
      }
   }

   default double getDouble(String dottedKey, DoubleSupplier defaultValue) {
      Objects.requireNonNull(dottedKey);
      return this.getDouble(Parser.parseDottedKey(dottedKey), defaultValue);
   }

   default double getDouble(List<String> path, DoubleSupplier defaultValue) {
      Objects.requireNonNull(defaultValue);
      Double value = this.getDouble(path);
      return value != null ? value : defaultValue.getAsDouble();
   }

   default boolean isBoolean(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isBoolean(Parser.parseDottedKey(dottedKey));
   }

   default boolean isBoolean(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof Boolean;
   }

   @Nullable
   default Boolean getBoolean(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getBoolean(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default Boolean getBoolean(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof Boolean)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (Boolean)value;
      }
   }

   default boolean getBoolean(String dottedKey, BooleanSupplier defaultValue) {
      Objects.requireNonNull(dottedKey);
      return this.getBoolean(Parser.parseDottedKey(dottedKey), defaultValue);
   }

   default boolean getBoolean(List<String> path, BooleanSupplier defaultValue) {
      Objects.requireNonNull(defaultValue);
      Boolean value = this.getBoolean(path);
      return value != null ? value : defaultValue.getAsBoolean();
   }

   default boolean isOffsetDateTime(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isOffsetDateTime(Parser.parseDottedKey(dottedKey));
   }

   default boolean isOffsetDateTime(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof OffsetDateTime;
   }

   @Nullable
   default OffsetDateTime getOffsetDateTime(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getOffsetDateTime(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default OffsetDateTime getOffsetDateTime(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof OffsetDateTime)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (OffsetDateTime)value;
      }
   }

   default OffsetDateTime getOffsetDateTime(String dottedKey, Supplier<OffsetDateTime> defaultValue) {
      Objects.requireNonNull(dottedKey);
      return this.getOffsetDateTime(Parser.parseDottedKey(dottedKey), defaultValue);
   }

   default OffsetDateTime getOffsetDateTime(List<String> path, Supplier<OffsetDateTime> defaultValue) {
      Objects.requireNonNull(defaultValue);
      OffsetDateTime value = this.getOffsetDateTime(path);
      return value != null ? value : defaultValue.get();
   }

   default boolean isLocalDateTime(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isLocalDateTime(Parser.parseDottedKey(dottedKey));
   }

   default boolean isLocalDateTime(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof LocalDateTime;
   }

   @Nullable
   default LocalDateTime getLocalDateTime(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getLocalDateTime(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default LocalDateTime getLocalDateTime(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof LocalDateTime)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (LocalDateTime)value;
      }
   }

   default LocalDateTime getLocalDateTime(String dottedKey, Supplier<LocalDateTime> defaultValue) {
      Objects.requireNonNull(dottedKey);
      return this.getLocalDateTime(Parser.parseDottedKey(dottedKey), defaultValue);
   }

   default LocalDateTime getLocalDateTime(List<String> path, Supplier<LocalDateTime> defaultValue) {
      Objects.requireNonNull(defaultValue);
      LocalDateTime value = this.getLocalDateTime(path);
      return value != null ? value : defaultValue.get();
   }

   default boolean isLocalDate(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isLocalDate(Parser.parseDottedKey(dottedKey));
   }

   default boolean isLocalDate(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof LocalDate;
   }

   @Nullable
   default LocalDate getLocalDate(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getLocalDate(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default LocalDate getLocalDate(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof LocalDate)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (LocalDate)value;
      }
   }

   default LocalDate getLocalDate(String dottedKey, Supplier<LocalDate> defaultValue) {
      Objects.requireNonNull(dottedKey);
      return this.getLocalDate(Parser.parseDottedKey(dottedKey), defaultValue);
   }

   default LocalDate getLocalDate(List<String> path, Supplier<LocalDate> defaultValue) {
      Objects.requireNonNull(defaultValue);
      LocalDate value = this.getLocalDate(path);
      return value != null ? value : defaultValue.get();
   }

   default boolean isLocalTime(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isLocalTime(Parser.parseDottedKey(dottedKey));
   }

   default boolean isLocalTime(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof LocalTime;
   }

   @Nullable
   default LocalTime getLocalTime(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getLocalTime(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default LocalTime getLocalTime(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof LocalTime)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (LocalTime)value;
      }
   }

   default LocalTime getLocalTime(String dottedKey, Supplier<LocalTime> defaultValue) {
      Objects.requireNonNull(dottedKey);
      return this.getLocalTime(Parser.parseDottedKey(dottedKey), defaultValue);
   }

   default LocalTime getLocalTime(List<String> path, Supplier<LocalTime> defaultValue) {
      Objects.requireNonNull(defaultValue);
      LocalTime value = this.getLocalTime(path);
      return value != null ? value : defaultValue.get();
   }

   default boolean isArray(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isArray(Parser.parseDottedKey(dottedKey));
   }

   default boolean isArray(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof TomlArray;
   }

   @Nullable
   default TomlArray getArray(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getArray(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default TomlArray getArray(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof TomlArray)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (TomlArray)value;
      }
   }

   default TomlArray getArrayOrEmpty(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getArrayOrEmpty(Parser.parseDottedKey(dottedKey));
   }

   default TomlArray getArrayOrEmpty(List<String> path) {
      TomlArray value = this.getArray(path);
      return value != null ? value : EmptyTomlArray.EMPTY_ARRAY;
   }

   default boolean isTable(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.isTable(Parser.parseDottedKey(dottedKey));
   }

   default boolean isTable(List<String> path) {
      Object value;
      try {
         value = this.get(path);
      } catch (TomlInvalidTypeException var4) {
         return false;
      }

      return value instanceof TomlTable;
   }

   @Nullable
   default TomlTable getTable(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getTable(Parser.parseDottedKey(dottedKey));
   }

   @Nullable
   default TomlTable getTable(List<String> path) {
      Object value = this.get(path);
      if (value == null) {
         return null;
      } else if (!(value instanceof TomlTable)) {
         throw new TomlInvalidTypeException("Value of '" + Toml.joinKeyPath(path) + "' is a " + TomlType.typeNameFor(value));
      } else {
         return (TomlTable)value;
      }
   }

   default TomlTable getTableOrEmpty(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return this.getTableOrEmpty(Parser.parseDottedKey(dottedKey));
   }

   default TomlTable getTableOrEmpty(List<String> path) {
      TomlTable value = this.getTable(path);
      return value != null ? value : EmptyTomlTable.EMPTY_TABLE;
   }

   Map<String, Object> toMap();

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

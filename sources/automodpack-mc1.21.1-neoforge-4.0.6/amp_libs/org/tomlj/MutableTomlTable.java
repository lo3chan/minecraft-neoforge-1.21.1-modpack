package amp_libs.org.tomlj;

import amp_libs.org.checkerframework.checker.nullness.qual.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class MutableTomlTable implements TomlTable {
   private final Map<String, MutableTomlTable.Element> properties = new LinkedHashMap<>();
   private final TomlVersion version;
   private TomlPosition definedAt;

   MutableTomlTable(TomlVersion version, TomlPosition definedAt) {
      this.version = version;
      this.definedAt = definedAt;
   }

   MutableTomlTable(TomlVersion version) {
      this.version = version;
      this.definedAt = null;
   }

   boolean isDefined() {
      return this.definedAt != null;
   }

   void define(TomlPosition position) {
      this.definedAt = position;
   }

   @Override
   public int size() {
      return this.properties.size();
   }

   @Override
   public boolean isEmpty() {
      return this.properties.isEmpty();
   }

   @Override
   public Set<String> keySet() {
      return this.properties.keySet();
   }

   @Override
   public Set<List<String>> keyPathSet(boolean includeTables) {
      return this.properties.entrySet().stream().flatMap(entry -> {
         String key = entry.getKey();
         List<String> basePath = Collections.singletonList(key);
         MutableTomlTable.Element element = entry.getValue();
         if (!(element.value instanceof TomlTable)) {
            return Stream.of(basePath);
         } else {
            Stream<List<String>> subKeys = ((TomlTable)element.value).keyPathSet(includeTables).stream().map(subPath -> {
               List<String> path = new ArrayList<>(subPath.size() + 1);
               path.add(key);
               path.addAll(subPath);
               return path;
            });
            return includeTables ? Stream.concat(Stream.of(basePath), subKeys) : subKeys;
         }
      }).collect(Collectors.toSet());
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      return this.properties
         .entrySet()
         .stream()
         .map(entry -> new SimpleEntry<>(entry.getKey(), entry.getValue().value))
         .collect(Collectors.toCollection(LinkedHashSet::new));
   }

   @Override
   public Set<Entry<List<String>, Object>> entryPathSet(boolean includeTables) {
      return this.properties.entrySet().stream().flatMap(entry -> {
         String key = entry.getKey();
         List<String> entryPath = Collections.singletonList(key);
         MutableTomlTable.Element element = entry.getValue();
         if (!(element.value instanceof TomlTable)) {
            return Stream.of(new SimpleEntry<>(entryPath, element.value));
         } else {
            Stream<Entry<List<String>, Object>> subEntries = ((TomlTable)element.value).entryPathSet(includeTables).stream().map(subEntry -> {
               List<String> subPath = subEntry.getKey();
               List<String> path = new ArrayList<>(subPath.size() + 1);
               path.add(key);
               path.addAll(subPath);
               return new SimpleEntry<>(path, subEntry.getValue());
            });
            return includeTables ? Stream.concat(Stream.of(new SimpleEntry<>(entryPath, element.value)), subEntries) : subEntries;
         }
      }).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   @Nullable
   @Override
   public Object get(List<String> path) {
      if (path.isEmpty()) {
         return this;
      } else {
         MutableTomlTable.Element element = this.getElement(path);
         return element != null ? element.value : null;
      }
   }

   @Nullable
   @Override
   public TomlPosition inputPositionOf(List<String> path) {
      if (path.isEmpty()) {
         return TomlPosition.positionAt(1, 1);
      } else {
         MutableTomlTable.Element element = this.getElement(path);
         return element != null ? element.position : null;
      }
   }

   private MutableTomlTable.Element getElement(List<String> path) {
      MutableTomlTable table = this;
      int depth = path.size();

      assert depth > 0;

      for (int i = 0; i < depth - 1; i++) {
         MutableTomlTable.Element element = table.properties.get(path.get(i));
         if (element == null) {
            return null;
         }

         if (!(element.value instanceof MutableTomlTable)) {
            return null;
         }

         table = (MutableTomlTable)element.value;
      }

      return table.properties.get(path.get(depth - 1));
   }

   @Override
   public Map<String, Object> toMap() {
      return this.properties.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().value));
   }

   MutableTomlTable createTable(List<String> path, TomlPosition position) {
      if (path.isEmpty()) {
         return this;
      } else {
         int depth = path.size();
         MutableTomlTable table = this.ensureTable(path.subList(0, depth - 1), position, true, true).table;
         String key = path.get(depth - 1);
         MutableTomlTable.Element element = table.properties.get(key);
         if (element == null) {
            MutableTomlTable newTable = new MutableTomlTable(this.version, position);
            table.properties.put(key, new MutableTomlTable.Element(newTable, position));
            return newTable;
         } else {
            if (element.value instanceof MutableTomlTable) {
               MutableTomlTable subTable = (MutableTomlTable)element.value;
               if (!subTable.isDefined()) {
                  subTable.define(position);
                  table.properties.put(key, new MutableTomlTable.Element(subTable, position));
                  return subTable;
               }
            }

            String message = Toml.joinKeyPath(path) + " previously defined at " + element.position;
            throw new TomlParseError(message, position);
         }
      }
   }

   MutableTomlTable createTableArray(List<String> path, TomlPosition position) {
      if (path.isEmpty()) {
         throw new IllegalArgumentException("empty path");
      } else {
         int depth = path.size();
         MutableTomlTable table = this.ensureTable(path.subList(0, depth - 1), position, true, true).table;
         String key = path.get(depth - 1);
         MutableTomlTable.Element element = table.properties
            .computeIfAbsent(key, k -> new MutableTomlTable.Element(MutableTomlArray.create(this.version, true), position));
         if (!(element.value instanceof TomlArray)) {
            String message = Toml.joinKeyPath(path) + " is not an array (previously defined at " + element.position + ")";
            throw new TomlParseError(message, position);
         } else if (element.value instanceof MutableTomlArray && ((MutableTomlArray)element.value).isTableArray()) {
            MutableTomlArray array = (MutableTomlArray)element.value;
            MutableTomlTable newTable = new MutableTomlTable(this.version);
            array.append(newTable, position);
            return newTable;
         } else {
            String message = Toml.joinKeyPath(path) + " previously defined as a literal array at " + element.position;
            throw new TomlParseError(message, position);
         }
      }
   }

   List<SimpleEntry<MutableTomlTable, TomlPosition>> set(String keyPath, Object value, TomlPosition position) {
      return this.set(Parser.parseDottedKey(keyPath), value, position);
   }

   List<SimpleEntry<MutableTomlTable, TomlPosition>> set(List<String> path, Object value, TomlPosition position) {
      int depth = path.size();

      assert depth > 0;

      if (value instanceof Integer) {
         value = ((Integer)value).longValue();
      }

      assert TomlType.typeFor(value).isPresent() : "Unexpected value of type " + value.getClass();

      MutableTomlTable.EnsureTableResult result = this.ensureTable(path.subList(0, depth - 1), position, false, false);
      MutableTomlTable table = result.table;
      MutableTomlTable.Element prevElem = table.properties.putIfAbsent(path.get(depth - 1), new MutableTomlTable.Element(value, position));
      if (prevElem != null) {
         String pathString = Toml.joinKeyPath(path);
         String message = pathString + " previously defined at " + prevElem.position;
         throw new TomlParseError(message, position);
      } else {
         return result.intermediates;
      }
   }

   private MutableTomlTable.EnsureTableResult ensureTable(List<String> path, TomlPosition position, boolean followTableArrays, boolean followDefinedTables) {
      MutableTomlTable table = this;
      int depth = path.size();
      if (depth == 0) {
         return new MutableTomlTable.EnsureTableResult(this, Collections.emptyList());
      } else {
         ArrayList<SimpleEntry<MutableTomlTable, TomlPosition>> elements = new ArrayList<>();
         int i = 0;

         MutableTomlTable.Element element;
         while (true) {
            if (i >= depth) {
               return new MutableTomlTable.EnsureTableResult(table, elements);
            }

            element = table.properties.computeIfAbsent(path.get(i), k -> new MutableTomlTable.Element(new MutableTomlTable(this.version), position));
            if (element.value instanceof MutableTomlTable) {
               table = (MutableTomlTable)element.value;
               if (!followDefinedTables && table.definedAt != null) {
                  String message = Toml.joinKeyPath(path.subList(0, i + 1)) + " already defined at " + table.definedAt;
                  throw new TomlParseError(message, position);
               }

               elements.add(new SimpleEntry<>(table, element.position));
            } else {
               if (element.value instanceof TomlTable) {
                  String message = Toml.joinKeyPath(path.subList(0, i + 1)) + " is not a table (previously defined at " + element.position + ")";
                  throw new TomlParseError(message, position);
               }

               if (!followTableArrays || !(element.value instanceof MutableTomlArray)) {
                  break;
               }

               MutableTomlArray array = (MutableTomlArray)element.value;
               if (!array.isTableArray()) {
                  break;
               }

               assert !array.isEmpty();

               table = (MutableTomlTable)array.get(array.size() - 1);
               elements.add(new SimpleEntry<>(table, element.position));
            }

            i++;
         }

         String message = Toml.joinKeyPath(path.subList(0, i + 1)) + " is not a table (previously defined at " + element.position + ")";
         throw new TomlParseError(message, position);
      }
   }

   private static class Element {
      final Object value;
      final TomlPosition position;

      private Element(Object value, TomlPosition position) {
         this.value = value;
         this.position = position;
      }
   }

   private static class EnsureTableResult {
      final MutableTomlTable table;
      final List<SimpleEntry<MutableTomlTable, TomlPosition>> intermediates;

      private EnsureTableResult(MutableTomlTable table, List<SimpleEntry<MutableTomlTable, TomlPosition>> intermediates) {
         this.table = table;
         this.intermediates = intermediates;
      }
   }
}

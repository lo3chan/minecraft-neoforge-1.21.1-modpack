package amp_libs.org.tomlj;

import amp_libs.org.checkerframework.checker.nullness.qual.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

final class EmptyTomlTable implements TomlTable {
   static final TomlTable EMPTY_TABLE = new EmptyTomlTable();

   private EmptyTomlTable() {
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public Set<String> keySet() {
      return Collections.emptySet();
   }

   @Override
   public Set<List<String>> keyPathSet(boolean includeTables) {
      return Collections.emptySet();
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      return Collections.emptySet();
   }

   @Override
   public Set<Entry<List<String>, Object>> entryPathSet(boolean includeTables) {
      return Collections.emptySet();
   }

   @Nullable
   @Override
   public Object get(List<String> path) {
      return null;
   }

   @Nullable
   @Override
   public TomlPosition inputPositionOf(List<String> path) {
      return null;
   }

   @Override
   public Map<String, Object> toMap() {
      return Collections.emptyMap();
   }
}

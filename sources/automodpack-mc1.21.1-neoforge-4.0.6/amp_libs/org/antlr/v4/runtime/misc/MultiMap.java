package amp_libs.org.antlr.v4.runtime.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MultiMap<K, V> extends LinkedHashMap<K, List<V>> {
   public void map(K key, V value) {
      List<V> elementsForKey = this.get(key);
      if (elementsForKey == null) {
         elementsForKey = new ArrayList<>();
         super.put(key, (V)elementsForKey);
      }

      elementsForKey.add(value);
   }

   public List<Pair<K, V>> getPairs() {
      List<Pair<K, V>> pairs = new ArrayList<>();

      for (K key : this.keySet()) {
         for (V value : this.get(key)) {
            pairs.add(new Pair<>(key, value));
         }
      }

      return pairs;
   }
}

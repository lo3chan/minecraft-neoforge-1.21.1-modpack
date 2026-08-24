package malte0811.ferritecore.fastmap;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class FastMap<Value> {
   private static final int INVALID_INDEX = -1;
   private final List<FastMapKey<?>> keys;
   private final List<Value> valueMatrix;
   private final Reference2IntMap<Property<?>> toKeyIndex;
   private final ReferenceSet<Property<?>> propertySet;

   public FastMap(Collection<Property<?>> properties, Map<Map<Property<?>, Comparable<?>>, Value> valuesMap, boolean compact) {
      List<FastMapKey<?>> keys = new ArrayList<>(properties.size());
      int factorUpTo = 1;
      if (useArrayMapForSize(properties.size())) {
         this.toKeyIndex = new Reference2IntArrayMap();
      } else {
         this.toKeyIndex = new Reference2IntOpenHashMap();
      }

      this.toKeyIndex.defaultReturnValue(-1);

      for (Property<?> prop : properties) {
         this.toKeyIndex.put(prop, keys.size());
         FastMapKey<?> nextKey;
         if (compact) {
            nextKey = new CompactFastMapKey(prop, factorUpTo);
         } else {
            nextKey = new BinaryFastMapKey(prop, factorUpTo);
         }

         keys.add(nextKey);
         factorUpTo *= nextKey.getFactorToNext();
      }

      this.keys = ImmutableList.copyOf(keys);
      List<Value> valuesList = new ArrayList<>(factorUpTo);

      for (int i = 0; i < factorUpTo; i++) {
         valuesList.add(null);
      }

      for (Entry<Map<Property<?>, Comparable<?>>, Value> state : valuesMap.entrySet()) {
         valuesList.set(this.getIndexOf(state.getKey()), state.getValue());
      }

      this.valueMatrix = Collections.unmodifiableList(valuesList);
      if (useArrayMapForSize(properties.size())) {
         this.propertySet = new ReferenceArraySet(properties);
      } else {
         this.propertySet = new ReferenceLinkedOpenHashSet(properties);
      }
   }

   @Nullable
   public Value with(int oldIndex, Property<?> prop, Object value) {
      if (value instanceof Comparable<?> valueComparable) {
         FastMapKey<?> keyToChange = this.getKeyFor(prop);
         if (keyToChange == null) {
            return null;
         } else {
            int newIndex = keyToChange.replaceIn(oldIndex, valueComparable);
            return newIndex < 0 ? null : this.valueMatrix.get(newIndex);
         }
      } else {
         return null;
      }
   }

   public int getIndexOf(Map<Property<?>, Comparable<?>> state) {
      int id = 0;

      for (FastMapKey<?> k : this.keys) {
         id += k.toPartialMapIndex(state.get(k.getProperty()));
      }

      return id;
   }

   @Nullable
   public <T extends Comparable<T>> T getValue(int stateIndex, Property<T> property) {
      FastMapKey<T> propId = this.getKeyFor(property);
      return propId == null ? null : propId.getValue(stateIndex);
   }

   @Nullable
   public Comparable<?> getValue(int stateIndex, Object key) {
      return key instanceof Property ? this.getValue(stateIndex, (Property<Comparable<?>>)key) : null;
   }

   public Entry<Property<?>, Comparable<?>> getEntry(int propertyIndex, int stateIndex) {
      return new SimpleImmutableEntry<>(this.getKey(propertyIndex).getProperty(), this.getKey(propertyIndex).getValue(stateIndex));
   }

   public int numProperties() {
      return this.keys.size();
   }

   public FastMapKey<?> getKey(int keyIndex) {
      return this.keys.get(keyIndex);
   }

   @Nullable
   private <T extends Comparable<T>> FastMapKey<T> getKeyFor(Property<T> prop) {
      int index = this.toKeyIndex.getInt(prop);
      return (FastMapKey<T>)(index == -1 ? null : this.getKey(index));
   }

   public boolean isSingleState() {
      return this.valueMatrix.size() == 1;
   }

   public ReferenceSet<Property<?>> getPropertySet() {
      return this.propertySet;
   }

   private static boolean useArrayMapForSize(int numElements) {
      return numElements < 5;
   }
}

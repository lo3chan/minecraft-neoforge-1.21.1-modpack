package malte0811.ferritecore.impl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2ObjectMap.BasicEntry;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry;
import java.util.Map;
import java.util.Objects;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.fastmap.FastMap;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FastMapEntryMap implements Reference2ObjectMap<Property<?>, Comparable<?>> {
   private final FastMapStateHolder<?> viewedState;

   public FastMapEntryMap(FastMapStateHolder<?> viewedState) {
      this.viewedState = viewedState;
   }

   public int size() {
      return this.getFastMap().numProperties();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean containsKey(Object key) {
      return this.get(key) != null;
   }

   public boolean containsValue(Object value) {
      ObjectIterator var2 = this.keySet().iterator();

      while (var2.hasNext()) {
         Property<?> key = (Property<?>)var2.next();
         if (Objects.equals(value, this.get(key))) {
            return true;
         }
      }

      return false;
   }

   public Comparable<?> get(@Nullable Object key) {
      return this.viewedState.getStateMap().getValue(this.viewedState.getStateIndex(), key);
   }

   @NotNull
   public ReferenceSet<Property<?>> keySet() {
      return this.getFastMap().getPropertySet();
   }

   @NotNull
   public ObjectCollection<Comparable<?>> values() {
      ObjectList<Comparable<?>> values = new ObjectArrayList();
      ObjectIterator var2 = this.keySet().iterator();

      while (var2.hasNext()) {
         Property<?> key = (Property<?>)var2.next();
         values.add(this.get(key));
      }

      return values;
   }

   public void putAll(@NotNull Map<? extends Property<?>, ? extends Comparable<?>> m) {
      throw this.exceptionForMutation();
   }

   public void defaultReturnValue(Comparable<?> comparable) {
      throw this.exceptionForMutation();
   }

   public Comparable<?> defaultReturnValue() {
      return null;
   }

   public ObjectSet<Entry<Property<?>, Comparable<?>>> reference2ObjectEntrySet() {
      ObjectSet<Entry<Property<?>, Comparable<?>>> entries = new ObjectArraySet();
      ObjectIterator var2 = this.keySet().iterator();

      while (var2.hasNext()) {
         Property<?> key = (Property<?>)var2.next();
         entries.add(new BasicEntry(key, this.get(key)));
      }

      return entries;
   }

   private FastMap<?> getFastMap() {
      return this.viewedState.getStateMap();
   }

   private RuntimeException exceptionForMutation() {
      return new UnsupportedOperationException();
   }
}

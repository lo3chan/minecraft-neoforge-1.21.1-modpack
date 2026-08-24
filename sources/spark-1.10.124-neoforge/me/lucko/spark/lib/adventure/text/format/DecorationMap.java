package me.lucko.spark.lib.adventure.text.format;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
final class DecorationMap extends AbstractMap<TextDecoration, TextDecoration.State> implements Examinable {
   static final TextDecoration[] DECORATIONS = TextDecoration.values();
   private static final TextDecoration.State[] STATES = TextDecoration.State.values();
   private static final int MAP_SIZE = DECORATIONS.length;
   private static final TextDecoration.State[] EMPTY_STATE_ARRAY = new TextDecoration.State[0];
   static final DecorationMap EMPTY = new DecorationMap(0);
   private static final DecorationMap.KeySet KEY_SET = new DecorationMap.KeySet();
   private final int bitSet;
   private volatile DecorationMap.EntrySet entrySet = null;
   private volatile DecorationMap.Values values = null;

   static DecorationMap fromMap(final Map<TextDecoration, TextDecoration.State> decorationMap) {
      if (decorationMap instanceof DecorationMap) {
         return (DecorationMap)decorationMap;
      } else {
         int bitSet = 0;

         for (TextDecoration decoration : DECORATIONS) {
            bitSet |= decorationMap.getOrDefault(decoration, TextDecoration.State.NOT_SET).ordinal() * offset(decoration);
         }

         return withBitSet(bitSet);
      }
   }

   static DecorationMap merge(final Map<TextDecoration, TextDecoration.State> first, final Map<TextDecoration, TextDecoration.State> second) {
      int bitSet = 0;

      for (TextDecoration decoration : DECORATIONS) {
         bitSet |= first.getOrDefault(decoration, second.getOrDefault(decoration, TextDecoration.State.NOT_SET)).ordinal() * offset(decoration);
      }

      return withBitSet(bitSet);
   }

   private static DecorationMap withBitSet(final int bitSet) {
      return bitSet == 0 ? EMPTY : new DecorationMap(bitSet);
   }

   private static int offset(final TextDecoration decoration) {
      return 1 << decoration.ordinal() * 2;
   }

   private DecorationMap(final int bitSet) {
      this.bitSet = bitSet;
   }

   @NotNull
   public DecorationMap with(@NotNull final TextDecoration decoration, @NotNull final TextDecoration.State state) {
      Objects.requireNonNull(state, "state");
      Objects.requireNonNull(decoration, "decoration");
      int offset = offset(decoration);
      return withBitSet(this.bitSet & ~(3 * offset) | state.ordinal() * offset);
   }

   @NotNull
   @Override
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Arrays.stream(DECORATIONS).map(decoration -> ExaminableProperty.of(decoration.toString(), this.get(decoration)));
   }

   public TextDecoration.State get(final Object o) {
      return o instanceof TextDecoration ? STATES[this.bitSet >> ((TextDecoration)o).ordinal() * 2 & 3] : null;
   }

   @Override
   public boolean containsKey(final Object key) {
      return key instanceof TextDecoration;
   }

   @Override
   public int size() {
      return MAP_SIZE;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @NotNull
   @Override
   public Set<Entry<TextDecoration, TextDecoration.State>> entrySet() {
      if (this.entrySet == null) {
         synchronized (this) {
            if (this.entrySet == null) {
               this.entrySet = new DecorationMap.EntrySet();
            }
         }
      }

      return this.entrySet;
   }

   @NotNull
   @Override
   public Set<TextDecoration> keySet() {
      return KEY_SET;
   }

   @NotNull
   @Override
   public Collection<TextDecoration.State> values() {
      if (this.values == null) {
         synchronized (this) {
            if (this.values == null) {
               this.values = new DecorationMap.Values();
            }
         }
      }

      return this.values;
   }

   @Override
   public boolean equals(final Object other) {
      if (other == this) {
         return true;
      } else {
         return other != null && other.getClass() == DecorationMap.class ? this.bitSet == ((DecorationMap)other).bitSet : false;
      }
   }

   @Override
   public int hashCode() {
      return this.bitSet;
   }

   final class EntrySet extends AbstractSet<Entry<TextDecoration, TextDecoration.State>> {
      @NotNull
      @Override
      public Iterator<Entry<TextDecoration, TextDecoration.State>> iterator() {
         return new Iterator<Entry<TextDecoration, TextDecoration.State>>() {
            private final Iterator<TextDecoration> decorations = DecorationMap.KEY_SET.iterator();
            private final Iterator<TextDecoration.State> states = DecorationMap.this.values().iterator();

            @Override
            public boolean hasNext() {
               return this.decorations.hasNext() && this.states.hasNext();
            }

            public Entry<TextDecoration, TextDecoration.State> next() {
               if (this.hasNext()) {
                  return new SimpleImmutableEntry<>(this.decorations.next(), this.states.next());
               } else {
                  throw new NoSuchElementException();
               }
            }
         };
      }

      @Override
      public int size() {
         return DecorationMap.MAP_SIZE;
      }
   }

   static final class KeySet extends AbstractSet<TextDecoration> {
      @Override
      public boolean contains(final Object o) {
         return o instanceof TextDecoration;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @NotNull
      @Override
      public Object[] toArray() {
         return Arrays.copyOf(DecorationMap.DECORATIONS, DecorationMap.MAP_SIZE, Object[].class);
      }

      @NotNull
      @Override
      public <T> T[] toArray(@NotNull final T[] dest) {
         if (dest.length < DecorationMap.MAP_SIZE) {
            return (T[])Arrays.copyOf(DecorationMap.DECORATIONS, DecorationMap.MAP_SIZE, (Class<? extends T[]>)dest.getClass());
         } else {
            System.arraycopy(DecorationMap.DECORATIONS, 0, dest, 0, DecorationMap.MAP_SIZE);
            if (dest.length > DecorationMap.MAP_SIZE) {
               dest[DecorationMap.MAP_SIZE] = null;
            }

            return dest;
         }
      }

      @NotNull
      @Override
      public Iterator<TextDecoration> iterator() {
         return Spliterators.iterator(Arrays.spliterator(DecorationMap.DECORATIONS));
      }

      @Override
      public int size() {
         return DecorationMap.MAP_SIZE;
      }
   }

   final class Values extends AbstractCollection<TextDecoration.State> {
      @NotNull
      @Override
      public Iterator<TextDecoration.State> iterator() {
         return Spliterators.iterator(Arrays.spliterator(this.toArray(DecorationMap.EMPTY_STATE_ARRAY)));
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @NotNull
      @Override
      public Object[] toArray() {
         Object[] states = new Object[DecorationMap.MAP_SIZE];

         for (int i = 0; i < DecorationMap.MAP_SIZE; i++) {
            states[i] = DecorationMap.this.get(DecorationMap.DECORATIONS[i]);
         }

         return states;
      }

      @NotNull
      @Override
      public <T> T[] toArray(@NotNull final T[] dest) {
         if (dest.length < DecorationMap.MAP_SIZE) {
            return (T[])Arrays.copyOf(this.toArray(), DecorationMap.MAP_SIZE, (Class<? extends T[]>)dest.getClass());
         } else {
            System.arraycopy(this.toArray(), 0, dest, 0, DecorationMap.MAP_SIZE);
            if (dest.length > DecorationMap.MAP_SIZE) {
               dest[DecorationMap.MAP_SIZE] = null;
            }

            return dest;
         }
      }

      @Override
      public boolean contains(final Object o) {
         return o instanceof TextDecoration.State && super.contains(o);
      }

      @Override
      public int size() {
         return DecorationMap.MAP_SIZE;
      }
   }
}

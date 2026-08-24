package me.lucko.spark.lib.adventure.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class InheritanceAwareMapImpl<C, V> implements InheritanceAwareMap<C, V> {
   private static final Object NONE = new Object();
   static final InheritanceAwareMapImpl EMPTY = new InheritanceAwareMapImpl(false, Collections.emptyMap());
   private final Map<Class<? extends C>, V> declaredValues;
   private final boolean strict;
   private final transient ConcurrentMap<Class<? extends C>, Object> cache = new ConcurrentHashMap<>();

   InheritanceAwareMapImpl(final boolean strict, final Map<Class<? extends C>, V> declaredValues) {
      this.strict = strict;
      this.declaredValues = declaredValues;
   }

   @Override
   public boolean containsKey(@NotNull final Class<? extends C> clazz) {
      return this.get(clazz) != null;
   }

   @Nullable
   @Override
   public V get(@NotNull final Class<? extends C> clazz) {
      Object ret = this.cache.computeIfAbsent(clazz, c -> {
         V value = this.declaredValues.get(c);
         if (value != null) {
            return value;
         } else {
            for (Entry<Class<? extends C>, V> entry : this.declaredValues.entrySet()) {
               if (entry.getKey().isAssignableFrom((Class<?>)c)) {
                  return entry.getValue();
               }
            }

            return NONE;
         }
      });
      return (V)(ret == NONE ? null : ret);
   }

   @NotNull
   @Override
   public InheritanceAwareMap<C, V> with(@NotNull final Class<? extends C> clazz, @NotNull final V value) {
      if (Objects.equals(this.declaredValues.get(clazz), value)) {
         return this;
      } else {
         if (this.strict) {
            validateNoneInHierarchy(clazz, this.declaredValues);
         }

         Map<Class<? extends C>, V> newValues = new LinkedHashMap<>(this.declaredValues);
         newValues.put(clazz, value);
         return new InheritanceAwareMapImpl<>(this.strict, Collections.unmodifiableMap(newValues));
      }
   }

   @NotNull
   @Override
   public InheritanceAwareMap<C, V> without(@NotNull final Class<? extends C> clazz) {
      if (!this.declaredValues.containsKey(clazz)) {
         return this;
      } else {
         Map<Class<? extends C>, V> newValues = new LinkedHashMap<>(this.declaredValues);
         newValues.remove(clazz);
         return new InheritanceAwareMapImpl<>(this.strict, Collections.unmodifiableMap(newValues));
      }
   }

   private static void validateNoneInHierarchy(final Class<?> beingRegistered, final Map<? extends Class<?>, ?> entries) {
      for (Class<?> clazz : entries.keySet()) {
         testHierarchy(clazz, beingRegistered);
      }
   }

   private static void testHierarchy(final Class<?> existing, final Class<?> beingRegistered) {
      if (!existing.equals(beingRegistered) && (existing.isAssignableFrom(beingRegistered) || beingRegistered.isAssignableFrom(existing))) {
         throw new IllegalArgumentException(
            "Conflict detected between already registered type "
               + existing
               + " and newly registered type "
               + beingRegistered
               + "! Types in a strict inheritance-aware map must not share a common hierarchy!"
         );
      }
   }

   static final class BuilderImpl<C, V> implements InheritanceAwareMap.Builder<C, V> {
      private boolean strict;
      private final Map<Class<? extends C>, V> values = new LinkedHashMap<>();

      @NotNull
      public InheritanceAwareMap<C, V> build() {
         return new InheritanceAwareMapImpl<>(this.strict, Collections.unmodifiableMap(new LinkedHashMap<>(this.values)));
      }

      @NotNull
      @Override
      public InheritanceAwareMap.Builder<C, V> strict(final boolean strict) {
         if (strict && !this.strict) {
            for (Class<? extends C> clazz : this.values.keySet()) {
               InheritanceAwareMapImpl.validateNoneInHierarchy(clazz, this.values);
            }
         }

         this.strict = strict;
         return this;
      }

      @NotNull
      @Override
      public InheritanceAwareMap.Builder<C, V> put(@NotNull final Class<? extends C> clazz, @NotNull final V value) {
         if (this.strict) {
            InheritanceAwareMapImpl.validateNoneInHierarchy(clazz, this.values);
         }

         this.values.put(Objects.requireNonNull(clazz, "clazz"), Objects.requireNonNull(value, "value"));
         return this;
      }

      @NotNull
      @Override
      public InheritanceAwareMap.Builder<C, V> remove(@NotNull final Class<? extends C> clazz) {
         this.values.remove(Objects.requireNonNull(clazz, "clazz"));
         return this;
      }

      @NotNull
      @Override
      public InheritanceAwareMap.Builder<C, V> putAll(@NotNull final InheritanceAwareMap<? extends C, ? extends V> map) {
         InheritanceAwareMapImpl<?, V> impl = (InheritanceAwareMapImpl<?, V>)map;
         if (this.strict && (!this.values.isEmpty() || !impl.strict)) {
            for (Entry<? extends Class<?>, V> entry : impl.declaredValues.entrySet()) {
               InheritanceAwareMapImpl.validateNoneInHierarchy((Class<?>)entry.getKey(), this.values);
               this.values.put((Class<? extends C>)entry.getKey(), entry.getValue());
            }

            return this;
         } else {
            this.values.putAll(impl.declaredValues);
            return this;
         }
      }
   }
}

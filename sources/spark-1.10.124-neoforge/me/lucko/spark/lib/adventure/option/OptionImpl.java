package me.lucko.spark.lib.adventure.option;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class OptionImpl<V> implements Option<V> {
   private static final Set<String> KNOWN_KEYS = ConcurrentHashMap.newKeySet();
   private final String id;
   private final Class<V> type;
   @Nullable
   private final V defaultValue;

   OptionImpl(@NotNull final String id, @NotNull final Class<V> type, @Nullable final V defaultValue) {
      this.id = id;
      this.type = type;
      this.defaultValue = defaultValue;
   }

   static <T> Option<T> option(final String id, final Class<T> type, @Nullable final T defaultValue) {
      if (!KNOWN_KEYS.add(id)) {
         throw new IllegalStateException("Key " + id + " has already been used. Option keys must be unique.");
      } else {
         return new OptionImpl<>(Objects.requireNonNull(id, "id"), Objects.requireNonNull(type, "type"), defaultValue);
      }
   }

   @NotNull
   @Override
   public String id() {
      return this.id;
   }

   @NotNull
   @Override
   public Class<V> type() {
      return this.type;
   }

   @Nullable
   @Override
   public V defaultValue() {
      return this.defaultValue;
   }

   @Override
   public boolean equals(@Nullable final Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         OptionImpl<?> that = (OptionImpl<?>)other;
         return Objects.equals(this.id, that.id) && Objects.equals(this.type, that.type);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id, this.type);
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "{id=" + this.id + ",type=" + this.type + ",defaultValue=" + this.defaultValue + '}';
   }
}

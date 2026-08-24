package me.lucko.spark.lib.adventure.option;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class OptionStateImpl implements OptionState {
   static final OptionState EMPTY = new OptionStateImpl(new IdentityHashMap<>());
   private final IdentityHashMap<Option<?>, Object> values;

   OptionStateImpl(final IdentityHashMap<Option<?>, Object> values) {
      this.values = new IdentityHashMap<>(values);
   }

   @Override
   public boolean has(@NotNull final Option<?> option) {
      return this.values.containsKey(Objects.requireNonNull(option, "flag"));
   }

   @Override
   public <V> V value(@NotNull final Option<V> option) {
      V value = option.type().cast(this.values.get(Objects.requireNonNull(option, "flag")));
      return value == null ? option.defaultValue() : value;
   }

   @Override
   public boolean equals(@Nullable final Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         OptionStateImpl that = (OptionStateImpl)other;
         return Objects.equals(this.values, that.values);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.values);
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "{values=" + this.values + '}';
   }

   static final class BuilderImpl implements OptionState.Builder {
      private final IdentityHashMap<Option<?>, Object> values = new IdentityHashMap<>();

      @NotNull
      @Override
      public OptionState build() {
         return (OptionState)(this.values.isEmpty() ? OptionStateImpl.EMPTY : new OptionStateImpl(this.values));
      }

      @NotNull
      @Override
      public <V> OptionState.Builder value(@NotNull final Option<V> option, @NotNull final V value) {
         this.values.put(Objects.requireNonNull(option, "flag"), Objects.requireNonNull(value, "value"));
         return this;
      }

      @NotNull
      @Override
      public OptionState.Builder values(@NotNull final OptionState existing) {
         if (existing instanceof OptionStateImpl) {
            this.values.putAll(((OptionStateImpl)existing).values);
         } else {
            if (!(existing instanceof OptionStateImpl.VersionedImpl)) {
               throw new IllegalArgumentException("existing set " + existing + " is of an unknown implementation type");
            }

            this.values.putAll(((OptionStateImpl)((OptionStateImpl.VersionedImpl)existing).filtered).values);
         }

         return this;
      }
   }

   static final class VersionedBuilderImpl implements OptionState.VersionedBuilder {
      private final Map<Integer, OptionStateImpl.BuilderImpl> builders = new TreeMap<>();

      @NotNull
      @Override
      public OptionState.Versioned build() {
         if (this.builders.isEmpty()) {
            return new OptionStateImpl.VersionedImpl(Collections.emptySortedMap(), 0, OptionState.emptyOptionState());
         } else {
            SortedMap<Integer, OptionState> built = new TreeMap<>();

            for (Entry<Integer, OptionStateImpl.BuilderImpl> entry : this.builders.entrySet()) {
               built.put(entry.getKey(), entry.getValue().build());
            }

            return new OptionStateImpl.VersionedImpl(built, built.lastKey(), OptionStateImpl.VersionedImpl.flattened(built, built.lastKey()));
         }
      }

      @NotNull
      @Override
      public OptionState.VersionedBuilder version(final int version, @NotNull final Consumer<OptionState.Builder> versionBuilder) {
         Objects.requireNonNull(versionBuilder, "versionBuilder").accept(this.builders.computeIfAbsent(version, $ -> new OptionStateImpl.BuilderImpl()));
         return this;
      }
   }

   static final class VersionedImpl implements OptionState.Versioned {
      private final SortedMap<Integer, OptionState> sets;
      private final int targetVersion;
      private final OptionState filtered;

      VersionedImpl(final SortedMap<Integer, OptionState> sets, final int targetVersion, final OptionState filtered) {
         this.sets = sets;
         this.targetVersion = targetVersion;
         this.filtered = filtered;
      }

      @Override
      public boolean has(@NotNull final Option<?> option) {
         return this.filtered.has(option);
      }

      @Override
      public <V> V value(@NotNull final Option<V> option) {
         return this.filtered.value(option);
      }

      @NotNull
      @Override
      public Map<Integer, OptionState> childStates() {
         return Collections.unmodifiableSortedMap(this.sets.headMap(this.targetVersion + 1));
      }

      @NotNull
      @Override
      public OptionState.Versioned at(final int version) {
         return new OptionStateImpl.VersionedImpl(this.sets, version, flattened(this.sets, version));
      }

      public static OptionState flattened(final SortedMap<Integer, OptionState> versions, final int targetVersion) {
         Map<Integer, OptionState> applicable = versions.headMap(targetVersion + 1);
         OptionState.Builder builder = OptionState.optionState();

         for (OptionState child : applicable.values()) {
            builder.values(child);
         }

         return builder.build();
      }

      @Override
      public boolean equals(@Nullable final Object other) {
         if (this == other) {
            return true;
         } else if (other != null && this.getClass() == other.getClass()) {
            OptionStateImpl.VersionedImpl that = (OptionStateImpl.VersionedImpl)other;
            return this.targetVersion == that.targetVersion && Objects.equals(this.sets, that.sets) && Objects.equals(this.filtered, that.filtered);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.sets, this.targetVersion, this.filtered);
      }

      @Override
      public String toString() {
         return this.getClass().getSimpleName() + "{sets=" + this.sets + ", targetVersion=" + this.targetVersion + ", filtered=" + this.filtered + '}';
      }
   }
}

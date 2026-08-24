package me.lucko.spark.lib.adventure.option;

import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface OptionState {
   static OptionState emptyOptionState() {
      return OptionStateImpl.EMPTY;
   }

   @NotNull
   static OptionState.Builder optionState() {
      return new OptionStateImpl.BuilderImpl();
   }

   @NotNull
   static OptionState.VersionedBuilder versionedOptionState() {
      return new OptionStateImpl.VersionedBuilderImpl();
   }

   boolean has(@NotNull final Option<?> option);

   <V> V value(@NotNull final Option<V> option);

   @NonExtendable
   public interface Builder {
      @NotNull
      <V> OptionState.Builder value(@NotNull final Option<V> option, @NotNull final V value);

      @NotNull
      OptionState.Builder values(@NotNull final OptionState existing);

      @NotNull
      OptionState build();
   }

   @NonExtendable
   public interface Versioned extends OptionState {
      @NotNull
      Map<Integer, OptionState> childStates();

      @NotNull
      OptionState.Versioned at(final int version);
   }

   @NonExtendable
   public interface VersionedBuilder {
      @NotNull
      OptionState.VersionedBuilder version(final int version, @NotNull final Consumer<OptionState.Builder> versionBuilder);

      @NotNull
      OptionState.Versioned build();
   }
}

package me.lucko.spark.lib.adventure.text.serializer.json;

import java.util.function.Consumer;
import me.lucko.spark.lib.adventure.option.OptionState;
import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class DummyJSONComponentSerializer implements JSONComponentSerializer {
   static final JSONComponentSerializer INSTANCE = new DummyJSONComponentSerializer();
   private static final String UNSUPPORTED_MESSAGE = "No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?";

   @NotNull
   public Component deserialize(@NotNull final String input) {
      throw new UnsupportedOperationException(
         "No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?"
      );
   }

   @NotNull
   public String serialize(@NotNull final Component component) {
      throw new UnsupportedOperationException(
         "No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?"
      );
   }

   static final class BuilderImpl implements JSONComponentSerializer.Builder {
      @NotNull
      @Override
      public JSONComponentSerializer.Builder options(@NotNull final OptionState flags) {
         return this;
      }

      @NotNull
      @Override
      public JSONComponentSerializer.Builder editOptions(@NotNull final Consumer<OptionState.Builder> optionEditor) {
         return this;
      }

      @Deprecated
      @NotNull
      @Override
      public JSONComponentSerializer.Builder downsampleColors() {
         return this;
      }

      @NotNull
      @Override
      public JSONComponentSerializer.Builder legacyHoverEventSerializer(@Nullable final LegacyHoverEventSerializer serializer) {
         return this;
      }

      @Deprecated
      @NotNull
      @Override
      public JSONComponentSerializer.Builder emitLegacyHoverEvent() {
         return this;
      }

      @NotNull
      @Override
      public JSONComponentSerializer build() {
         return DummyJSONComponentSerializer.INSTANCE;
      }
   }
}

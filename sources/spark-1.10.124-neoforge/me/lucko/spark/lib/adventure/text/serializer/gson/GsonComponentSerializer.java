package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import me.lucko.spark.lib.adventure.builder.AbstractBuilder;
import me.lucko.spark.lib.adventure.option.OptionState;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.serializer.json.JSONComponentSerializer;
import me.lucko.spark.lib.adventure.text.serializer.json.JSONOptions;
import me.lucko.spark.lib.adventure.util.Buildable;
import me.lucko.spark.lib.adventure.util.PlatformAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface GsonComponentSerializer extends JSONComponentSerializer, Buildable<GsonComponentSerializer, GsonComponentSerializer.Builder> {
   @NotNull
   static GsonComponentSerializer gson() {
      return GsonComponentSerializerImpl.Instances.INSTANCE;
   }

   @NotNull
   static GsonComponentSerializer colorDownsamplingGson() {
      return GsonComponentSerializerImpl.Instances.LEGACY_INSTANCE;
   }

   static GsonComponentSerializer.Builder builder() {
      return new GsonComponentSerializerImpl.BuilderImpl();
   }

   @NotNull
   Gson serializer();

   @NotNull
   UnaryOperator<GsonBuilder> populator();

   @NotNull
   Component deserializeFromTree(@NotNull final JsonElement input);

   @NotNull
   JsonElement serializeToTree(@NotNull final Component component);

   public interface Builder extends AbstractBuilder<GsonComponentSerializer>, Buildable.Builder<GsonComponentSerializer>, JSONComponentSerializer.Builder {
      @NotNull
      GsonComponentSerializer.Builder options(@NotNull final OptionState flags);

      @NotNull
      GsonComponentSerializer.Builder editOptions(@NotNull final Consumer<OptionState.Builder> optionEditor);

      @NotNull
      default GsonComponentSerializer.Builder downsampleColors() {
         return this.editOptions(features -> features.value(JSONOptions.EMIT_RGB, false));
      }

      @Deprecated
      @NotNull
      default GsonComponentSerializer.Builder legacyHoverEventSerializer(@Nullable final LegacyHoverEventSerializer serializer) {
         return this.legacyHoverEventSerializer((me.lucko.spark.lib.adventure.text.serializer.json.LegacyHoverEventSerializer)serializer);
      }

      @NotNull
      GsonComponentSerializer.Builder legacyHoverEventSerializer(
         @Nullable final me.lucko.spark.lib.adventure.text.serializer.json.LegacyHoverEventSerializer serializer
      );

      @Deprecated
      @NotNull
      default GsonComponentSerializer.Builder emitLegacyHoverEvent() {
         return this.editOptions(b -> b.value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.BOTH));
      }

      @NotNull
      GsonComponentSerializer build();
   }

   @PlatformAPI
   @Internal
   public interface Provider {
      @PlatformAPI
      @Internal
      @NotNull
      GsonComponentSerializer gson();

      @PlatformAPI
      @Internal
      @NotNull
      GsonComponentSerializer gsonLegacy();

      @PlatformAPI
      @Internal
      @NotNull
      Consumer<GsonComponentSerializer.Builder> builder();
   }
}

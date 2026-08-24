package me.lucko.spark.lib.adventure.text.serializer.gson.impl;

import com.google.auto.service.AutoService;
import java.util.function.Supplier;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonComponentSerializer;
import me.lucko.spark.lib.adventure.text.serializer.json.JSONComponentSerializer;
import me.lucko.spark.lib.adventure.util.Services;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService({JSONComponentSerializer.Provider.class})
public final class JSONComponentSerializerProviderImpl implements JSONComponentSerializer.Provider, Services.Fallback {
   @NotNull
   @Override
   public JSONComponentSerializer instance() {
      return GsonComponentSerializer.gson();
   }

   @NotNull
   @Override
   public Supplier<JSONComponentSerializer.Builder> builder() {
      return GsonComponentSerializer::builder;
   }

   @Override
   public String toString() {
      return "JSONComponentSerializerProviderImpl[GsonComponentSerializer]";
   }
}

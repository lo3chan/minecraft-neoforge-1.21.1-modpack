package me.lucko.spark.lib.adventure.text.serializer.json;

import java.util.Optional;
import java.util.function.Supplier;
import me.lucko.spark.lib.adventure.util.Services;

final class JSONComponentSerializerAccessor {
   private static final Optional<JSONComponentSerializer.Provider> SERVICE = Services.serviceWithFallback(JSONComponentSerializer.Provider.class);

   private JSONComponentSerializerAccessor() {
   }

   static final class Instances {
      static final JSONComponentSerializer INSTANCE = JSONComponentSerializerAccessor.SERVICE
         .map(JSONComponentSerializer.Provider::instance)
         .orElse(DummyJSONComponentSerializer.INSTANCE);
      static final Supplier<JSONComponentSerializer.Builder> BUILDER_SUPPLIER = JSONComponentSerializerAccessor.SERVICE
         .map(JSONComponentSerializer.Provider::builder)
         .orElse(DummyJSONComponentSerializer.BuilderImpl::new);
   }
}

package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.ComponentLike;
import me.lucko.spark.lib.adventure.text.TranslationArgument;

final class TranslationArgumentSerializer extends TypeAdapter<TranslationArgument> {
   private final Gson gson;

   static TypeAdapter<TranslationArgument> create(final Gson gson) {
      return new TranslationArgumentSerializer(gson).nullSafe();
   }

   private TranslationArgumentSerializer(final Gson gson) {
      this.gson = gson;
   }

   public void write(final JsonWriter out, final TranslationArgument value) throws IOException {
      Object raw = value.value();
      if (raw instanceof Boolean) {
         out.value((Boolean)raw);
      } else if (raw instanceof Number) {
         out.value((Number)raw);
      } else {
         if (!(raw instanceof Component)) {
            throw new IllegalStateException("Unable to serialize translatable argument of type " + raw.getClass() + ": " + raw);
         }

         this.gson.toJson(raw, SerializerFactory.COMPONENT_TYPE, out);
      }
   }

   public TranslationArgument read(final JsonReader in) throws IOException {
      switch (in.peek()) {
         case BOOLEAN:
            return TranslationArgument.bool(in.nextBoolean());
         case NUMBER:
            return TranslationArgument.numeric((Number)this.gson.fromJson(in, Number.class));
         default:
            return TranslationArgument.component((ComponentLike)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE));
      }
   }
}

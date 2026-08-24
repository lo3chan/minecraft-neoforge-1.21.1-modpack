package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.UUID;
import me.lucko.spark.lib.adventure.option.OptionState;
import me.lucko.spark.lib.adventure.text.serializer.json.JSONOptions;

final class UUIDSerializer extends TypeAdapter<UUID> {
   private final boolean emitIntArray;

   static TypeAdapter<UUID> uuidSerializer(final OptionState features) {
      return new UUIDSerializer(features.value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY)).nullSafe();
   }

   private UUIDSerializer(final boolean emitIntArray) {
      this.emitIntArray = emitIntArray;
   }

   public void write(final JsonWriter out, final UUID value) throws IOException {
      if (this.emitIntArray) {
         int msb0 = (int)(value.getMostSignificantBits() >> 32);
         int msb1 = (int)(value.getMostSignificantBits() & 4294967295L);
         int lsb0 = (int)(value.getLeastSignificantBits() >> 32);
         int lsb1 = (int)(value.getLeastSignificantBits() & 4294967295L);
         out.beginArray().value(msb0).value(msb1).value(lsb0).value(lsb1).endArray();
      } else {
         out.value(value.toString());
      }
   }

   public UUID read(final JsonReader in) throws IOException {
      if (in.peek() == JsonToken.BEGIN_ARRAY) {
         in.beginArray();
         int msb0 = in.nextInt();
         int msb1 = in.nextInt();
         int lsb0 = in.nextInt();
         int lsb1 = in.nextInt();
         in.endArray();
         return new UUID((long)msb0 << 32 | msb1 & 4294967295L, (long)lsb0 << 32 | lsb1 & 4294967295L);
      } else {
         return UUID.fromString(in.nextString());
      }
   }
}

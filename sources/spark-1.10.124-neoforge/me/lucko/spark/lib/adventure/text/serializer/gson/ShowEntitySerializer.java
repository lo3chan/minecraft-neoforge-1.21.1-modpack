package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.UUID;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;

final class ShowEntitySerializer extends TypeAdapter<HoverEvent.ShowEntity> {
   private final Gson gson;

   static TypeAdapter<HoverEvent.ShowEntity> create(final Gson gson) {
      return new ShowEntitySerializer(gson).nullSafe();
   }

   private ShowEntitySerializer(final Gson gson) {
      this.gson = gson;
   }

   public HoverEvent.ShowEntity read(final JsonReader in) throws IOException {
      in.beginObject();
      Key type = null;
      UUID id = null;
      Component name = null;

      while (in.hasNext()) {
         String fieldName = in.nextName();
         if (fieldName.equals("type")) {
            type = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
         } else if (fieldName.equals("id")) {
            id = (UUID)this.gson.fromJson(in, SerializerFactory.UUID_TYPE);
         } else if (fieldName.equals("name")) {
            name = (Component)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE);
         } else {
            in.skipValue();
         }
      }

      if (type != null && id != null) {
         in.endObject();
         return HoverEvent.ShowEntity.showEntity(type, id, name);
      } else {
         throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
      }
   }

   public void write(final JsonWriter out, final HoverEvent.ShowEntity value) throws IOException {
      out.beginObject();
      out.name("type");
      this.gson.toJson(value.type(), SerializerFactory.KEY_TYPE, out);
      out.name("id");
      this.gson.toJson(value.id(), SerializerFactory.UUID_TYPE, out);
      Component name = value.name();
      if (name != null) {
         out.name("name");
         this.gson.toJson(name, SerializerFactory.COMPONENT_TYPE, out);
      }

      out.endObject();
   }
}

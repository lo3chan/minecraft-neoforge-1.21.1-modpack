package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Component.Serializer;
import vazkii.patchouli.api.IVariableSerializer;

public class TextComponentVariableSerializer implements IVariableSerializer<Component> {
   public Component fromJson(JsonElement json, Provider registries) {
      if (json.isJsonNull()) {
         return Component.literal("");
      } else {
         return json.isJsonPrimitive() ? Component.literal(json.getAsString()) : Serializer.fromJson(json, registries);
      }
   }

   public JsonElement toJson(Component stack, Provider registries) {
      return (JsonElement)ComponentSerialization.CODEC
         .encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), stack)
         .getOrThrow(JsonParseException::new);
   }
}

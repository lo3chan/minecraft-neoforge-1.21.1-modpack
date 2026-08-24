package de.cristelknight.cristellib.data.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.config.FileWriter;
import java.util.Map.Entry;
import net.minecraft.util.GsonHelper;

public interface ICondition<T extends ICondition<T>> {
   Codec<ICondition<?>> FULL_CODEC = Codec.PASSTHROUGH
      .xmap(dynamic -> decode((JsonElement)dynamic.convert(JsonOps.INSTANCE).getValue()), condition -> new Dynamic(JsonOps.INSTANCE, encode((T)condition)));

   private static ICondition<?> decode(JsonElement e) {
      if (e instanceof JsonObject object) {
         String type = GsonHelper.getAsString(object, "type");
         Codec conditionCodec = ConditionRegistry.getCodec(type);
         object.remove("type");
         return FileWriter.loadFromElement("Couldn't read ICondition of type: " + type, conditionCodec, JsonOps.INSTANCE, object);
      } else {
         throw new RuntimeException(Constants.getWithPrefix("Expected ICondition to be an Object"));
      }
   }

   private static <T extends ICondition<?>> JsonObject encode(T condition) {
      Codec<T> codec = (Codec<T>)condition.getCodec();
      String type = ConditionRegistry.getType(codec);
      JsonElement e = FileWriter.writeToElement("Couldn't encode ICondition", codec, JsonOps.INSTANCE, condition);
      if (!(e instanceof JsonObject object)) {
         throw new RuntimeException(Constants.getWithPrefix("Expected ICondition to be an Object"));
      } else {
         JsonObject reordered = new JsonObject();
         reordered.addProperty("type", type);

         for (Entry<String, JsonElement> entry : object.entrySet()) {
            reordered.add(entry.getKey(), entry.getValue());
         }

         return reordered;
      }
   }

   boolean test();

   Codec<T> getCodec();
}

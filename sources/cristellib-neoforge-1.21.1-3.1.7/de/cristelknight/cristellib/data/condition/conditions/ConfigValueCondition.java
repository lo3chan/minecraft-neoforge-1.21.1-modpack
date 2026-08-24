package de.cristelknight.cristellib.data.condition.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.config.FileWriter;
import de.cristelknight.cristellib.config.simple.ConfigHolder;
import de.cristelknight.cristellib.config.simple.ConfigRegistry;
import de.cristelknight.cristellib.data.condition.ICondition;
import de.cristelknight.cristellib.util.JsonHelper;

public record ConfigValueCondition(String className, String key, JsonElement expected) implements ICondition<ConfigValueCondition> {
   private static final Codec<JsonElement> JSON_CODEC = Codec.PASSTHROUGH
      .xmap(dynamic -> (JsonElement)dynamic.convert(JsonOps.INSTANCE).getValue(), condition -> new Dynamic(JsonOps.INSTANCE, condition));
   public static final Codec<ConfigValueCondition> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.fieldOf("class_name").forGetter(ConfigValueCondition::className),
            Codec.STRING.fieldOf("key").forGetter(ConfigValueCondition::key),
            JSON_CODEC.fieldOf("expected").forGetter(ConfigValueCondition::expected)
         )
         .apply(instance, ConfigValueCondition::new)
   );

   @Override
   public boolean test() {
      Class<?> clazz;
      try {
         clazz = Class.forName(this.className);
      } catch (ClassNotFoundException var7) {
         Constants.LOG.warn("Couldn't parse class_name: {} for ConfigValueCondition", this.className);
         return false;
      }

      if (fromClass(clazz) instanceof JsonObject object) {
         for (JsonElement actual : JsonHelper.findAll(this.key, object, "")) {
            if (actual.equals(this.expected)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static <T> JsonElement fromClass(Class<T> clazz) {
      ConfigHolder<T> holder = ConfigRegistry.holder(clazz);
      return FileWriter.writeToElement(
         "Couldn't write config for class: " + clazz.getName(), holder.getSettings().getCodec(), JsonOps.INSTANCE, holder.getInstance()
      );
   }

   @Override
   public Codec<ConfigValueCondition> getCodec() {
      return CODEC;
   }
}

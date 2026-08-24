package net.mehvahdjukaar.moonlight.api.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.function.BiFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;

public class NestedModelLoader implements CustomModelLoader {
   private final BiFunction<BakedModel, ModelState, CustomBakedModel> factory;
   private final String path;

   public NestedModelLoader(String modelPath, BiFunction<BakedModel, ModelState, CustomBakedModel> bakedModelFactory) {
      this.factory = bakedModelFactory;
      this.path = modelPath;
   }

   @Override
   public CustomGeometry deserialize(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
      JsonElement j = json.get(this.path);
      return (modelBaker, spriteGetter, transform) -> {
         BakedModel baked = CustomModelLoader.parseModel(j, modelBaker, spriteGetter, transform);
         return this.factory.apply(baked, transform);
      };
   }
}

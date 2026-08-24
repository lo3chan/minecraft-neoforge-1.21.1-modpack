package org.dimdev.limlib.client.specialmodels.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModel.Deserializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.dimdev.limlib.client.specialmodels.SpecialModelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Deserializer.class})
public class BlockModelDeserializerMixin {
   @Unique
   private static final String CORNERS_SPECIAL_MODELS_KEY = "specialmodels";

   @Inject(
      method = {"deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;"},
      at = {@At("RETURN")}
   )
   private void corners$readSpecialModels(JsonElement jsonElement, Type type, JsonDeserializationContext context, CallbackInfoReturnable<BlockModel> cir) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      JsonObject specialModels = GsonHelper.getAsJsonObject(jsonObject, "specialmodels", null);
      if (specialModels != null) {
         Map<ResourceLocation, ResourceLocation> parsed = new LinkedHashMap<>();

         for (Entry<String, JsonElement> entry : specialModels.entrySet()) {
            ResourceLocation rendererId = ResourceLocation.parse(entry.getKey());
            ResourceLocation modelId = ResourceLocation.parse(GsonHelper.convertToString(entry.getValue(), "specialmodels"));
            parsed.put(rendererId, modelId);
         }

         BlockModel model = (BlockModel)cir.getReturnValue();
         ((SpecialModelSource)model).corners$setSpecialModels(parsed);
      }
   }
}

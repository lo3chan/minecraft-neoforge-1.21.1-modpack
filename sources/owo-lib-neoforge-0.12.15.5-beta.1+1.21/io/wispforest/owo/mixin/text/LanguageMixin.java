package io.wispforest.owo.mixin.text;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Map.Entry;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Language.class})
public class LanguageMixin {
   @ModifyExpressionValue(
      method = {"loadFromJson(Ljava/io/InputStream;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/google/gson/JsonElement;isJsonArray()Z"
      )}
   )
   private static boolean widenScope(boolean original, @Local Entry<String, JsonElement> entry) {
      return original | !entry.getValue().isJsonPrimitive();
   }
}

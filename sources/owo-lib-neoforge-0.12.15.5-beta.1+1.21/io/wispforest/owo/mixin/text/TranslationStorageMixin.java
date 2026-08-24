package io.wispforest.owo.mixin.text;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import io.wispforest.owo.util.KawaiiUtil;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.resources.language.ClientLanguage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLanguage.class})
public class TranslationStorageMixin {
   @Mutable
   @Shadow
   @Final
   private Map<String, String> storage;

   @Inject(
      method = {"<init>(Ljava/util/Map;Z)V"},
      at = {@At("TAIL")}
   )
   private void kawaii(Map<String, String> translations, boolean rightToLeft, CallbackInfo ci) {
      if (Objects.equals(System.getProperty("owo.uwu"), "yes please")) {
         Builder<String, String> builder = ImmutableMap.builder();
         translations.forEach((s, s2) -> builder.put(s, KawaiiUtil.uwuify(s2)));
         this.storage = builder.build();
      }
   }
}

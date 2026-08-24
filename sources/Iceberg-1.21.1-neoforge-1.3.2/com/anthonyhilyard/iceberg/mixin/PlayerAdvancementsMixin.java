package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.events.common.CriterionEvent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({PlayerAdvancements.class})
public class PlayerAdvancementsMixin {
   @Shadow
   private ServerPlayer player;

   @Inject(
      method = {"award(Lnet/minecraft/advancements/AdvancementHolder;Ljava/lang/String;)Z"},
      at = {@At("TAIL")},
      locals = LocalCapture.CAPTURE_FAILEXCEPTION
   )
   public void onAward(AdvancementHolder advancementHolder, String criterionKey, CallbackInfoReturnable<Boolean> callbackInfo, boolean success) {
      if (success) {
         CriterionEvent.EVENT.invoker().awardCriterion(this.player, advancementHolder, criterionKey);
      }
   }
}

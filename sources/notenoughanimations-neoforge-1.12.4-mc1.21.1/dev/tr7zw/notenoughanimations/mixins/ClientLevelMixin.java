package dev.tr7zw.notenoughanimations.mixins;

import dev.tr7zw.notenoughanimations.NEAnimationsMod;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public class ClientLevelMixin {
   @Inject(
      method = {"tickEntities()V"},
      at = {@At("HEAD")}
   )
   private void startWorldTick(CallbackInfo ci) {
      NEAnimationsMod.INSTANCE.clientTick();
   }
}

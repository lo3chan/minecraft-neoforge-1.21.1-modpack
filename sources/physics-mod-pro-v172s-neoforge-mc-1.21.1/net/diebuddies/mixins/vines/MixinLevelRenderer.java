package net.diebuddies.mixins.vines;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.vines.DynamicLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class MixinLevelRenderer {
   @Shadow
   private ClientLevel level;

   @Inject(
      at = {@At("TAIL")},
      method = {"allChanged"}
   )
   public void allChanged(CallbackInfo info) {
      if (this.level != null) {
         DynamicLoader loader = (DynamicLoader)Minecraft.getInstance().level.getChunkSource();
         VAO.storePreviouslyBoundState();
         if (!ConfigClient.areDynamicBlockPhysicsEnabled()) {
            loader.unloadAllRagdolls();
         } else {
            loader.unloadAllRagdolls();
            loader.loadAllRagdolls();
         }

         if (!ConfigClient.areSnowPhysicsEnabled()) {
            loader.unloadAllSnow();
         } else {
            loader.unloadAllSnow();
            loader.loadAllSnow();
         }

         if (!ConfigClient.areOceanPhysicsEnabled()) {
            loader.unloadAllOcean();
         } else {
            loader.unloadAllOcean();
            loader.loadAllOcean();
         }

         VAO.restorePreviouslyBoundState();
      }
   }
}

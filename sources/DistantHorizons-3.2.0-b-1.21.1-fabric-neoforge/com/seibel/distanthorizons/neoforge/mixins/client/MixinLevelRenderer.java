package com.seibel.distanthorizons.neoforge.mixins.client;

import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftRenderWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.ModInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class MixinLevelRenderer {
   @Unique
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   @Shadow(
      remap = false
   )
   private ClientLevel level;

   @Inject(
      at = {@At("HEAD")},
      method = {"renderSectionLayer"}
   )
   private void renderChunkLayer(
      RenderType renderType, double x, double y, double z, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo callback
   ) {
      ClientApi.RENDER_STATE.mcModelViewMatrix = McObjectConverter_neoforge.convert(modelViewMatrix);
      ClientApi.RENDER_STATE.mcProjectionMatrix = McObjectConverter_neoforge.convert(projectionMatrix);
      ClientApi.RENDER_STATE.partialTickTime = MinecraftRenderWrapper_neoforge.INSTANCE.getPartialTickTime();
      ClientApi.RENDER_STATE.clientLevelWrapper = ClientLevelWrapper_neoforge.getWrapperIfDifferent(ClientApi.RENDER_STATE.clientLevelWrapper, this.level);
      if (ModInfo.IS_DEV_BUILD) {
         ClientApi.RENDER_STATE.canRenderOrThrow();
      }

      if (renderType.equals(RenderType.solid())) {
         ClientApi.INSTANCE.renderLods();
      } else if (renderType.equals(RenderType.translucent())) {
         ClientApi.INSTANCE.renderDeferredLodsForShaders();
      }

      if (renderType.equals(RenderType.cutout())) {
         ClientApi.INSTANCE.renderFadeOpaque();
      } else if (renderType.equals(RenderType.tripwire())) {
         ClientApi.INSTANCE.renderFadeTransparent();
      }
   }
}

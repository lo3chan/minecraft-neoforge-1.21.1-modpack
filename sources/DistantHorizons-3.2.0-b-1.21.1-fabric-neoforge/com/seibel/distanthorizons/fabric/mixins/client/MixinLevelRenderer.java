package com.seibel.distanthorizons.fabric.mixins.client;

import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftRenderWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import net.minecraft.class_1921;
import net.minecraft.class_638;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_761.class})
public class MixinLevelRenderer {
   @Shadow
   private class_638 field_4085;
   @Unique
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   @Inject(
      at = {@At("HEAD")},
      method = {"Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"},
      cancellable = true
   )
   private void renderChunkLayer(class_1921 renderType, double x, double y, double z, Matrix4f projectionMatrix, Matrix4f frustumMatrix, CallbackInfo callback) {
      ClientApi.RENDER_STATE.mcModelViewMatrix = McObjectConverter_fabric.convert(projectionMatrix);
      ClientApi.RENDER_STATE.mcProjectionMatrix = new DhMat4f();
      ClientApi.RENDER_STATE.mcProjectionMatrix.setIdentity();
      ClientApi.RENDER_STATE.partialTickTime = MinecraftRenderWrapper_fabric.INSTANCE.getPartialTickTime();
      ClientApi.RENDER_STATE.clientLevelWrapper = ClientLevelWrapper_fabric.getWrapperIfDifferent(ClientApi.RENDER_STATE.clientLevelWrapper, this.field_4085);
      if (renderType.equals(class_1921.method_23583())) {
         ClientApi.INSTANCE.renderDeferredLodsForShaders();
      }
   }
}

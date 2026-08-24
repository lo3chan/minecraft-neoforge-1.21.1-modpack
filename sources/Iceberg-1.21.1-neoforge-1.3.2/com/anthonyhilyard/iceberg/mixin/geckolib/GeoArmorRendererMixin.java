package com.anthonyhilyard.iceberg.mixin.geckolib;

import com.anthonyhilyard.iceberg.renderer.CheckedBufferSource;
import com.anthonyhilyard.iceberg.renderer.VertexCollector;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Mixin({GeoArmorRenderer.class})
public abstract class GeoArmorRendererMixin<T extends Item & GeoItem> {
   @Shadow
   protected T animatable;
   @Shadow
   protected MultiBufferSource bufferSource;

   @ModifyArg(
      method = {"renderToBuffer"},
      require = 0,
      at = @At(
         value = "INVOKE",
         target = "Lsoftware/bernie/geckolib/renderer/GeoArmorRenderer;defaultRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/animatable/GeoAnimatable;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFI)V"
      )
   )
   private MultiBufferSource icebergCustomBufferSource(MultiBufferSource multiBufferSource) {
      return !(this.bufferSource instanceof VertexCollector) && !(this.bufferSource instanceof CheckedBufferSource) ? multiBufferSource : this.bufferSource;
   }

   @ModifyArg(
      method = {"renderToBuffer"},
      require = 0,
      at = @At(
         value = "INVOKE",
         target = "Lsoftware/bernie/geckolib/renderer/GeoArmorRenderer;defaultRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/animatable/GeoAnimatable;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFI)V"
      )
   )
   private VertexConsumer icebergCustomBuffer(VertexConsumer buffer) {
      if (!(this.bufferSource instanceof VertexCollector) && !(this.bufferSource instanceof CheckedBufferSource)) {
         return buffer;
      } else {
         GeoArmorRenderer<T> self = (GeoArmorRenderer<T>)this;
         Minecraft mc = Minecraft.getInstance();
         float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(true);
         RenderType renderType = self.getRenderType(this.animatable, self.getTextureLocation(this.animatable), this.bufferSource, partialTick);
         return this.bufferSource.getBuffer(renderType);
      }
   }
}

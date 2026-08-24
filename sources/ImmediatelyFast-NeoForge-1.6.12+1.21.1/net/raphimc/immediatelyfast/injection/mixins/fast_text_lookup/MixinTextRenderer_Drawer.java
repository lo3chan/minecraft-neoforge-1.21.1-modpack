package net.raphimc.immediatelyfast.injection.mixins.fast_text_lookup;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.StringRenderOutput;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({StringRenderOutput.class})
public abstract class MixinTextRenderer_Drawer {
   @Unique
   private RenderType immediatelyFast$lastRenderLayer;
   @Unique
   private VertexConsumer immediatelyFast$lastVertexConsumer;
   @Unique
   private ResourceLocation immediatelyFast$lastFont;
   @Unique
   private FontSet immediatelyFast$lastFontStorage;

   @Redirect(
      method = {"accept"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
      )
   )
   private VertexConsumer reduceGetBufferCalls(MultiBufferSource instance, RenderType renderLayer) {
      boolean isBufferInvalid = this.immediatelyFast$lastVertexConsumer instanceof BufferBuilder bufferBuilder && !bufferBuilder.building;
      if (!isBufferInvalid && this.immediatelyFast$lastRenderLayer == renderLayer) {
         return this.immediatelyFast$lastVertexConsumer;
      } else {
         this.immediatelyFast$lastRenderLayer = renderLayer;
         return this.immediatelyFast$lastVertexConsumer = instance.getBuffer(renderLayer);
      }
   }

   @Redirect(
      method = {"accept"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/Font;getFontSet(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/gui/font/FontSet;"
      )
   )
   private FontSet reduceGetFontStorageCalls(Font instance, ResourceLocation id) {
      if (this.immediatelyFast$lastFont == id) {
         return this.immediatelyFast$lastFontStorage;
      } else {
         this.immediatelyFast$lastFont = id;
         return this.immediatelyFast$lastFontStorage = instance.getFontSet(id);
      }
   }
}

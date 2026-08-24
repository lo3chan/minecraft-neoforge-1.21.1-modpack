package io.wispforest.owo.mixin.ui;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.util.pond.OwoTessellatorExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({GuiGraphics.class})
public class DrawContextMixin {
   @Inject(
      method = {"innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/Tesselator;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)Lcom/mojang/blaze3d/vertex/BufferBuilder;"
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void injectBufferBegin(
      ResourceLocation texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, CallbackInfo ci, Matrix4f matrix4f
   ) {
      if (this instanceof OwoUIDrawContext context && context.recording()) {
         ((OwoTessellatorExtension)Tesselator.getInstance()).owo$skipNextBegin();
      }
   }

   @Inject(
      method = {"innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFFFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/Tesselator;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)Lcom/mojang/blaze3d/vertex/BufferBuilder;"
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void injectBufferBeginPartTwo(
      ResourceLocation texture,
      int x1,
      int x2,
      int y1,
      int y2,
      int z,
      float u1,
      float u2,
      float v1,
      float v2,
      float red,
      float green,
      float blue,
      float alpha,
      CallbackInfo ci,
      Matrix4f matrix4f
   ) {
      if (this instanceof OwoUIDrawContext context && context.recording()) {
         ((OwoTessellatorExtension)Tesselator.getInstance()).owo$skipNextBegin();
      }
   }

   @Inject(
      method = {"innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;buildOrThrow()Lcom/mojang/blaze3d/vertex/MeshData;"
      )},
      cancellable = true
   )
   private void skipDraw(
      ResourceLocation texture,
      int x1,
      int x2,
      int y1,
      int y2,
      int z,
      float u1,
      float u2,
      float v1,
      float v2,
      CallbackInfo ci,
      @Local(ordinal = 0) BufferBuilder builder
   ) {
      if (this instanceof OwoUIDrawContext context && context.recording()) {
         ci.cancel();
         ((OwoTessellatorExtension)Tesselator.getInstance()).owo$setStoredBuilder(builder);
      }
   }

   @Inject(
      method = {"innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFFFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;buildOrThrow()Lcom/mojang/blaze3d/vertex/MeshData;"
      )},
      cancellable = true
   )
   private void skipDrawSeason2(
      ResourceLocation texture,
      int x1,
      int x2,
      int y1,
      int y2,
      int z,
      float u1,
      float u2,
      float v1,
      float v2,
      float red,
      float green,
      float blue,
      float alpha,
      CallbackInfo ci,
      @Local(ordinal = 0) BufferBuilder builder
   ) {
      if (this instanceof OwoUIDrawContext context && context.recording()) {
         ci.cancel();
         ((OwoTessellatorExtension)Tesselator.getInstance()).owo$setStoredBuilder(builder);
      }
   }
}

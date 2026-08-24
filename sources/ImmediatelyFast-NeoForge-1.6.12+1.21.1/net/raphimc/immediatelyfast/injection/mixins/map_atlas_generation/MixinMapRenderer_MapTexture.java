package net.raphimc.immediatelyfast.injection.mixins.map_atlas_generation;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lenni0451.reflect.Objects;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.MapRenderer.MapInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import net.raphimc.immediatelyfast.feature.map_atlas_generation.MapAtlasTexture;
import net.raphimc.immediatelyfast.injection.interfaces.IMapRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {MapInstance.class},
   priority = 1100
)
public abstract class MixinMapRenderer_MapTexture {
   @Shadow
   private MapItemSavedData data;
   @Mutable
   @Shadow
   @Final
   private DynamicTexture texture;
   @Shadow
   @Final
   MapRenderer this$0;
   @Unique
   private static final DynamicTexture DUMMY_TEXTURE = (DynamicTexture)Objects.allocate(DynamicTexture.class);
   @Unique
   private int immediatelyFast$atlasX;
   @Unique
   private int immediatelyFast$atlasY;
   @Unique
   private MapAtlasTexture immediatelyFast$atlasTexture;

   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "NEW",
         target = "(IIZ)Lnet/minecraft/client/renderer/texture/DynamicTexture;"
      )
   )
   private DynamicTexture initAtlasParametersAndDontAllocateTexture(int width, int height, boolean useMipmaps, @Local int id) {
      int packedLocation = ((IMapRenderer)this.this$0).immediatelyFast$getAtlasMapping(id);
      if (packedLocation == -1) {
         ImmediatelyFast.LOGGER.warn("Map " + id + " is not in an atlas");
         return new DynamicTexture(width, height, useMipmaps);
      } else {
         this.immediatelyFast$atlasX = (packedLocation >> 8 & 0xFF) * 128;
         this.immediatelyFast$atlasY = (packedLocation & 0xFF) * 128;
         this.immediatelyFast$atlasTexture = ((IMapRenderer)this.this$0).immediatelyFast$getMapAtlasTexture(packedLocation >> 16);
         if (this.immediatelyFast$atlasTexture == null) {
            throw new IllegalStateException("getMapAtlasTexture returned null for packedLocation " + packedLocation + " (map " + id + ")");
         } else {
            return DUMMY_TEXTURE;
         }
      }
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/texture/TextureManager;register(Ljava/lang/String;Lnet/minecraft/client/renderer/texture/DynamicTexture;)Lnet/minecraft/resources/ResourceLocation;"
      )
   )
   private ResourceLocation getAtlasTextureIdentifier(TextureManager textureManager, String id, DynamicTexture texture) {
      if (this.immediatelyFast$atlasTexture != null) {
         this.texture = null;
         return this.immediatelyFast$atlasTexture.getIdentifier();
      } else {
         return textureManager.register(id, texture);
      }
   }

   @Inject(
      method = {"updateTexture"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateAtlasTexture(CallbackInfo ci) {
      if (this.immediatelyFast$atlasTexture != null) {
         ci.cancel();
         DynamicTexture atlasTexture = this.immediatelyFast$atlasTexture.getTexture();
         NativeImage atlasImage = atlasTexture.getPixels();
         if (atlasImage == null) {
            throw new IllegalStateException("Atlas texture has already been closed");
         }

         for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
               int i = x + y * 128;
               atlasImage.setPixelRGBA(this.immediatelyFast$atlasX + x, this.immediatelyFast$atlasY + y, MapColor.getColorFromPackedId(this.data.colors[i]));
            }
         }

         atlasTexture.bind();
         atlasImage.upload(
            0, this.immediatelyFast$atlasX, this.immediatelyFast$atlasY, this.immediatelyFast$atlasX, this.immediatelyFast$atlasY, 128, 128, false, false
         );
      }
   }

   @Redirect(
      method = {"draw"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setUv(FF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
      ),
      slice = @Slice(
         from = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;addVertex(Lorg/joml/Matrix4f;FFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            ordinal = 0
         ),
         to = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setLight(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            ordinal = 3
         )
      )
   )
   private VertexConsumer drawAtlasTexture(VertexConsumer instance, float u, float v) {
      if (this.immediatelyFast$atlasTexture != null) {
         if (u == 0.0F && v == 1.0F) {
            u = this.immediatelyFast$atlasX / 4096.0F;
            v = (this.immediatelyFast$atlasY + 128) / 4096.0F;
         } else if (u == 1.0F && v == 1.0F) {
            u = (this.immediatelyFast$atlasX + 128) / 4096.0F;
            v = (this.immediatelyFast$atlasY + 128) / 4096.0F;
         } else if (u == 1.0F && v == 0.0F) {
            u = (this.immediatelyFast$atlasX + 128) / 4096.0F;
            v = this.immediatelyFast$atlasY / 4096.0F;
         } else if (u == 0.0F && v == 0.0F) {
            u = this.immediatelyFast$atlasX / 4096.0F;
            v = this.immediatelyFast$atlasY / 4096.0F;
         }
      }

      return instance.setUv(u, v);
   }

   @Inject(
      method = {"close"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void dontCloseDummyTexture(CallbackInfo ci) {
      if (this.immediatelyFast$atlasTexture != null) {
         ci.cancel();
      }
   }
}

package net.joefoxe.hexerei.block.connected;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class SpriteShiftEntry {
   protected StitchedSprite original;
   protected StitchedSprite target;

   public void set(ResourceLocation originalTextureLocation, ResourceLocation targetTextureLocation) {
      this.original = new StitchedSprite(originalTextureLocation);
      this.target = new StitchedSprite(targetTextureLocation);
   }

   public ResourceLocation getOriginalResourceLocation() {
      return this.original.getLocation();
   }

   public ResourceLocation getTargetResourceLocation() {
      return this.target.getLocation();
   }

   public TextureAtlasSprite getOriginal() {
      return this.original.get();
   }

   public TextureAtlasSprite getTarget() {
      return this.target.get();
   }

   public float getTargetU(float localU) {
      return this.getTarget().getU(getUnInterpolatedU(this.getOriginal(), localU));
   }

   public float getTargetV(float localV) {
      return this.getTarget().getV(getUnInterpolatedV(this.getOriginal(), localV));
   }

   public static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
      float f = sprite.getU1() - sprite.getU0();
      return (u - sprite.getU0()) / f;
   }

   public static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
      float f = sprite.getV1() - sprite.getV0();
      return (v - sprite.getV0()) / f;
   }
}

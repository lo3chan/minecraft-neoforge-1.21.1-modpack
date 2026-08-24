package net.diebuddies.mixins.particle;

import net.diebuddies.physics.settings.animation.TextureSheetParticleExtension;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({TextureSheetParticle.class})
public class MixinTextureSheetParticle implements TextureSheetParticleExtension {
   @Shadow
   protected TextureAtlasSprite sprite;

   @Override
   public TextureAtlasSprite getSprite() {
      return this.sprite;
   }
}

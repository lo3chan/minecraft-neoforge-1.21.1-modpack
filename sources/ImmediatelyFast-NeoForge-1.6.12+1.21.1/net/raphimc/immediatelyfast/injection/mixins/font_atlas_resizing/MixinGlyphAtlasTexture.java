package net.raphimc.immediatelyfast.injection.mixins.font_atlas_resizing;

import net.minecraft.client.gui.font.FontTexture;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FontTexture.class})
public abstract class MixinGlyphAtlasTexture {
   @Unique
   private boolean immediatelyFast$shouldResizeFontAtlas;

   @Inject(
      method = {"<init>"},
      at = {@At(
         value = "CTOR_HEAD",
         unsafe = true
      )}
   )
   private void checkFontAtlasResizing(CallbackInfo ci) {
      this.immediatelyFast$shouldResizeFontAtlas = ImmediatelyFast.runtimeConfig.font_atlas_resizing;
   }

   @ModifyConstant(
      method = {"*"},
      constant = {@Constant(
         intValue = 256
      )}
   )
   private int modifyGlyphAtlasTextureSize(int original) {
      return this.immediatelyFast$shouldResizeFontAtlas ? 2048 : 256;
   }

   @ModifyConstant(
      method = {"*"},
      constant = {@Constant(
         floatValue = 256.0F
      )}
   )
   private float modifyGlyphAtlasTextureSize(float original) {
      return this.immediatelyFast$shouldResizeFontAtlas ? 2048.0F : 256.0F;
   }
}

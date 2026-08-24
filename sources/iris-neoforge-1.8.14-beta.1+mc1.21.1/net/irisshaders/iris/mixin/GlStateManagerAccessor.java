package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.BlendState;
import com.mojang.blaze3d.platform.GlStateManager.ColorMask;
import com.mojang.blaze3d.platform.GlStateManager.DepthState;
import com.mojang.blaze3d.platform.GlStateManager.TextureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   value = {GlStateManager.class},
   remap = false
)
public interface GlStateManagerAccessor {
   @Accessor("BLEND")
   static BlendState getBLEND() {
      throw new UnsupportedOperationException("Not accessed");
   }

   @Accessor("COLOR_MASK")
   static ColorMask getCOLOR_MASK() {
      throw new UnsupportedOperationException("Not accessed");
   }

   @Accessor("DEPTH")
   static DepthState getDEPTH() {
      throw new UnsupportedOperationException("Not accessed");
   }

   @Accessor("activeTexture")
   static int getActiveTexture() {
      throw new UnsupportedOperationException("Not accessed");
   }

   @Accessor("TEXTURES")
   static TextureState[] getTEXTURES() {
      throw new UnsupportedOperationException("Not accessed");
   }
}

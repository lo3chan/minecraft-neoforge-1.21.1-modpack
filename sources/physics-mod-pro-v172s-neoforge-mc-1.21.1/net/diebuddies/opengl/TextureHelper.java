package net.diebuddies.opengl;

import com.mojang.blaze3d.systems.RenderSystem;

public class TextureHelper {
   public static int getLoadedTextures() {
      return RenderSystem.getShaderTexture(0);
   }
}

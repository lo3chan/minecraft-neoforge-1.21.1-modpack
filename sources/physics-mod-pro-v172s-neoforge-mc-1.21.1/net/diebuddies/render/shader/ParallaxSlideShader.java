package net.diebuddies.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;

public class ParallaxSlideShader extends ShaderInstance {
   public ParallaxSlideShader() throws IOException {
      super(new ShaderResourceProvider(), "parallax_slide", DefaultVertexFormat.POSITION_TEX);
   }
}

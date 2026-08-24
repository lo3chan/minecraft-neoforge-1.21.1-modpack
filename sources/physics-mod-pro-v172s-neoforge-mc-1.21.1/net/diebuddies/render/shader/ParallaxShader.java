package net.diebuddies.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;

public class ParallaxShader extends ShaderInstance {
   public ParallaxShader() throws IOException {
      super(new ShaderResourceProvider(), "parallax", DefaultVertexFormat.POSITION_TEX);
   }
}

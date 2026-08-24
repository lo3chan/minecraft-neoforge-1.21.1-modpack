package net.diebuddies.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;

public class LiquidCompositeShader extends ShaderInstance {
   public LiquidCompositeShader() throws IOException {
      super(new ShaderResourceProvider(), "liquid_composite", DefaultVertexFormat.BLOCK);
   }
}

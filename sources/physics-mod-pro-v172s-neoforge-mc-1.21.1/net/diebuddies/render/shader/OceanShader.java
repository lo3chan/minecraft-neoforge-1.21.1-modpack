package net.diebuddies.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;

public class OceanShader extends ShaderInstance {
   public OceanShader() throws IOException {
      super(new ShaderResourceProvider(), "ocean", DefaultVertexFormat.BLOCK);
   }
}

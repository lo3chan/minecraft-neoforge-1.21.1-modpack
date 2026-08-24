package net.diebuddies.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;

public class SolidPhysicsShader extends ShaderInstance {
   public SolidPhysicsShader() throws IOException {
      super(new ShaderResourceProvider(), "solid_physics", DefaultVertexFormat.NEW_ENTITY);
   }
}

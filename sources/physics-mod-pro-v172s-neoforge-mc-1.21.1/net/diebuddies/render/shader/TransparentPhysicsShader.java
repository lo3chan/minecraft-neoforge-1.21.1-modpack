package net.diebuddies.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;

public class TransparentPhysicsShader extends ShaderInstance {
   public TransparentPhysicsShader() throws IOException {
      super(new ShaderResourceProvider(), "transparent_physics", DefaultVertexFormat.NEW_ENTITY);
   }
}

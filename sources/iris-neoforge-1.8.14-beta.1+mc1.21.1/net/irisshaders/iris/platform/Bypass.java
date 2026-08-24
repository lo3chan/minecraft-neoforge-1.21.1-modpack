package net.irisshaders.iris.platform;

import java.util.function.Supplier;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;

public class Bypass extends ShaderStateShard {
   public Bypass(Supplier<ShaderInstance> original) {
      super(() -> {
         ImmediateState.bypass = true;
         ShaderInstance i = original.get();
         ImmediateState.bypass = false;
         return i;
      });
   }
}

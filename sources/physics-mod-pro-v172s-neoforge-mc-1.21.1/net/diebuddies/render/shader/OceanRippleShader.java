package net.diebuddies.render.shader;

import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Shader;

public class OceanRippleShader extends Shader {
   public static final String VERTEX_SHADER = "/assets/physicsmod/shaders/core/ocean_ripple.vsh";
   public static final String FRAGMENT_SHADER = "/assets/physicsmod/shaders/core/ocean_ripple.fsh";

   public OceanRippleShader() {
      super("/assets/physicsmod/shaders/core/ocean_ripple.vsh", "/assets/physicsmod/shaders/core/ocean_ripple.fsh");
   }

   @Override
   public void bindAttributes() {
      super.bindAttributes();
      this.bindAttribute("Position", Data.POSITION.getAttribute());
      this.bindAttribute("UV0", Data.TEX_COORD.getAttribute());
      this.bindAttribute("Offset", Data.PUDDLE_POS.getAttribute());
      this.bindAttribute("OffsetNew", Data.PUDDLE_POS_NEW.getAttribute());
   }
}

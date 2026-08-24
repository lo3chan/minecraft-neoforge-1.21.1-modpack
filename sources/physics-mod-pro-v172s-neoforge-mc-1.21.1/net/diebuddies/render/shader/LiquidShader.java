package net.diebuddies.render.shader;

import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Shader;

public class LiquidShader extends Shader {
   public static final String VERTEX_SHADER = "/assets/physicsmod/shaders/core/liquid.vsh";
   public static final String FRAGMENT_SHADER = "/assets/physicsmod/shaders/core/liquid.fsh";

   public LiquidShader() {
      super("/assets/physicsmod/shaders/core/liquid.vsh", "/assets/physicsmod/shaders/core/liquid.fsh");
   }

   @Override
   public void bindAttributes() {
      super.bindAttributes();
      this.bindAttribute("Position", Data.POSITION.getAttribute());
      this.bindAttribute("Normal", Data.NORMAL.getAttribute());
      this.bindAttribute("Offset", Data.LIQUID_POS.getAttribute());
      this.bindAttribute("OffsetNew", Data.LIQUID_POS_NEW.getAttribute());
   }
}

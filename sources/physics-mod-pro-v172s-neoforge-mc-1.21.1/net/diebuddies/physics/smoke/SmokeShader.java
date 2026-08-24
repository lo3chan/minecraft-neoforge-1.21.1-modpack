package net.diebuddies.physics.smoke;

import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Replacement;
import net.diebuddies.opengl.Shader;

public class SmokeShader extends Shader {
   public static final String VERTEX_SHADER = "/assets/physicsmod/shaders/core/smoke.vsh";
   public static final String FRAGMENT_SHADER = "/assets/physicsmod/shaders/core/smoke.fsh";

   public SmokeShader(String replacement) {
      super("/assets/physicsmod/shaders/core/smoke.vsh", "/assets/physicsmod/shaders/core/smoke.fsh", new Replacement("#SHADOW_TRANSFORM", replacement));
   }

   @Override
   public void bindAttributes() {
      super.bindAttributes();
      this.bindAttribute("Position", Data.POSITION.getAttribute());
      this.bindAttribute("Color", Data.COLOR.getAttribute());
      this.bindAttribute("UV0", Data.TEX_COORD.getAttribute());
      this.bindAttribute("UV1", Data.OVERLAY.getAttribute());
      this.bindAttribute("UV2", Data.LIGHT.getAttribute());
      this.bindAttribute("Normal", Data.NORMAL.getAttribute());
      this.bindAttribute("ObjectID", Data.SMOKE_LIGHT.getAttribute());
      this.bindAttribute("Offset", Data.SMOKE_POS.getAttribute());
      this.bindAttribute("OffsetNew", Data.SMOKE_POS_NEW.getAttribute());
   }
}

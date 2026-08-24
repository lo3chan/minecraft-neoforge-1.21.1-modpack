package vazkii.psi.api.spell.param;

import vazkii.psi.api.internal.Vector3;

public class ParamVector extends ParamSpecific<Vector3> {
   public ParamVector(String name, int color, boolean canDisable, boolean constant) {
      super(name, color, canDisable, constant);
   }

   @Override
   public Class<Vector3> getRequiredType() {
      return Vector3.class;
   }
}

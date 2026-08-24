package vazkii.psi.api.spell.param;

import vazkii.psi.api.spell.SpellParam;

public class ParamAny extends SpellParam<SpellParam.Any> {
   public ParamAny(String name, int color, boolean canDisable) {
      super(name, color, canDisable);
   }

   public ParamAny(String name, int color, boolean canDisable, SpellParam.ArrowType arrowType) {
      super(name, color, canDisable, arrowType);
   }

   @Override
   public Class<SpellParam.Any> getRequiredType() {
      return SpellParam.Any.class;
   }
}

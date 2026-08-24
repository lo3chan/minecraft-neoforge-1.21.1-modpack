package vazkii.psi.api.spell.param;

import vazkii.psi.api.spell.SpellParam;

public abstract class ParamSpecific<T> extends SpellParam<T> {
   final boolean constant;

   public ParamSpecific(String name, int color, boolean canDisable, boolean constant) {
      super(name, color, canDisable);
      this.constant = constant;
   }

   @Override
   public boolean requiresConstant() {
      return this.constant;
   }
}

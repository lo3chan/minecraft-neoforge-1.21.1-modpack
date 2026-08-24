package vazkii.psi.api.spell.param;

public class ParamNumber extends ParamSpecific<Number> {
   public ParamNumber(String name, int color, boolean canDisable, boolean constant) {
      super(name, color, canDisable, constant);
   }

   @Override
   public Class<Number> getRequiredType() {
      return Number.class;
   }
}

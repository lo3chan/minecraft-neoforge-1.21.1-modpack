package vazkii.psi.common.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorMultiply extends PieceOperator {
   SpellParam<Number> num1;
   SpellParam<Number> num2;
   SpellParam<Number> num3;

   public PieceOperatorMultiply(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num1 = new ParamNumber("psi.spellparam.number1", 4117034, false, false));
      this.addParam(this.num2 = new ParamNumber("psi.spellparam.number2", 4117034, false, false));
      this.addParam(this.num3 = new ParamNumber("psi.spellparam.number3", 4117034, true, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double d1 = this.getParamValue(context, this.num1).doubleValue();
      double d2 = this.getParamValue(context, this.num2).doubleValue();
      Number d3 = this.getParamValue(context, this.num3);
      if (d3 == null) {
         d3 = 1.0;
      }

      return d1 * d2 * d3.doubleValue();
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

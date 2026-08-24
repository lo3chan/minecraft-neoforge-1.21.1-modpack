package vazkii.psi.common.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorIntegerDivide extends PieceOperator {
   SpellParam<Number> num1;
   SpellParam<Number> num2;
   SpellParam<Number> num3;

   public PieceOperatorIntegerDivide(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num1 = new ParamNumber("psi.spellparam.number1", 13773354, false, false));
      this.addParam(this.num2 = new ParamNumber("psi.spellparam.number2", 4117034, false, false));
      this.addParam(this.num3 = new ParamNumber("psi.spellparam.number3", 13814826, true, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double d1 = this.getParamValue(context, this.num1).doubleValue();
      Number d2 = this.getParamValue(context, this.num2).doubleValue();
      Number d3 = this.getParamValue(context, this.num3);
      if (d2.doubleValue() != 0.0 && d2.intValue() != 0 && (d3 == null || d3.doubleValue() != 0.0 && d3.intValue() != 0)) {
         double d4 = d3 != null ? d1 / (d2.doubleValue() * d3.doubleValue()) : d1 / d2.doubleValue();
         return d4 < 0.0 ? Math.ceil(d4) : Math.floor(d4);
      } else {
         throw new SpellRuntimeException("psi.spellerror.dividebyzero");
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

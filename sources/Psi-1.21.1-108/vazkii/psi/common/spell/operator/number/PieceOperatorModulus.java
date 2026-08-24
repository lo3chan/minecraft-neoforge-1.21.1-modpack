package vazkii.psi.common.spell.operator.number;

import java.math.BigDecimal;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorModulus extends PieceOperator {
   SpellParam<Number> num1;
   SpellParam<Number> num2;

   public PieceOperatorModulus(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num1 = new ParamNumber("psi.spellparam.number1", 13773354, false, false));
      this.addParam(this.num2 = new ParamNumber("psi.spellparam.number2", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double d1 = this.getParamValue(context, this.num1).doubleValue();
      double d2 = this.getParamValue(context, this.num2).doubleValue();
      if (d2 == 0.0) {
         throw new SpellRuntimeException("psi.spellerror.dividebyzero");
      } else {
         BigDecimal precise1 = new BigDecimal(d1);
         BigDecimal precise2 = new BigDecimal(d2);
         return precise1.remainder(precise2).doubleValue();
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

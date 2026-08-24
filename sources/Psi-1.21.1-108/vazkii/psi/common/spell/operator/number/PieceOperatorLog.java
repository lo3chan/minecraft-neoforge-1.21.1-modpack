package vazkii.psi.common.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorLog extends PieceOperator {
   SpellParam<Number> num;
   SpellParam<Number> base;

   public PieceOperatorLog(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num = new ParamNumber("psi.spellparam.target", 2774482, false, false));
      this.addParam(this.base = new ParamNumber("psi.spellparam.base", 13773354, true, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double d = this.getParamValue(context, this.num).doubleValue();
      if (d < 0.0) {
         throw new SpellRuntimeException("psi.spellerror.negativenumber");
      } else {
         double logNum = Math.log10(d);
         Number b = this.getParamValue(context, this.base);
         if (b != null) {
            if (b.doubleValue() < 0.0) {
               throw new SpellRuntimeException("psi.spellerror.negativenumber");
            }

            logNum /= Math.log10(b.doubleValue());
         }

         return logNum;
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

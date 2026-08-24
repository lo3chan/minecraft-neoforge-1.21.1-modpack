package vazkii.psi.common.spell.operator.number;

import java.util.concurrent.ThreadLocalRandom;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorRandom extends PieceOperator {
   SpellParam<Number> max;
   SpellParam<Number> min;

   public PieceOperatorRandom(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.max = new ParamNumber("psi.spellparam.max", 2774482, false, false));
      this.addParam(this.min = new ParamNumber("psi.spellparam.min", 13773354, true, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      int maxVal = this.getParamValue(context, this.max).intValue();
      int minVal = this.getParamValueOrDefault(context, this.min, 0).intValue();
      if (maxVal - minVal <= 0) {
         throw new SpellRuntimeException("psi.spellerror.negativenumber");
      } else {
         return (double)(ThreadLocalRandom.current().nextInt(maxVal - minVal) + minVal);
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

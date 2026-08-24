package vazkii.psi.common.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorSquareRoot extends PieceOperator {
   SpellParam<Number> num;

   public PieceOperatorSquareRoot(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num = new ParamNumber("psi.spellparam.number", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double d = this.getParamValue(context, this.num).doubleValue();
      if (d < 0.0) {
         throw new SpellRuntimeException("psi.spellerror.negativenumber");
      } else {
         return Math.sqrt(d);
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

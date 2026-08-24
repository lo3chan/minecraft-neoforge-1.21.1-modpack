package vazkii.psi.common.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorRoot extends PieceOperator {
   SpellParam<Number> num;
   SpellParam<Number> root;

   public PieceOperatorRoot(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num = new ParamNumber("psi.spellparam.number", 4117034, false, false));
      this.addParam(this.root = new ParamNumber("psi.spellparam.root", 13773354, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double base = this.getParamValue(context, this.num).doubleValue();
      double r = this.getParamValue(context, this.root).doubleValue();
      if (base < 0.0 && r % 2.0 == 0.0) {
         throw new SpellRuntimeException("psi.spellerror.nthroot");
      } else {
         return Math.pow(base, 1.0 / r);
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

package vazkii.psi.common.spell.operator.number;

import vazkii.psi.api.internal.math.Gamma;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorGammaFunc extends PieceOperator {
   SpellParam<Number> num1;

   public PieceOperatorGammaFunc(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(1.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.num1 = new ParamNumber("psi.spellparam.number1", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double d1 = this.getParamValue(context, this.num1).doubleValue();
      if (d1 <= 0.0) {
         throw new SpellRuntimeException("psi.spellerror.nonpositivevalue");
      } else {
         return Gamma.gamma(d1);
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

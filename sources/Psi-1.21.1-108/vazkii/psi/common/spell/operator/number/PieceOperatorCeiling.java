package vazkii.psi.common.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorCeiling extends PieceOperator {
   SpellParam<Number> num;

   public PieceOperatorCeiling(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num = new ParamNumber("psi.spellparam.number", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double d = this.getParamValue(context, this.num).doubleValue();
      return Math.ceil(d);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

package vazkii.psi.common.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorPower extends PieceOperator {
   SpellParam<Number> num;
   SpellParam<Number> power;

   public PieceOperatorPower(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num = new ParamNumber("psi.spellparam.base", 4117034, false, false));
      this.addParam(this.power = new ParamNumber("psi.spellparam.power", 13773354, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double d = this.getParamValue(context, this.num).doubleValue();
      double pow = this.getParamValue(context, this.power).doubleValue();
      return Math.pow(d, pow);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

package vazkii.psi.common.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVectorConstruct extends PieceOperator {
   SpellParam<Number> num1;
   SpellParam<Number> num2;
   SpellParam<Number> num3;

   public PieceOperatorVectorConstruct(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.num1 = new ParamNumber("psi.spellparam.x", 13773354, true, false));
      this.addParam(this.num2 = new ParamNumber("psi.spellparam.y", 4117034, true, false));
      this.addParam(this.num3 = new ParamNumber("psi.spellparam.z", 2774482, true, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Number d1 = this.getParamValue(context, this.num1);
      Number d2 = this.getParamValue(context, this.num2);
      Number d3 = this.getParamValue(context, this.num3);
      if (d1 == null) {
         d1 = 0.0;
      }

      if (d2 == null) {
         d2 = 0.0;
      }

      if (d3 == null) {
         d3 = 0.0;
      }

      return new Vector3(d1.doubleValue(), d2.doubleValue(), d3.doubleValue());
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}

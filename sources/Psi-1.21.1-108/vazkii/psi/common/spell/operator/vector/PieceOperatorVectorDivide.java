package vazkii.psi.common.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVectorDivide extends PieceOperator {
   SpellParam<Vector3> vec1;
   SpellParam<Number> num2;

   public PieceOperatorVectorDivide(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.vec1 = new ParamVector("psi.spellparam.vector1", 13773354, false, false));
      this.addParam(this.num2 = new ParamNumber("psi.spellparam.number2", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 v1 = this.getParamValue(context, this.vec1);
      double d = this.getParamValue(context, this.num2).doubleValue();
      if (d == 0.0) {
         throw new SpellRuntimeException("psi.spellerror.dividebyzero");
      } else {
         return v1.copy().multiply(1.0 / d);
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}

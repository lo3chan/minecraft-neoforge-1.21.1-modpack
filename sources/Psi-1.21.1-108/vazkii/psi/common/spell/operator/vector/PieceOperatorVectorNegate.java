package vazkii.psi.common.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVectorNegate extends PieceOperator {
   SpellParam<Vector3> vec1;

   public PieceOperatorVectorNegate(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.vec1 = new ParamVector("psi.spellparam.target", 2774482, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 v1 = this.getParamValue(context, this.vec1);
      return v1.copy().negate();
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}

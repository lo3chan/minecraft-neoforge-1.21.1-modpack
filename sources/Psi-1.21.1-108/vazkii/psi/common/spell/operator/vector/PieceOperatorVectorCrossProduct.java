package vazkii.psi.common.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVectorCrossProduct extends PieceOperator {
   SpellParam<Vector3> vec1;
   SpellParam<Vector3> vec2;

   public PieceOperatorVectorCrossProduct(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.vec1 = new ParamVector("psi.spellparam.vector1", 13773354, false, false));
      this.addParam(this.vec2 = new ParamVector("psi.spellparam.vector2", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 v1 = this.getParamValue(context, this.vec1);
      Vector3 v2 = this.getParamValue(context, this.vec2);
      return v1.copy().crossProduct(v2);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}

package vazkii.psi.common.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVectorProject extends PieceOperator {
   SpellParam<Vector3> target;
   SpellParam<Vector3> axis;

   public PieceOperatorVectorProject(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamVector("psi.spellparam.vector1", 13773354, false, false));
      this.addParam(this.axis = new ParamVector("psi.spellparam.vector2", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 targetVal = this.getParamValue(context, this.target);
      Vector3 axisVal = this.getParamValue(context, this.axis);
      return targetVal.copy().project(axisVal);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}

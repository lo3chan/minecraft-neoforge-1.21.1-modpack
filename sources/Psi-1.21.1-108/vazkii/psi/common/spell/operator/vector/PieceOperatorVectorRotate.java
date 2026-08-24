package vazkii.psi.common.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVectorRotate extends PieceOperator {
   private SpellParam<Vector3> vector;
   private SpellParam<Vector3> axis;
   private SpellParam<Number> angle;

   public PieceOperatorVectorRotate(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.vector = new ParamVector("psi.spellparam.vector", 13773354, false, false));
      this.addParam(this.axis = new ParamVector("psi.spellparam.axis", 2805970, false, false));
      this.addParam(this.angle = new ParamNumber("psi.spellparam.angle", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 v = this.getParamValue(context, this.vector);
      Vector3 a = this.getParamValue(context, this.axis);
      double an = this.getParamValue(context, this.angle).doubleValue();
      return v.copy().rotate(an, a.copy());
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}

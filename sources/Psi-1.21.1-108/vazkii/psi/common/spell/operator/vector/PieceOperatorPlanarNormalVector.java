package vazkii.psi.common.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorPlanarNormalVector extends PieceOperator {
   SpellParam<Vector3> vector;

   public PieceOperatorPlanarNormalVector(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.vector = new ParamVector("psi.spellparam.vector1", 2774482, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 v = this.getParamValue(context, this.vector);
      return new Vector3(v.z, v.x, v.y);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}

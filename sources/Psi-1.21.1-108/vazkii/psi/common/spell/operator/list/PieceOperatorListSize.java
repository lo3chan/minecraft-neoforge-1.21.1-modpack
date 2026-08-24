package vazkii.psi.common.spell.operator.list;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class PieceOperatorListSize extends PieceOperator {
   SpellParam<EntityListWrapper> list;

   public PieceOperatorListSize(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.list = new ParamEntityListWrapper("psi.spellparam.list", 13773354, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      EntityListWrapper l1 = this.getNotNullParamValue(context, this.list);
      return l1.size();
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

package vazkii.psi.common.spell.operator.list;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class PieceOperatorListUnion extends PieceOperator {
   SpellParam<EntityListWrapper> list1;
   SpellParam<EntityListWrapper> list2;

   public PieceOperatorListUnion(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.list1 = new ParamEntityListWrapper("psi.spellparam.list1", 2774482, false, false));
      this.addParam(this.list2 = new ParamEntityListWrapper("psi.spellparam.list2", 13773354, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      EntityListWrapper l1 = this.getNotNullParamValue(context, this.list1);
      EntityListWrapper l2 = this.getNotNullParamValue(context, this.list2);
      return EntityListWrapper.union(l1, l2);
   }

   @Override
   public Class<?> getEvaluationType() {
      return EntityListWrapper.class;
   }
}

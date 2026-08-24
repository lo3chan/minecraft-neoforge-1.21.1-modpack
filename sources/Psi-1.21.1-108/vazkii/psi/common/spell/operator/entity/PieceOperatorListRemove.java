package vazkii.psi.common.spell.operator.entity;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class PieceOperatorListRemove extends PieceOperator {
   SpellParam<Entity> target;
   SpellParam<EntityListWrapper> list;

   public PieceOperatorListRemove(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 2774482, false, false));
      this.addParam(this.list = new ParamEntityListWrapper("psi.spellparam.list", 13814826, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity targetVal = this.getParamValue(context, this.target);
      EntityListWrapper listVal = this.getParamValue(context, this.list);
      if (targetVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         return EntityListWrapper.withRemoved(listVal, targetVal);
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return EntityListWrapper.class;
   }
}

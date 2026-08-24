package vazkii.psi.common.spell.operator.entity;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class PieceOperatorRandomEntity extends PieceOperator {
   SpellParam<EntityListWrapper> list;

   public PieceOperatorRandomEntity(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.list = new ParamEntityListWrapper("psi.spellparam.target", 13814826, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      EntityListWrapper listVal = this.getParamValue(context, this.list);
      if (listVal.size() == 0) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         return listVal.get(ThreadLocalRandom.current().nextInt(listVal.size()));
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Entity.class;
   }
}

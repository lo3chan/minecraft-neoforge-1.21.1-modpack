package vazkii.psi.common.spell.operator.entity;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorEntityHeight extends PieceOperator {
   SpellParam<Entity> target;

   public PieceOperatorEntityHeight(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity e = this.getParamValue(context, this.target);
      if (e == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         return e.getBbHeight();
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

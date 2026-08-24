package vazkii.psi.common.spell.selector;

import net.minecraft.world.entity.LivingEntity;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorAttackTarget extends PieceSelector {
   public PieceSelectorAttackTarget(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return LivingEntity.class;
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      if (context.attackedEntity == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         return context.attackedEntity;
      }
   }
}

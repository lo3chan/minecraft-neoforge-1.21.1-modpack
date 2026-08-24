package vazkii.psi.common.spell.selector;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorAttacker extends PieceSelector {
   public PieceSelectorAttacker(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return LivingEntity.class;
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      if (context.attackingEntity != null && !(context.attackingEntity instanceof FakePlayer)) {
         return context.attackingEntity;
      } else {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      }
   }
}

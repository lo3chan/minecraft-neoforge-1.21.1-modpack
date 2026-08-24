package vazkii.psi.common.spell.selector;

import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorCaster extends PieceSelector {
   public PieceSelectorCaster(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Player.class;
   }

   @Override
   public Object execute(SpellContext context) {
      return context.caster;
   }
}

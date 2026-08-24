package vazkii.psi.common.spell.selector;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorDamageTaken extends PieceSelector {
   public PieceSelectorDamageTaken(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }

   @Override
   public Object execute(SpellContext context) {
      return context.damageTaken;
   }
}

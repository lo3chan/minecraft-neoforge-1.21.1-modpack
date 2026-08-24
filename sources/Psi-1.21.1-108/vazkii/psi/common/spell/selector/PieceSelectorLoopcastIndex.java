package vazkii.psi.common.spell.selector;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorLoopcastIndex extends PieceSelector {
   public PieceSelectorLoopcastIndex(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }

   @Override
   public Object execute(SpellContext context) {
      return (double)context.loopcastIndex;
   }
}

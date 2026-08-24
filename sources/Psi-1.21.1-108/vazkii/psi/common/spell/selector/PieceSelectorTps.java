package vazkii.psi.common.spell.selector;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorTps extends PieceSelector {
   public PieceSelectorTps(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }

   @Override
   public Object execute(SpellContext context) {
      double tps = 1000.0 / PieceSelectorTickTime.getMspt(context);
      return Math.min(tps, 20.0);
   }
}

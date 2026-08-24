package vazkii.psi.common.spell.selector.entity;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorIsElytraFlying extends PieceSelector {
   public PieceSelectorIsElytraFlying(Spell spell) {
      super(spell);
   }

   @Override
   public Object execute(SpellContext context) {
      return context.caster.isFallFlying() ? 1.0 : 0.0;
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

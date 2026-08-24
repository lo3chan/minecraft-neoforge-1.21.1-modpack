package vazkii.psi.common.spell.selector;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorFocalPoint extends PieceSelector {
   public PieceSelectorFocalPoint(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Entity.class;
   }

   @Override
   public Object execute(SpellContext context) {
      return context.focalPoint;
   }
}

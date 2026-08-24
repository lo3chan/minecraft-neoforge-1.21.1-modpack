package vazkii.psi.common.spell.selector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;
import vazkii.psi.common.item.ItemVectorRuler;

public class PieceSelectorRulerVector extends PieceSelector {
   public PieceSelectorRulerVector(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }

   @Override
   public Object execute(SpellContext context) {
      return ItemVectorRuler.getRulerVector(context.caster);
   }
}

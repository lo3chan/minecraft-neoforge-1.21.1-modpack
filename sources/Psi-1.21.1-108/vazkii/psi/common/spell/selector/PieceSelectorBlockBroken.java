package vazkii.psi.common.spell.selector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorBlockBroken extends PieceSelector {
   public PieceSelectorBlockBroken(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      if (context.positionBroken == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else {
         return Vector3.fromBlockPos(context.positionBroken.getBlockPos());
      }
   }
}

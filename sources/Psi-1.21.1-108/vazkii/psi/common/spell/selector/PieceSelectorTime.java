package vazkii.psi.common.spell.selector;

import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorTime extends PieceSelector {
   public PieceSelectorTime(Spell spell) {
      super(spell);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      ItemStack cadStack = PsiAPI.getPlayerCAD(context.caster);
      if (cadStack != null && cadStack.getItem() instanceof ICAD) {
         return (double)((ICAD)cadStack.getItem()).getTime(cadStack);
      } else {
         throw new SpellRuntimeException("psi.spellerror.nocad");
      }
   }
}

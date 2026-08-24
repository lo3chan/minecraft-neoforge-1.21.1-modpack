package vazkii.psi.api.spell.piece;

import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.item.ItemCAD;

public abstract class PieceCraftingTrick extends PieceTrick {
   public PieceCraftingTrick(Spell spell) {
      super(spell);
   }

   @Override
   public Object execute(SpellContext context) {
      ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
      if (cad.getItem() instanceof ItemCAD) {
         ((ItemCAD)cad.getItem()).craft(cad, context.caster, this);
      }

      return null;
   }

   public abstract boolean canCraft(PieceCraftingTrick var1);
}

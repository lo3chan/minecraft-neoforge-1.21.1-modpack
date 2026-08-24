package vazkii.psi.common.spell.selector.entity;

import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorCasterBattery extends PieceSelector {
   public PieceSelectorCasterBattery(Spell spell) {
      super(spell);
   }

   @Override
   public Object execute(SpellContext context) {
      ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
      return cad != null && cad.getItem() instanceof ICAD icad ? icad.getStatValue(cad, EnumCADStat.OVERFLOW) * 1.0 : 0.0;
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}

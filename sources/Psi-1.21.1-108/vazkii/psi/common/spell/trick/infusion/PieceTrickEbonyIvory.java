package vazkii.psi.common.spell.trick.infusion;

import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;

public class PieceTrickEbonyIvory extends PieceTrickGreaterInfusion {
   public PieceTrickEbonyIvory(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(250.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(3000.0));
   }

   @Override
   protected void addPotencyAndCost(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.POTENCY, 250);
      meta.addStat(EnumSpellStat.COST, 3000);
   }

   @Override
   public boolean canCraft(PieceCraftingTrick trick) {
      return trick instanceof PieceTrickEbonyIvory;
   }
}

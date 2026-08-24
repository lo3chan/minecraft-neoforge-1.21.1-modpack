package vazkii.psi.common.spell.trick.infusion;

import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;

public class PieceTrickInfusion extends PieceCraftingTrick {
   public PieceTrickInfusion(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(100.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(1200.0));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      this.addPotencyAndCost(meta);
   }

   protected void addPotencyAndCost(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.POTENCY, 100);
      meta.addStat(EnumSpellStat.COST, 1200);
   }

   @Override
   public boolean canCraft(PieceCraftingTrick trick) {
      return trick instanceof PieceTrickInfusion;
   }
}

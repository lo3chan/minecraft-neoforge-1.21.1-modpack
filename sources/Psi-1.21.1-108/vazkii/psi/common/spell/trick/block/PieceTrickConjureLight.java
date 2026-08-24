package vazkii.psi.common.spell.trick.block;

import net.minecraft.world.level.block.state.BlockState;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.common.block.BlockConjured;

public class PieceTrickConjureLight extends PieceTrickConjureBlock {
   public PieceTrickConjureLight(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(25.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(100.0));
   }

   @Override
   public void addStats(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.POTENCY, 25);
      meta.addStat(EnumSpellStat.COST, 100);
   }

   @Override
   public BlockState messWithState(BlockState state) {
      return (BlockState)state.setValue(BlockConjured.LIGHT, true);
   }
}

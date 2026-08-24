package vazkii.psi.common.spell.trick;

import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickChangeSlot extends PieceTrick {
   SpellParam<Number> slot;

   public PieceTrickChangeSlot(Spell spell) {
      super(spell);
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException {
      super.addToMetadata(meta);
      double slt = SpellHelpers.ensurePositiveOrZero(this, this.slot);
      meta.addStat(EnumSpellStat.COMPLEXITY, 1);
   }

   @Override
   public void initParams() {
      this.addParam(this.slot = new ParamNumber("psi.spellparam.slot", 13773354, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      int slt = this.getParamValue(context, this.slot).intValue();
      context.customTargetSlot = true;
      context.targetSlot = slt;
      return null;
   }
}

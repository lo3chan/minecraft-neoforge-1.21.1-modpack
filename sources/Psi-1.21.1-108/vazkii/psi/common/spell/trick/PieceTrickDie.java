package vazkii.psi.common.spell.trick;

import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickDie extends PieceTrick {
   SpellParam<Number> target;

   public PieceTrickDie(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(1.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamNumber("psi.spellparam.target", 2774482, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.COMPLEXITY, 1);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double timeVal = this.getParamValue(context, this.target).doubleValue();
      if (Math.abs(timeVal) < 1.0) {
         context.stopped = true;
      }

      return null;
   }
}

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

public class PieceTrickDelay extends PieceTrick {
   SpellParam<Number> time;

   public PieceTrickDelay(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(2.0));
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.time", true));
   }

   @Override
   public void initParams() {
      this.addParam(this.time = new ParamNumber("psi.spellparam.time", 2774482, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.COMPLEXITY, 1);
      Double timeVal = this.getParamEvaluation(this.time);
      if (timeVal != null && !(timeVal <= 0.0) && timeVal == timeVal.intValue()) {
         meta.addStat(EnumSpellStat.POTENCY, timeVal.intValue());
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositiveinteger", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      context.delay = this.getParamValue(context, this.time).intValue();
      return null;
   }
}

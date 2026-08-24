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

public class PieceTrickSwitchTargetSlot extends PieceTrick {
   SpellParam<Number> pos;
   SpellParam<Number> shift;

   public PieceTrickSwitchTargetSlot(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(2.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.pos = new ParamNumber("psi.spellparam.position", 2774482, true, false));
      this.addParam(this.shift = new ParamNumber("psi.spellparam.shift", 4117034, true, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      if (this.paramSides.get(this.shift) != SpellParam.Side.OFF && this.paramSides.get(this.pos) != SpellParam.Side.OFF) {
         throw new SpellCompilationException("psi.spellerror.exclusiveparams", this.x, this.y);
      } else {
         meta.addStat(EnumSpellStat.COMPLEXITY, 1);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Number posVal = this.getParamValue(context, this.pos);
      Number shiftVal = this.getParamValue(context, this.shift);
      if (shiftVal != null) {
         context.shiftTargetSlot = true;
         context.targetSlot = shiftVal.intValue();
      } else if (posVal != null) {
         context.shiftTargetSlot = false;
         context.targetSlot = posVal.intValue();
      } else {
         context.shiftTargetSlot = true;
         context.targetSlot = 1;
      }

      return null;
   }
}

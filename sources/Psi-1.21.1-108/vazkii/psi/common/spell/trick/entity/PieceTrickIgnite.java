package vazkii.psi.common.spell.trick.entity;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickIgnite extends PieceTrick {
   SpellParam<Entity> target;
   SpellParam<Number> time;

   public PieceTrickIgnite(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.time", true).abs().mul(40.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.time", true).abs().mul(65.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
      this.addParam(this.time = new ParamNumber("psi.spellparam.time", 13773354, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double timeVal = this.getParamEvaluation(this.time);
      if (timeVal != null && !(timeVal <= 0.0) && timeVal == timeVal.intValue()) {
         meta.addStat(EnumSpellStat.POTENCY, timeVal.intValue() * 40);
         meta.addStat(EnumSpellStat.COST, timeVal.intValue() * 65);
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositiveinteger", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity targetVal = this.getParamValue(context, this.target);
      int timeVal = this.getParamValue(context, this.time).intValue();
      context.verifyEntity(targetVal);
      targetVal.igniteForSeconds(timeVal);
      return null;
   }
}

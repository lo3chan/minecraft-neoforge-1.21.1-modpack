package vazkii.psi.common.spell.trick;

import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.detonator.IDetonationHandler;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickDetonate extends PieceTrick {
   SpellParam<Number> radius;

   public PieceTrickDetonate(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(1.0).add("(").append("psi.spellparam.radius", true).append(" == 0)"));
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.radius", true).min(5.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.radius", true).mul(5.0).ceil());
   }

   @Override
   public void initParams() {
      this.addParam(this.radius = new ParamNumber("psi.spellparam.radius", 4117034, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      double radiusVal = Math.min(32.0, SpellHelpers.ensurePositiveOrZero(this, this.radius));
      meta.addStat(EnumSpellStat.POTENCY, (int)Math.min(radiusVal, 5.0));
      meta.addStat(EnumSpellStat.COST, (int)Math.ceil(radiusVal * 5.0));
      if (radiusVal == 0.0) {
         meta.addStat(EnumSpellStat.COMPLEXITY, 1);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double radiusVal = Math.min(32.0, this.getNotNullParamValue(context, this.radius).doubleValue());
      if (radiusVal == 0.0) {
         IDetonationHandler.performDetonation(context.focalPoint.level(), context.caster, 0.0, entity -> entity == context.caster);
         return null;
      } else {
         IDetonationHandler.performDetonation(context.focalPoint.level(), context.caster, context.focalPoint, radiusVal);
         return null;
      }
   }
}

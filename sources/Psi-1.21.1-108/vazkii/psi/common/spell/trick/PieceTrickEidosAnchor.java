package vazkii.psi.common.spell.trick;

import vazkii.psi.api.internal.Vector3;
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
import vazkii.psi.common.core.handler.PlayerDataHandler;

public class PieceTrickEidosAnchor extends PieceTrick {
   SpellParam<Number> time;

   public PieceTrickEidosAnchor(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.time", true).mul(5.5).add(20.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.time", true).mul(40.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.time = new ParamNumber("psi.spellparam.time", 13773354, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double timeVal = this.getParamEvaluation(this.time);
      if (timeVal != null && !(timeVal <= 0.0) && timeVal == timeVal.intValue()) {
         meta.addStat(EnumSpellStat.POTENCY, (int)(timeVal * 5.5 + 20.0));
         meta.addStat(EnumSpellStat.COST, timeVal.intValue() * 40);
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositiveinteger", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      int timeVal = this.getParamValue(context, this.time).intValue();
      PlayerDataHandler.PlayerData data = PlayerDataHandler.get(context.caster);
      data.eidosAnchor = Vector3.fromEntity(context.caster);
      data.eidosAnchorPitch = context.caster.getXRot();
      data.eidosAnchorYaw = context.caster.getYRot();
      data.eidosAnchorTime = timeVal * 20;
      data.postAnchorRecallTime = 0;
      data.isAnchored = true;
      return null;
   }
}

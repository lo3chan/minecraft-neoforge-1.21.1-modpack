package vazkii.psi.common.spell.trick.entity;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class PieceTrickMassExodus extends PieceTrick {
   SpellParam<EntityListWrapper> target;
   SpellParam<Vector3> position;
   SpellParam<Number> speed;

   public PieceTrickMassExodus(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.speed", true).abs().mul(100.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.speed", true).abs().mul(100.0).max(1.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntityListWrapper("psi.spellparam.target", 13814826, false, false));
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 4117034, false, false));
      this.addParam(this.speed = new ParamNumber("psi.spellparam.speed", 13773354, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double speedVal = this.getParamEvaluation(this.speed);
      if (speedVal == null) {
         speedVal = 1.0;
      }

      double absSpeed = Math.abs(speedVal);
      meta.addStat(EnumSpellStat.POTENCY, (int)(absSpeed * 100.0));
      meta.addStat(EnumSpellStat.COST, (int)Math.max(1.0, absSpeed * 100.0));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      EntityListWrapper targetVal = this.getParamValue(context, this.target);
      Vector3 positionVal = this.getParamValue(context, this.position);
      double speedVal = this.getParamValue(context, this.speed).doubleValue();

      for (Entity e : targetVal) {
         Vector3 vec = positionVal.copy().sub(Vector3.fromEntity(e));
         PieceTrickAddMotion.addMotion(context, e, vec, speedVal);
      }

      return null;
   }
}

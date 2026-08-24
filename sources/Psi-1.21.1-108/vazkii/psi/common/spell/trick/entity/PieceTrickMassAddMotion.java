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

public class PieceTrickMassAddMotion extends PieceTrick {
   SpellParam<EntityListWrapper> target;
   SpellParam<Vector3> direction;
   SpellParam<Number> speed;

   public PieceTrickMassAddMotion(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.speed", true).abs().mul(90.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.speed", true).abs().mul(105.0).max(1.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntityListWrapper("psi.spellparam.target", 13814826, false, false));
      this.addParam(this.direction = new ParamVector("psi.spellparam.direction", 4117034, false, false));
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
      meta.addStat(EnumSpellStat.POTENCY, (int)(absSpeed * 90.0));
      meta.addStat(EnumSpellStat.COST, (int)Math.max(1.0, absSpeed * 105.0));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      EntityListWrapper targetVal = this.getParamValue(context, this.target);
      Vector3 directionVal = this.getParamValue(context, this.direction);
      double speedVal = this.getParamValue(context, this.speed).doubleValue();

      for (Entity e : targetVal) {
         PieceTrickAddMotion.addMotion(context, e, directionVal, speedVal);
      }

      return null;
   }
}

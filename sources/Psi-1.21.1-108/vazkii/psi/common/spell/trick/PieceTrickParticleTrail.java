package vazkii.psi.common.spell.trick;

import net.minecraft.server.level.ServerLevel;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageParticleTrail;

public class PieceTrickParticleTrail extends PieceTrick {
   SpellParam<Vector3> positionParam;
   SpellParam<Vector3> rayParam;
   SpellParam<Number> lengthParam;
   SpellParam<Number> timeParam;

   public PieceTrickParticleTrail(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.distance", true).floor().mul(10.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.positionParam = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.rayParam = new ParamVector("psi.spellparam.ray", 4117034, false, false));
      this.addParam(this.lengthParam = new ParamNumber("psi.spellparam.distance", 2805970, false, true));
      this.addParam(this.timeParam = new ParamNumber("psi.spellparam.time", 7678674, true, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException {
      super.addToMetadata(meta);
      double length = SpellHelpers.ensurePositiveAndNonzero(this, this.lengthParam);
      meta.addStat(EnumSpellStat.POTENCY, (int)length * 10);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 pos = SpellHelpers.getVector3(this, context, this.positionParam, true, false);
      Vector3 dir = SpellHelpers.getVector3(this, context, this.rayParam, true, false, false);
      double length = this.getParamValue(context, this.lengthParam).doubleValue();
      int time = Math.min(this.getParamValueOrDefault(context, this.timeParam, 20).intValue(), 1200);
      if (time <= 0) {
         throw new SpellRuntimeException("psi.spellerror.negativenumber");
      } else {
         time /= 6;
         if (!context.isInRadius(pos.copy().add(dir.copy().normalize().multiply(length)))) {
            throw new SpellRuntimeException("psi.spellerror.outsideradius");
         } else {
            MessageRegister.sendToPlayersInDimension(
               (ServerLevel)context.focalPoint.getCommandSenderWorld(),
               new MessageParticleTrail(pos.toVec3D(), dir.toVec3D(), length, time, PsiAPI.getPlayerCAD(context.caster))
            );
            return null;
         }
      }
   }
}

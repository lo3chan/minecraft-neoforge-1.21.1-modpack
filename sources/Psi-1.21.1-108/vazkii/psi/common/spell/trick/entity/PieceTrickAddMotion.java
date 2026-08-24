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
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.core.handler.AdditiveMotionHandler;

public class PieceTrickAddMotion extends PieceTrick {
   public static final double MULTIPLIER = 0.3;
   SpellParam<Entity> target;
   SpellParam<Vector3> direction;
   SpellParam<Number> speed;

   public PieceTrickAddMotion(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.speed", true).abs().mul(50.0));
      this.setStatLabel(
         EnumSpellStat.COST,
         new StatLabel("psi.spellparam.speed", true).abs().mul(90.0).sub(new StatLabel(3.0).mul("psi.statlabel.is_last", true).parenthesize()).max(1.0)
      );
   }

   public static void addMotion(SpellContext context, Entity e, Vector3 dir, double speed) throws SpellRuntimeException {
      context.verifyEntity(e);
      if (!context.isInRadius(e)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         dir = dir.copy().normalize().multiply(0.3 * speed);
         if (Math.abs(dir.y) > 1.0E-4) {
            if (e.getDeltaMovement().y() + dir.y >= 0.0) {
               e.fallDistance = 0.0F;
            } else if (dir.y > 0.0) {
               double magicnumber = 0.25510204081632654;
               double yvel = (e.getDeltaMovement().y() + dir.y) * magicnumber + 1.0;
               if (yvel > 0.0) {
                  float newfall = (float)(-(49.0 / magicnumber) + (49.0 * yvel - Math.log(yvel) / Math.log(4.0 * magicnumber)) / magicnumber);
                  e.fallDistance = Math.min(e.fallDistance, Math.max(0.0F, newfall));
               }
            }
         }

         AdditiveMotionHandler.addMotion(e, dir.x, dir.y, dir.z);
      }
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
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
      int dc = 0;
      if (!meta.getFlag("psi.addmotion")) {
         meta.setFlag("psi.addmotion", true);
         dc = 3;
      }

      meta.addStat(EnumSpellStat.POTENCY, (int)(absSpeed * 50.0));
      meta.addStat(EnumSpellStat.COST, (int)Math.max(1.0, absSpeed * 90.0 - dc));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity targetVal = this.getParamValue(context, this.target);
      Vector3 directionVal = this.getParamValue(context, this.direction);
      double speedVal = this.getParamValue(context, this.speed).doubleValue();
      addMotion(context, targetVal, directionVal, speedVal);
      return null;
   }
}

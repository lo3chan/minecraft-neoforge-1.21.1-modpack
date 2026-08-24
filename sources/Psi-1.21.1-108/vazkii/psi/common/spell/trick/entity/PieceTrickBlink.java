package vazkii.psi.common.spell.trick.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
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
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageBlink;

public class PieceTrickBlink extends PieceTrick {
   SpellParam<Entity> target;
   SpellParam<Number> distance;

   public PieceTrickBlink(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.distance", true).abs().mul(30.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.distance", true).abs().mul(40.0));
   }

   public static void blink(SpellContext context, Entity e, double dist) throws SpellRuntimeException {
      context.verifyEntity(e);
      if (!context.isInRadius(e)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         Vec3 look = e.getLookAngle();
         double offX = look.x * dist;
         double offY = e.equals(context.caster) ? look.y * dist : Math.max(0.0, look.y * dist);
         double offZ = look.z * dist;
         e.setPos(e.getX() + offX, e.getY() + offY, e.getZ() + offZ);
         if (e instanceof ServerPlayer) {
            MessageRegister.sendToPlayer((ServerPlayer)e, new MessageBlink(offX, offY, offZ));
         }
      }
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
      this.addParam(this.distance = new ParamNumber("psi.spellparam.distance", 13773354, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double distanceVal = this.getParamEvaluation(this.distance);
      if (distanceVal == null) {
         distanceVal = 1.0;
      }

      meta.addStat(EnumSpellStat.POTENCY, (int)(Math.abs(distanceVal) * 30.0));
      meta.addStat(EnumSpellStat.COST, (int)(Math.abs(distanceVal) * 40.0));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity targetVal = this.getParamValue(context, this.target);
      double distanceVal = this.getParamValue(context, this.distance).doubleValue();
      blink(context, targetVal, distanceVal);
      return null;
   }
}

package vazkii.psi.common.spell.trick.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

public abstract class PieceTrickPotionBase extends PieceTrick {
   SpellParam<Entity> target;
   SpellParam<Number> power;
   SpellParam<Number> time;

   public PieceTrickPotionBase(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.time", true).mul("psi.spellparam.power", true).square().mul(5.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.time", true).mul("psi.spellparam.power", true).square().mul(5.0).square());
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
      if (this.hasPower()) {
         this.addParam(this.power = new ParamNumber("psi.spellparam.power", 13773354, false, true));
      }

      this.addParam(this.time = new ParamNumber("psi.spellparam.time", 2774482, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double powerVal = 1.0;
      if (this.hasPower()) {
         powerVal = this.getParamEvaluation(this.power);
      }

      Double timeVal = this.getParamEvaluation(this.time);
      if (powerVal != null && timeVal != null && !(powerVal <= 0.0) && powerVal == powerVal.intValue() && !(timeVal <= 0.0) && timeVal == timeVal.intValue()) {
         meta.addStat(EnumSpellStat.POTENCY, 20 + this.getPotency(powerVal.intValue(), timeVal.intValue()));
         meta.addStat(EnumSpellStat.COST, 40 + this.getCost(powerVal.intValue(), timeVal.intValue()));
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositiveinteger", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity targetVal = this.getParamValue(context, this.target);
      context.verifyEntity(targetVal);
      if (!(targetVal instanceof LivingEntity)) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else if (!context.isInRadius(targetVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         double powerVal = 1.0;
         if (this.hasPower()) {
            powerVal = this.getParamValue(context, this.power).doubleValue();
         }

         double timeVal = this.getParamValue(context, this.time).doubleValue();
         ((LivingEntity)targetVal)
            .addEffect(new MobEffectInstance(this.getPotion(), Math.max(1, (int)timeVal) * 20, this.hasPower() ? Math.max(0, (int)powerVal - 1) : 0));
         return null;
      }
   }

   public abstract Holder<MobEffect> getPotion();

   public int getCost(int power, int time) throws SpellCompilationException {
      return (int)this.multiplySafe(this.getPotency(power, time) * 5, new double[0]);
   }

   public int getPotency(int power, int time) throws SpellCompilationException {
      return (int)this.multiplySafe(time, new double[]{power, power, 5.0});
   }

   public boolean hasPower() {
      return true;
   }
}

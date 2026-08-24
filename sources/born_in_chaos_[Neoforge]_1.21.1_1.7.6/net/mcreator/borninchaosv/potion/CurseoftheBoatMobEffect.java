package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.CurseoftheBoatKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CurseoftheBoatMobEffect extends MobEffect {
   public CurseoftheBoatMobEffect() {
      super(MobEffectCategory.HARMFUL, -12350519);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.curseofthe_boat_0"), -1.0, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.curseofthe_boat_1"), -10.0, Operation.ADD_VALUE
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      CurseoftheBoatKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}

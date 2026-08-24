package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.RabbitAgilityPriNalozhieniiEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.StonePersistenceKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class StonePersistenceMobEffect extends MobEffect {
   public StonePersistenceMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -6110003);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED,
         ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.stone_persistence_0"),
         -0.4,
         Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.KNOCKBACK_RESISTANCE,
         ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.stone_persistence_1"),
         0.45,
         Operation.ADD_MULTIPLIED_BASE
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      RabbitAgilityPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      StonePersistenceKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}

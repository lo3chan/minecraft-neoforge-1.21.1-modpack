package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.ObsessionKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.ObsessionPriNalozhieniiEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ObsessionMobEffect extends MobEffect {
   public ObsessionMobEffect() {
      super(MobEffectCategory.NEUTRAL, -4974033);
      this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.obsession_0"), 15.0, Operation.ADD_VALUE);
      this.addAttributeModifier(
         Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.obsession_1"), 5.0, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.obsession_2"), 5.0, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.obsession_3"), 0.35, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.obsession_4"), 0.5, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.obsession_5"), 0.35, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      ObsessionPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      ObsessionKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}

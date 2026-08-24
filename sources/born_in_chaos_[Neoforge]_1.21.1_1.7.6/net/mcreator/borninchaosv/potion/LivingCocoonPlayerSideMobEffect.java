package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.LivingCocoonKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.LivingCocoonPriNalozhieniiEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class LivingCocoonPlayerSideMobEffect extends MobEffect {
   public LivingCocoonPlayerSideMobEffect() {
      super(MobEffectCategory.NEUTRAL, -11771077);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED,
         ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.living_cocoon_player_side_0"),
         -0.16,
         Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      LivingCocoonPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      LivingCocoonKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
      return super.applyEffectTick(entity, amplifier);
   }
}

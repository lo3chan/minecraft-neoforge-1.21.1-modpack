package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.IceBarrierKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class IceBarrierMobEffect extends MobEffect {
   public IceBarrierMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -10769710);
      this.addAttributeModifier(
         Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.ice_barrier_0"), 0.4, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.ice_barrier_1"), 4.0, Operation.ADD_VALUE
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      IceBarrierKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}

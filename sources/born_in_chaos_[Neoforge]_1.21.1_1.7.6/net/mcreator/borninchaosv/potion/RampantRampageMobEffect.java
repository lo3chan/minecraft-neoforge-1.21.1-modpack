package net.mcreator.borninchaosv.potion;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

public class RampantRampageMobEffect extends MobEffect {
   public RampantRampageMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -3470832);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.rampant_rampage_0"), 1.0, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.rampant_rampage_1"), 15.0, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.rampant_rampage_2"), 0.05, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
      cures.add(EffectCures.MILK);
   }
}

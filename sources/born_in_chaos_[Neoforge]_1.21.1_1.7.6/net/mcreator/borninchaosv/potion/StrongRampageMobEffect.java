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

public class StrongRampageMobEffect extends MobEffect {
   public StrongRampageMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -4587520);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.strong_rampage_0"), 0.5, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.strong_rampage_1"), 8.0, Operation.ADD_VALUE
      );
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
      cures.add(EffectCures.MILK);
   }
}

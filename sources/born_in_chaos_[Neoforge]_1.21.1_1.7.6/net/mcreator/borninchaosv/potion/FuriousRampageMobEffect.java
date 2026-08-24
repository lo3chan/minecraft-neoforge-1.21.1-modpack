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

public class FuriousRampageMobEffect extends MobEffect {
   public FuriousRampageMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -3670016);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.furious_rampage_0"), 0.75, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.furious_rampage_1"), 10.0, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.furious_rampage_2"), 0.02, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
      cures.add(EffectCures.MILK);
   }
}

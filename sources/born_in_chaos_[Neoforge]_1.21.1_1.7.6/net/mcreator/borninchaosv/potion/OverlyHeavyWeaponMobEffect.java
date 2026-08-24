package net.mcreator.borninchaosv.potion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class OverlyHeavyWeaponMobEffect extends MobEffect {
   public OverlyHeavyWeaponMobEffect() {
      super(MobEffectCategory.HARMFUL, -6071958);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED,
         ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.overly_heavy_weapon_0"),
         -0.5,
         Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED,
         ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.overly_heavy_weapon_1"),
         -0.5,
         Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE,
         ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.overly_heavy_weapon_2"),
         -0.1,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}

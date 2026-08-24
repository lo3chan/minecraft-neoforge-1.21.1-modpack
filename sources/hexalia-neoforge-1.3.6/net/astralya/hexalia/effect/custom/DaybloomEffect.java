package net.astralya.hexalia.effect.custom;

import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DaybloomEffect extends MobEffect {
   private static final int COOLDOWN = 100;
   private static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath("hexalia", "daybloom");

   public DaybloomEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Player player) {
         Level level = player.level();
         SunlightCheck sun = new SunlightCheck(level, player.blockPosition());
         sun.recheckCanSeeSun();
         float generation = sun.getGenerationMultiplier();
         if (generation <= 0.0F) {
            player.hurt(player.damageSources().magic(), 1.5F);
            this.removeSpeedModifier(player);
         } else {
            player.heal(2.0F * generation);
            this.applySpeedModifier(player, amplifier, generation);
         }

         return true;
      } else {
         return super.applyEffectTick(entity, amplifier);
      }
   }

   private void applySpeedModifier(Player player, int amplifier, float sunlightScale) {
      AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attribute != null) {
         this.removeSpeedModifier(player);
         double base = 0.05 * (amplifier + 1);
         double scaled = base * sunlightScale;
         if (scaled != 0.0) {
            attribute.addTransientModifier(new AttributeModifier(RESOURCE_LOCATION, scaled, Operation.ADD_MULTIPLIED_TOTAL));
         }
      }
   }

   private void removeSpeedModifier(Player player) {
      AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attribute != null) {
         attribute.removeModifier(RESOURCE_LOCATION);
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 100 == 0;
   }
}

package com.aetherteam.aether.entity.projectile;

import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.projectile.dart.AbstractDart;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PoisonNeedle extends AbstractDart {
   public PoisonNeedle(EntityType<? extends PoisonNeedle> type, Level level) {
      super(type, level);
      this.setBaseDamage(0.25);
      this.pickup = Pickup.DISALLOWED;
   }

   public PoisonNeedle(Level level, LivingEntity shooter) {
      super((EntityType<? extends AbstractDart>)AetherEntityTypes.POISON_NEEDLE.get(), level, shooter, new ItemStack(Items.ARROW), null);
      this.setBaseDamage(0.25);
      this.pickup = Pickup.DISALLOWED;
   }

   protected void doPostHurtEffects(LivingEntity living) {
      super.doPostHurtEffects(living);
      living.addEffect(new MobEffectInstance(AetherEffects.INEBRIATION, 500, 0, false, false, true));
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack(Items.ARROW);
   }
}

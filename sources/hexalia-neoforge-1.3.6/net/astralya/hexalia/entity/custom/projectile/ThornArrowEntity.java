package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class ThornArrowEntity extends AbstractArrow {
   public ThornArrowEntity(EntityType<? extends ThornArrowEntity> type, Level level) {
      super(type, level);
      this.pickup = Pickup.DISALLOWED;
      this.setBaseDamage(1.5);
   }

   public ThornArrowEntity(EntityType<? extends ThornArrowEntity> type, Level level, LivingEntity shooter) {
      super(type, level);
      this.setOwner(shooter);
      this.pickup = Pickup.DISALLOWED;
      this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
      this.setBaseDamage(1.5);
   }

   public void setOwner(@Nullable Entity entity) {
      super.setOwner(entity);
      this.pickup = Pickup.DISALLOWED;
   }

   protected void onHitEntity(EntityHitResult hit) {
      super.onHitEntity(hit);
      if (!this.level().isClientSide && hit.getEntity() instanceof LivingEntity living) {
         living.addEffect(new MobEffectInstance(ModMobEffects.BLEEDING, 60, 0));
      }
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack(Items.ARROW);
   }
}

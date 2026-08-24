package com.aetherteam.aether.entity.projectile.dart;

import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class PoisonDart extends AbstractDart {
   public PoisonDart(EntityType<? extends PoisonDart> type, Level level) {
      super(type, level);
      this.setBaseDamage(0.25);
   }

   public PoisonDart(Level level, LivingEntity shooter, ItemStack itemStack, @Nullable ItemStack firedFromWeapon) {
      super((EntityType<? extends AbstractDart>)AetherEntityTypes.POISON_DART.get(), level, shooter, itemStack, firedFromWeapon);
      this.setBaseDamage(0.25);
   }

   public PoisonDart(
      EntityType<? extends PoisonDart> entityType, Level level, double x, double y, double z, ItemStack itemStack, @Nullable ItemStack firedFromWeapon
   ) {
      super(entityType, x, y, z, level, itemStack, firedFromWeapon);
      this.setBaseDamage(0.25);
   }

   protected void doPostHurtEffects(LivingEntity living) {
      super.doPostHurtEffects(living);
      living.addEffect(new MobEffectInstance(AetherEffects.INEBRIATION, 200, 0));
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack((ItemLike)AetherItems.POISON_DART.get());
   }
}

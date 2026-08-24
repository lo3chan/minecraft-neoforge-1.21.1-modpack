package com.aetherteam.aether.entity.projectile.dart;

import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class EnchantedDart extends AbstractDart {
   public EnchantedDart(EntityType<? extends EnchantedDart> type, Level level) {
      super(type, level);
      this.setBaseDamage(1.5);
   }

   public EnchantedDart(Level level, LivingEntity shooter, ItemStack itemStack, @Nullable ItemStack firedFromWeapon) {
      super((EntityType<? extends AbstractDart>)AetherEntityTypes.ENCHANTED_DART.get(), level, shooter, itemStack, firedFromWeapon);
      this.setBaseDamage(1.5);
   }

   public EnchantedDart(
      EntityType<? extends EnchantedDart> entityType, Level level, double x, double y, double z, ItemStack itemStack, @Nullable ItemStack firedFromWeapon
   ) {
      super(entityType, x, y, z, level, itemStack, firedFromWeapon);
      this.setBaseDamage(1.5);
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack((ItemLike)AetherItems.ENCHANTED_DART.get());
   }
}

package com.aetherteam.aether.entity.projectile.dart;

import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class GoldenDart extends AbstractDart {
   public GoldenDart(EntityType<? extends GoldenDart> type, Level level) {
      super(type, level);
      this.setBaseDamage(0.5);
   }

   public GoldenDart(Level level, LivingEntity shooter, ItemStack itemStack, @Nullable ItemStack firedFromWeapon) {
      super((EntityType<? extends AbstractDart>)AetherEntityTypes.GOLDEN_DART.get(), level, shooter, itemStack, firedFromWeapon);
      this.setBaseDamage(0.5);
   }

   public GoldenDart(
      EntityType<? extends GoldenDart> entityType, Level level, double x, double y, double z, ItemStack itemStack, @Nullable ItemStack firedFromWeapon
   ) {
      super(entityType, x, y, z, level, itemStack, firedFromWeapon);
      this.setBaseDamage(0.5);
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack((ItemLike)AetherItems.GOLDEN_DART.get());
   }
}

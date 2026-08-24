package com.aetherteam.aether.item.combat;

import com.aetherteam.aether.entity.projectile.dart.AbstractDart;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public abstract class DartItem extends ArrowItem implements ProjectileItem {
   public DartItem(Properties properties) {
      super(properties);
   }

   public abstract AbstractDart createDart(Level var1, ItemStack var2, LivingEntity var3, @Nullable ItemStack var4);

   public boolean isInfinite(ItemStack ammo, ItemStack weapon, LivingEntity livingEntity) {
      return weapon.getEnchantmentLevel(livingEntity.level().holderOrThrow(Enchantments.INFINITY)) > 0;
   }
}

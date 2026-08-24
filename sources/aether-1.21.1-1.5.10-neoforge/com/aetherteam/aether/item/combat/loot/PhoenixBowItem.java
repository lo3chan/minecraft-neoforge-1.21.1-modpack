package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.PhoenixArrowAttachment;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class PhoenixBowItem extends BowItem {
   public PhoenixBowItem() {
      super(new Properties().durability(384).rarity(AetherItems.AETHER_LOOT));
   }

   public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
      PhoenixArrowAttachment data = (PhoenixArrowAttachment)arrow.getData(AetherDataAttachments.PHOENIX_ARROW);
      data.setPhoenixArrow(true);
      int defaultTime = 20;
      if (arrow.getOwner() instanceof LivingEntity livingEntity
         && EnchantmentHelper.getEnchantmentLevel(livingEntity.level().holderOrThrow(Enchantments.FLAME), livingEntity) > 0) {
         defaultTime = 40;
      }

      data.setFireTime(defaultTime);
      return super.customArrow(arrow, projectileStack, weaponStack);
   }
}

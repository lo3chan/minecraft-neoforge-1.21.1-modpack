package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;

public class CandyCaneSwordItem extends SwordItem {
   public CandyCaneSwordItem() {
      super(AetherItemTiers.CANDY_CANE, new Properties().attributes(SwordItem.createAttributes(AetherItemTiers.CANDY_CANE, 3.0F, -2.4F)));
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (EquipmentUtil.isFullStrength(attacker) && !target.getType().is(AetherTags.Entities.NO_CANDY_CANE_DROPS) && target.level().getRandom().nextBoolean()) {
         target.spawnAtLocation((ItemLike)AetherItems.CANDY_CANE.get());
      }

      return super.hurtEnemy(stack, target, attacker);
   }
}

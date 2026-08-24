package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;

public class VampireBladeItem extends SwordItem {
   public VampireBladeItem() {
      super(
         AetherItemTiers.VAMPIRE, new Properties().rarity(AetherItems.AETHER_LOOT).attributes(SwordItem.createAttributes(AetherItemTiers.VAMPIRE, 3.0F, -2.4F))
      );
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (EquipmentUtil.isFullStrength(attacker) && attacker.getHealth() < attacker.getMaxHealth()) {
         if (attacker instanceof Player player) {
            ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).setVampireHealing(true);
         } else {
            attacker.heal(1.0F);
         }
      }

      return super.hurtEnemy(stack, target, attacker);
   }
}

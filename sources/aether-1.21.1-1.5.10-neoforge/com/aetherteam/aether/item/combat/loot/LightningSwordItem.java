package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.LightningTrackerAttachment;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;

public class LightningSwordItem extends SwordItem {
   public LightningSwordItem() {
      super(
         AetherItemTiers.LIGHTNING,
         new Properties().rarity(AetherItems.AETHER_LOOT).attributes(SwordItem.createAttributes(AetherItemTiers.LIGHTNING, 3.0F, -2.4F))
      );
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (EquipmentUtil.isFullStrength(attacker)) {
         LightningBolt lightningBolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(attacker.level());
         if (lightningBolt != null) {
            ((LightningTrackerAttachment)lightningBolt.getData(AetherDataAttachments.LIGHTNING_TRACKER)).setOwner(attacker);
            lightningBolt.setPos(target.getX(), target.getY(), target.getZ());
            attacker.level().addFreshEntity(lightningBolt);
         }
      }

      return super.hurtEnemy(stack, target, attacker);
   }
}

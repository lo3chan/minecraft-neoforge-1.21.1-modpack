package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;

public class FlamingSwordItem extends SwordItem {
   public FlamingSwordItem() {
      super(
         AetherItemTiers.FLAMING, new Properties().rarity(AetherItems.AETHER_LOOT).attributes(SwordItem.createAttributes(AetherItemTiers.FLAMING, 3.0F, -2.4F))
      );
   }

   public static void onLivingDamage(Post event) {
      LivingEntity target = event.getEntity();
      DamageSource damageSource = event.getSource();
      handleFlamingSwordAbility(target, damageSource);
   }

   private static void handleFlamingSwordAbility(LivingEntity target, DamageSource source) {
      if (source.getDirectEntity() instanceof LivingEntity attacker && EquipmentUtil.isFullStrength(attacker)) {
         ItemStack heldStack = attacker.getMainHandItem();
         if (heldStack.is((Item)AetherItems.FLAMING_SWORD.get())) {
            int defaultTime = 30;
            int fireAspectModifier = EnchantmentHelper.getEnchantmentLevel(attacker.level().holderOrThrow(Enchantments.FIRE_ASPECT), attacker);
            if (fireAspectModifier > 0) {
               defaultTime += fireAspectModifier * 4;
            }

            target.igniteForSeconds(defaultTime);
         }
      }
   }
}

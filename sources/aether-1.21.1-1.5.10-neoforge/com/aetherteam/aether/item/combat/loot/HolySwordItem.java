package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class HolySwordItem extends SwordItem {
   public HolySwordItem() {
      super(AetherItemTiers.HOLY, new Properties().rarity(AetherItems.AETHER_LOOT).attributes(SwordItem.createAttributes(AetherItemTiers.HOLY, 3, -2.4F)));
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (EquipmentUtil.isFullStrength(attacker) && (target.getType().is(EntityTypeTags.UNDEAD) || target.isInvertedHealAndHarm())) {
         stack.hurtAndBreak(10, attacker, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
      }

      return super.hurtEnemy(stack, target, attacker);
   }

   public static void onLivingDamage(Pre event) {
      LivingEntity target = event.getEntity();
      DamageSource damageSource = event.getSource();
      float damage = event.getNewDamage();
      if (canPerformAbility(target, damageSource)) {
         ItemStack itemStack = target.getMainHandItem();
         float bonus = 8.25F;
         int smiteModifier = itemStack.getEnchantmentLevel(target.level().holderOrThrow(Enchantments.SMITE));
         if (smiteModifier > 0) {
            bonus += smiteModifier * 2.5F;
         }

         event.setNewDamage(damage + bonus);
      }
   }

   private static boolean canPerformAbility(LivingEntity target, DamageSource source) {
      return !(
            source.getDirectEntity() instanceof LivingEntity attacker
               && EquipmentUtil.isFullStrength(attacker)
               && (target.getType().is(EntityTypeTags.UNDEAD) || target.isInvertedHealAndHarm())
         )
         ? false
         : attacker.getMainHandItem().is((Item)AetherItems.HOLY_SWORD.get());
   }
}

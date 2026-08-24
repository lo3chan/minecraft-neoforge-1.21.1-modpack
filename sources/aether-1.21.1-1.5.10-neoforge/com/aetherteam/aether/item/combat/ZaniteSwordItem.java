package com.aetherteam.aether.item.combat;

import com.aetherteam.aether.item.combat.abilities.weapon.ZaniteWeapon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class ZaniteSwordItem extends SwordItem implements ZaniteWeapon {
   public ZaniteSwordItem() {
      super(AetherItemTiers.ZANITE, new Properties().attributes(SwordItem.createAttributes(AetherItemTiers.ZANITE, 3.0F, -2.4F)));
   }

   public static void onModifyAttributes(ItemAttributeModifierEvent event) {
      ItemAttributeModifiers modifiers = event.getDefaultModifiers();
      ItemStack itemStack = event.getItemStack();
      if (itemStack.getItem() instanceof ZaniteWeapon zaniteWeapon) {
         Entry attributeEntry = zaniteWeapon.increaseDamage(modifiers, itemStack);
         event.replaceModifier(attributeEntry.attribute(), attributeEntry.modifier(), attributeEntry.slot());
      }
   }
}

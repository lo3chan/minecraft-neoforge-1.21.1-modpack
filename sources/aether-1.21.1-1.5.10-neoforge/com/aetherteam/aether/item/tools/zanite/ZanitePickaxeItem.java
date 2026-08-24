package com.aetherteam.aether.item.tools.zanite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Item.Properties;

public class ZanitePickaxeItem extends PickaxeItem implements ZaniteTool {
   public ZanitePickaxeItem() {
      super(AetherItemTiers.ZANITE, new Properties().attributes(PickaxeItem.createAttributes(AetherItemTiers.ZANITE, 1.0F, -2.8F)));
   }
}

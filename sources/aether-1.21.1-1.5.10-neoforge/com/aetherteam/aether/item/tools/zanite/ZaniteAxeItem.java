package com.aetherteam.aether.item.tools.zanite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item.Properties;

public class ZaniteAxeItem extends AxeItem implements ZaniteTool {
   public ZaniteAxeItem() {
      super(AetherItemTiers.ZANITE, new Properties().attributes(AxeItem.createAttributes(AetherItemTiers.ZANITE, 6.0F, -3.1F)));
   }
}

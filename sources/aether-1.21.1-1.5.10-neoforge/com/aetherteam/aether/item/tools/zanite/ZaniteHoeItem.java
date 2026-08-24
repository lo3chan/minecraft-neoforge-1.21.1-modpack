package com.aetherteam.aether.item.tools.zanite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item.Properties;

public class ZaniteHoeItem extends HoeItem implements ZaniteTool {
   public ZaniteHoeItem() {
      super(AetherItemTiers.ZANITE, new Properties().attributes(HoeItem.createAttributes(AetherItemTiers.ZANITE, -2.0F, -1.0F)));
   }
}

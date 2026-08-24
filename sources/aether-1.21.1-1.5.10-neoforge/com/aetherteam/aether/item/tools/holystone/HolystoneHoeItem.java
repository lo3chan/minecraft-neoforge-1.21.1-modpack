package com.aetherteam.aether.item.tools.holystone;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item.Properties;

public class HolystoneHoeItem extends HoeItem implements HolystoneTool {
   public HolystoneHoeItem() {
      super(AetherItemTiers.HOLYSTONE, new Properties().attributes(HoeItem.createAttributes(AetherItemTiers.HOLYSTONE, -1.0F, -2.0F)));
   }
}

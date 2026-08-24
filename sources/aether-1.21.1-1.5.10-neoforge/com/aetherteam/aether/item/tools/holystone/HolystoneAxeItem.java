package com.aetherteam.aether.item.tools.holystone;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item.Properties;

public class HolystoneAxeItem extends AxeItem implements HolystoneTool {
   public HolystoneAxeItem() {
      super(AetherItemTiers.HOLYSTONE, new Properties().attributes(AxeItem.createAttributes(AetherItemTiers.HOLYSTONE, 7.0F, -3.2F)));
   }
}

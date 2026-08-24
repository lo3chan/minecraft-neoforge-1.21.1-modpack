package com.aetherteam.aether.item.tools.holystone;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Item.Properties;

public class HolystoneShovelItem extends ShovelItem implements HolystoneTool {
   public HolystoneShovelItem() {
      super(AetherItemTiers.HOLYSTONE, new Properties().attributes(ShovelItem.createAttributes(AetherItemTiers.HOLYSTONE, 1.5F, -3.0F)));
   }
}

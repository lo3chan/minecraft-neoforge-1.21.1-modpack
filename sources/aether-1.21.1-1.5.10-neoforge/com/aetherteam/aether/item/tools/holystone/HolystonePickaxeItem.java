package com.aetherteam.aether.item.tools.holystone;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Item.Properties;

public class HolystonePickaxeItem extends PickaxeItem implements HolystoneTool {
   public HolystonePickaxeItem() {
      super(AetherItemTiers.HOLYSTONE, new Properties().attributes(PickaxeItem.createAttributes(AetherItemTiers.HOLYSTONE, 1.0F, -2.8F)));
   }
}

package com.aetherteam.aether.item.tools.zanite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Item.Properties;

public class ZaniteShovelItem extends ShovelItem implements ZaniteTool {
   public ZaniteShovelItem() {
      super(AetherItemTiers.ZANITE, new Properties().attributes(ShovelItem.createAttributes(AetherItemTiers.ZANITE, 1.5F, -3.0F)));
   }
}

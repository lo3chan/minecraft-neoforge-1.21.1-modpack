package com.aetherteam.aether.item.tools.skyroot;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.SkyrootTool;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Item.Properties;

public class SkyrootShovelItem extends ShovelItem implements SkyrootTool {
   public SkyrootShovelItem() {
      super(AetherItemTiers.SKYROOT, new Properties().attributes(ShovelItem.createAttributes(AetherItemTiers.SKYROOT, 1.5F, -3.0F)));
   }
}

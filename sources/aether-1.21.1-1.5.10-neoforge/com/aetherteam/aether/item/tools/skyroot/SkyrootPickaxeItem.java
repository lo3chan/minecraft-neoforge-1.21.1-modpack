package com.aetherteam.aether.item.tools.skyroot;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.SkyrootTool;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Item.Properties;

public class SkyrootPickaxeItem extends PickaxeItem implements SkyrootTool {
   public SkyrootPickaxeItem() {
      super(AetherItemTiers.SKYROOT, new Properties().attributes(PickaxeItem.createAttributes(AetherItemTiers.SKYROOT, 1.0F, -2.8F)));
   }
}

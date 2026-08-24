package com.aetherteam.aether.item.tools.gravitite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.GravititeTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;

public class GravititeShovelItem extends ShovelItem implements GravititeTool {
   public GravititeShovelItem() {
      super(AetherItemTiers.GRAVITITE, new Properties().attributes(ShovelItem.createAttributes(AetherItemTiers.GRAVITITE, 1.5F, -3.0F)));
   }

   public InteractionResult useOn(UseOnContext context) {
      return !this.floatBlock(context) ? super.useOn(context) : InteractionResult.sidedSuccess(context.getLevel().isClientSide());
   }
}

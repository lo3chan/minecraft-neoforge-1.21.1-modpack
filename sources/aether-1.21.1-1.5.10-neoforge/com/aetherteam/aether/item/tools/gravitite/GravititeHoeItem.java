package com.aetherteam.aether.item.tools.gravitite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.GravititeTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;

public class GravititeHoeItem extends HoeItem implements GravititeTool {
   public GravititeHoeItem() {
      super(AetherItemTiers.GRAVITITE, new Properties().attributes(HoeItem.createAttributes(AetherItemTiers.GRAVITITE, -3.0F, 0.0F)));
   }

   public InteractionResult useOn(UseOnContext context) {
      return !this.floatBlock(context) ? super.useOn(context) : InteractionResult.sidedSuccess(context.getLevel().isClientSide());
   }
}

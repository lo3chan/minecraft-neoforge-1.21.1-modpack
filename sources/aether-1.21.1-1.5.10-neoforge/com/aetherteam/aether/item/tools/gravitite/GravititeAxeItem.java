package com.aetherteam.aether.item.tools.gravitite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.GravititeTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;

public class GravititeAxeItem extends AxeItem implements GravititeTool {
   public GravititeAxeItem() {
      super(AetherItemTiers.GRAVITITE, new Properties().attributes(AxeItem.createAttributes(AetherItemTiers.GRAVITITE, 5.0F, -3.0F)));
   }

   public InteractionResult useOn(UseOnContext context) {
      return !this.floatBlock(context) ? super.useOn(context) : InteractionResult.sidedSuccess(context.getLevel().isClientSide());
   }
}

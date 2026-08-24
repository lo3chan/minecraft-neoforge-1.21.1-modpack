package com.aetherteam.aether.item.tools.gravitite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.GravititeTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;

public class GravititePickaxeItem extends PickaxeItem implements GravititeTool {
   public GravititePickaxeItem() {
      super(AetherItemTiers.GRAVITITE, new Properties().attributes(PickaxeItem.createAttributes(AetherItemTiers.GRAVITITE, 1.0F, -2.8F)));
   }

   public InteractionResult useOn(UseOnContext context) {
      return !this.floatBlock(context) ? super.useOn(context) : InteractionResult.sidedSuccess(context.getLevel().isClientSide());
   }
}

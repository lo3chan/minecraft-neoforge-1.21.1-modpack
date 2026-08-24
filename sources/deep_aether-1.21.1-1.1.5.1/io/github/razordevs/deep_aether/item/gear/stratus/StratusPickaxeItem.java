package io.github.razordevs.deep_aether.item.gear.stratus;

import com.aetherteam.aether.item.tools.abilities.GravititeTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class StratusPickaxeItem extends PickaxeItem implements GravititeTool {
   public StratusPickaxeItem(Tier tier, Properties properties) {
      super(tier, properties);
   }

   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      return !this.floatBlock(context) ? super.useOn(context) : InteractionResult.sidedSuccess(level.isClientSide());
   }
}

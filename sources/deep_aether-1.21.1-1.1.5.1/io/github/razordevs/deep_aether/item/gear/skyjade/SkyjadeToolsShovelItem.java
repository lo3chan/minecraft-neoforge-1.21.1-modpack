package io.github.razordevs.deep_aether.item.gear.skyjade;

import io.github.razordevs.deep_aether.DeepAetherConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SkyjadeToolsShovelItem extends ShovelItem implements SkyjadeTool {
   public SkyjadeToolsShovelItem(Tier tier, Properties properties) {
      super(tier, properties);
   }

   public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
      this.disableSound(player, pos);
      return super.canAttackBlock(state, level, pos, player);
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return (Boolean)DeepAetherConfig.SERVER.skyjade_enchant.get() && !(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get();
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return (Boolean)DeepAetherConfig.SERVER.skyjade_enchant.get() && !(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get();
   }
}

package io.github.razordevs.deep_aether.item.gear.skyjade;

import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.init.DATiers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SkyjadeToolsSwordItem extends SwordItem implements SkyjadeTool, SkyjadeWeapon {
   public SkyjadeToolsSwordItem() {
      super(DATiers.SKYJADE, new Properties().attributes(SwordItem.createAttributes(DATiers.SKYJADE, 3, -3.0F)));
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

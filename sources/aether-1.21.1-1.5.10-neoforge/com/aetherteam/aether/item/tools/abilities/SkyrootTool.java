package com.aetherteam.aether.item.tools.abilities;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlockStateProperties;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface SkyrootTool {
   default ItemStack doubleDrops(Level level, ItemStack drop, @Nullable ItemStack tool, @Nullable BlockState state) {
      if (tool != null
         && tool.getEnchantmentLevel(level.holderOrThrow(Enchantments.SILK_TOUCH)) == 0
         && state != null
         && ((Boolean)state.getValue(AetherBlockStateProperties.DOUBLE_DROPS) || state.is(AetherTags.Blocks.DOUBLE_DROPS_OVERRIDE))
         && tool.isCorrectToolForDrops(state)) {
         drop.setCount(2 * drop.getCount());
      }

      return drop;
   }
}

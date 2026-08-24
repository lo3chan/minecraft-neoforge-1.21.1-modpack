package io.github.razordevs.deep_aether.item.gear;

import com.google.common.collect.ImmutableMap.Builder;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.item.gear.skyjade.SkyjadeTool;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class DAAbilityHooks {
   public static class ToolHooks {
      public static final Map<Block, Block> STRIPPABLES = new Builder()
         .put((Block)DABlocks.ROSEROOT_LOG.get(), (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get())
         .put((Block)DABlocks.ROSEROOT_WOOD.get(), (Block)DABlocks.STRIPPED_ROSEROOT_WOOD.get())
         .put((Block)DABlocks.YAGROOT_LOG.get(), (Block)DABlocks.STRIPPED_YAGROOT_LOG.get())
         .put((Block)DABlocks.YAGROOT_WOOD.get(), (Block)DABlocks.STRIPPED_YAGROOT_WOOD.get())
         .build();

      public static BlockState setupToolActions(BlockState old, ItemAbility action) {
         Block oldBlock = old.getBlock();
         return action == ItemAbilities.AXE_STRIP && STRIPPABLES.containsKey(oldBlock) ? STRIPPABLES.get(oldBlock).withPropertiesOf(old) : old;
      }

      public static float handleSkyjadeToolAbility(ItemStack stack, float speed) {
         return stack.getItem() instanceof SkyjadeTool skyjadeTool ? skyjadeTool.decreaseSpeed(stack, speed) : speed;
      }
   }
}

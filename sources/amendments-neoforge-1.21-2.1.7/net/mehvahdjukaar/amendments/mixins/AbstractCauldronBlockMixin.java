package net.mehvahdjukaar.amendments.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.events.behaviors.CauldronConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AbstractCauldronBlock.class})
public class AbstractCauldronBlockMixin extends Block {
   public AbstractCauldronBlockMixin(Properties properties) {
      super(properties);
   }

   @ModifyReturnValue(
      method = {"useItemOn"},
      at = {@At("RETURN")}
   )
   public ItemInteractionResult use(
      ItemInteractionResult original,
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hitResult
   ) {
      return original == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION && this == Blocks.CAULDRON && CommonConfigs.LIQUID_CAULDRON.get()
         ? CauldronConversion.convert(state, pos, level, player, hand, stack, false)
         : original;
   }
}

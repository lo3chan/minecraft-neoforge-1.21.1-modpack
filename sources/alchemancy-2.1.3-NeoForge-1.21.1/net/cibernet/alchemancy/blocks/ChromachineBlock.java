package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.properties.DisguisedProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;

public class ChromachineBlock extends Block {
   public ChromachineBlock(Properties properties) {
      super(properties);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      ItemStack stack = player.getMainHandItem();
      if (stack.isEmpty()) {
         return InteractionResult.PASS;
      } else if (stack.is(AlchemancyTags.Items.CANNOT_TINT)) {
         player.displayClientMessage(Component.translatable("block.alchemancy.chromachine.cannot_tint"), true);
         return InteractionResult.PASS;
      } else if (stack.is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS)) {
         player.displayClientMessage(Component.translatable("block.alchemancy.chromachine.cannot_infuse"), true);
         return InteractionResult.PASS;
      } else if (((DisguisedProperty)AlchemancyProperties.DISGUISED.get()).hasData(stack)) {
         player.displayClientMessage(Component.translatable("block.alchemancy.chromachine.cannot_tint_disguised"), true);
         return InteractionResult.PASS;
      } else {
         if (level.isClientSide()) {
            ClientUtil.openChromachineScreen(stack);
         }

         return InteractionResult.sidedSuccess(level.isClientSide());
      }
   }
}

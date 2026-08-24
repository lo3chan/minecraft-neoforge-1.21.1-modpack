package net.joefoxe.hexerei.item.custom;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;

public class WaxBlendItem extends Item {
   public static final BiMap<Block, Block> WAXABLES = new Builder()
      .put((Block)ModBlocks.WILLOW_PLANKS.get(), (Block)ModBlocks.POLISHED_WILLOW_PLANKS.get())
      .put((Block)ModBlocks.POLISHED_WILLOW_CONNECTED.get(), (Block)ModBlocks.WAXED_POLISHED_WILLOW_CONNECTED.get())
      .put((Block)ModBlocks.POLISHED_WILLOW_PILLAR.get(), (Block)ModBlocks.WAXED_POLISHED_WILLOW_PILLAR.get())
      .put((Block)ModBlocks.POLISHED_WILLOW_LAYERED.get(), (Block)ModBlocks.WAXED_POLISHED_WILLOW_LAYERED.get())
      .put((Block)ModBlocks.WILLOW_CONNECTED.get(), (Block)ModBlocks.WAXED_WILLOW_CONNECTED.get())
      .put((Block)ModBlocks.POLISHED_WITCH_HAZEL_CONNECTED.get(), (Block)ModBlocks.WAXED_POLISHED_WITCH_HAZEL_CONNECTED.get())
      .put((Block)ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get(), (Block)ModBlocks.WAXED_POLISHED_WITCH_HAZEL_PILLAR.get())
      .put((Block)ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get(), (Block)ModBlocks.WAXED_POLISHED_WITCH_HAZEL_LAYERED.get())
      .put((Block)ModBlocks.WITCH_HAZEL_CONNECTED.get(), (Block)ModBlocks.WAXED_WITCH_HAZEL_CONNECTED.get())
      .put((Block)ModBlocks.POLISHED_MAHOGANY_CONNECTED.get(), (Block)ModBlocks.WAXED_POLISHED_MAHOGANY_CONNECTED.get())
      .put((Block)ModBlocks.POLISHED_MAHOGANY_PILLAR.get(), (Block)ModBlocks.WAXED_POLISHED_MAHOGANY_PILLAR.get())
      .put((Block)ModBlocks.POLISHED_MAHOGANY_LAYERED.get(), (Block)ModBlocks.WAXED_POLISHED_MAHOGANY_LAYERED.get())
      .put((Block)ModBlocks.MAHOGANY_CONNECTED.get(), (Block)ModBlocks.WAXED_MAHOGANY_CONNECTED.get())
      .put((Block)ModBlocks.MAHOGANY_WINDOW_PANE.get(), (Block)ModBlocks.WAXED_MAHOGANY_WINDOW_PANE.get())
      .put((Block)ModBlocks.WILLOW_WINDOW_PANE.get(), (Block)ModBlocks.WAXED_WILLOW_WINDOW_PANE.get())
      .put((Block)ModBlocks.WITCH_HAZEL_WINDOW_PANE.get(), (Block)ModBlocks.WAXED_WITCH_HAZEL_WINDOW_PANE.get())
      .put((Block)ModBlocks.MAHOGANY_WINDOW.get(), (Block)ModBlocks.WAXED_MAHOGANY_WINDOW.get())
      .put((Block)ModBlocks.WILLOW_WINDOW.get(), (Block)ModBlocks.WAXED_WILLOW_WINDOW.get())
      .put((Block)ModBlocks.WITCH_HAZEL_WINDOW.get(), (Block)ModBlocks.WAXED_WITCH_HAZEL_WINDOW.get())
      .put((Block)ModBlocks.INFUSED_FABRIC_CARPET.get(), (Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get())
      .put((Block)ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get(), (Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_SLAB.get())
      .put((Block)ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get(), (Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_STAIRS.get())
      .put((Block)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get(), (Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE.get())
      .put((Block)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_SLAB.get(), (Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_SLAB.get())
      .put((Block)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get(), (Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get())
      .put((Block)ModBlocks.STONE_WINDOW.get(), (Block)ModBlocks.WAXED_STONE_WINDOW.get())
      .put((Block)ModBlocks.STONE_WINDOW_PANE.get(), (Block)ModBlocks.WAXED_STONE_WINDOW_PANE.get())
      .put((Block)ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get(), (Block)ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_ORNATE.get())
      .put((Block)ModBlocks.INFUSED_FABRIC_BLOCK.get(), (Block)ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get())
      .build();
   public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(WAXABLES::inverse);

   public WaxBlendItem(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResult useOn(UseOnContext pContext) {
      Level level = pContext.getLevel();
      BlockPos blockpos = pContext.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      return getWaxed(blockstate)
         .map(
            newBlockstate -> {
               if (blockstate.hasProperty(ConnectingCarpetDyed.COLOR)) {
                  newBlockstate.setValue(ConnectingCarpetDyed.COLOR, (DyeColor)blockstate.getValue(ConnectingCarpetDyed.COLOR));
               }

               Player player = pContext.getPlayer();
               ItemStack itemstack = pContext.getItemInHand();
               if (player instanceof ServerPlayer) {
                  CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
               }

               itemstack.shrink(1);
               if (blockstate.getBlock() instanceof CrossCollisionBlock) {
                  BlockState changeTo = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)newBlockstate.setValue(
                                 CrossCollisionBlock.NORTH, (Boolean)blockstate.getValue(CrossCollisionBlock.NORTH)
                              ))
                              .setValue(CrossCollisionBlock.SOUTH, (Boolean)blockstate.getValue(CrossCollisionBlock.SOUTH)))
                           .setValue(CrossCollisionBlock.EAST, (Boolean)blockstate.getValue(CrossCollisionBlock.EAST)))
                        .setValue(CrossCollisionBlock.WEST, (Boolean)blockstate.getValue(CrossCollisionBlock.WEST)))
                     .setValue(CrossCollisionBlock.WATERLOGGED, (Boolean)blockstate.getValue(CrossCollisionBlock.WATERLOGGED));
                  level.setBlockAndUpdate(blockpos, changeTo);
               } else {
                  level.setBlock(blockpos, newBlockstate, 11);
               }

               level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, Context.of(player, newBlockstate));
               level.levelEvent(player, 3003, blockpos, 0);
               return InteractionResult.sidedSuccess(level.isClientSide);
            }
         )
         .orElse(InteractionResult.PASS);
   }

   public static Optional<BlockState> getWaxed(BlockState pState) {
      return Optional.ofNullable((Block)WAXABLES.get(pState.getBlock())).map(p_150877_ -> p_150877_.withPropertiesOf(pState));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.translatable("tooltip.hexerei.wax_blend").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}

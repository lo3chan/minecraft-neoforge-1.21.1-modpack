package net.mcreator.undeadrevamp.block;

import io.netty.buffer.Unpooled;
import net.mcreator.undeadrevamp.block.entity.BlacpetalblockBlockEntity;
import net.mcreator.undeadrevamp.procedures.BlacpetalblockUpdateTickProcedure;
import net.mcreator.undeadrevamp.world.inventory.BlackpetalblockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;

public class BlacpetalblockBlock extends Block implements EntityBlock {
   public BlacpetalblockBlock() {
      super(Properties.of().ignitedByLava().sound(SoundType.GRASS).strength(1.0F, 5.0F).lightLevel(s -> 2));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
      return 35;
   }

   public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
      super.tick(blockstate, world, pos, random);
      BlacpetalblockUpdateTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }

   public InteractionResult useWithoutItem(BlockState blockstate, Level world, final BlockPos pos, Player entity, BlockHitResult hit) {
      super.useWithoutItem(blockstate, world, pos, entity, hit);
      if (entity instanceof ServerPlayer player) {
         player.openMenu(new MenuProvider() {
            public Component getDisplayName() {
               return Component.literal("Black Petal Block");
            }

            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player playerx) {
               return new BlackpetalblockMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
            }
         }, pos);
      }

      return InteractionResult.SUCCESS;
   }

   public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
      return worldIn.getBlockEntity(pos) instanceof MenuProvider menuProvider ? menuProvider : null;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BlacpetalblockBlockEntity(pos, state);
   }

   public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
      super.triggerEvent(state, world, pos, eventID, eventParam);
      BlockEntity blockEntity = world.getBlockEntity(pos);
      return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (world.getBlockEntity(pos) instanceof BlacpetalblockBlockEntity be) {
            Containers.dropContents(world, pos, be);
            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, isMoving);
      }
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
      return world.getBlockEntity(pos) instanceof BlacpetalblockBlockEntity be ? AbstractContainerMenu.getRedstoneSignalFromContainer(be) : 0;
   }
}

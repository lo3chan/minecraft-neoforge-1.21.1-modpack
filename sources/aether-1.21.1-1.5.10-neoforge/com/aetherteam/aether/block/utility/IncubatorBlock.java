package com.aetherteam.aether.block.utility;

import com.aetherteam.aether.blockentity.AetherBlockEntityTypes;
import com.aetherteam.aether.blockentity.IncubatorBlockEntity;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class IncubatorBlock extends BaseEntityBlock {
   public static final MapCodec<IncubatorBlock> CODEC = simpleCodec(IncubatorBlock::new);
   public static final BooleanProperty LIT = BlockStateProperties.LIT;

   public IncubatorBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue(LIT, false));
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{LIT});
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new IncubatorBlockEntity(pos, state);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return createTickerHelper(blockEntityType, (BlockEntityType)AetherBlockEntityTypes.INCUBATOR.get(), IncubatorBlockEntity::serverTick);
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         this.openContainer(level, pos, player);
         return InteractionResult.CONSUME;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof IncubatorBlockEntity incubatorBlockEntity) {
            if (level instanceof ServerLevel) {
               Containers.dropContents(level, pos, incubatorBlockEntity);
            }

            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   protected void openContainer(Level level, BlockPos pos, Player player) {
      if (!level.isClientSide() && level.getBlockEntity(pos) instanceof IncubatorBlockEntity incubatorBlockEntity) {
         player.openMenu(incubatorBlockEntity);
      }
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if ((Boolean)state.getValue(LIT)) {
         double f = pos.getX() + 0.5;
         double f1 = pos.getY() + 1.0 + random.nextFloat() * 15.0 / 16.0;
         double f2 = pos.getZ() + 0.5;
         level.addParticle(ParticleTypes.SMOKE, f, f1, f2, 0.0, 0.0, 0.0);
         level.addParticle(ParticleTypes.FLAME, f, f1, f2, 0.0, 0.0, 0.0);
         if (random.nextDouble() < 0.1) {
            level.playLocalSound(
               pos.getX() + 0.5,
               pos.getY(),
               pos.getZ() + 0.5,
               (SoundEvent)AetherSoundEvents.BLOCK_INCUBATOR_CRACKLE.get(),
               SoundSource.BLOCKS,
               1.0F,
               1.0F,
               false
            );
         }
      }
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }
}

package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class BlockSkunkSpray extends MultifaceBlock implements SimpleWaterloggedBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   protected MapCodec<? extends MultifaceBlock> codec() {
      return AMPlatform.unsupportedBlockCodec();
   }

   public BlockSkunkSpray() {
      super(Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).noOcclusion().randomTicks().noCollission().instabreak().sound(SoundType.FROGSPAWN));
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false)).setValue(AGE, 0));
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos2) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return super.updateShape(state, direction, state2, levelAccessor, pos, pos2);
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
      this.tick(state, level, pos, randomSource);
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (random.nextInt(8) == 0) {
         MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

         for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(pos, direction);
            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);
            if (blockstate.is(this) && !this.incrementAge(blockstate, level, blockpos$mutableblockpos)) {
               level.scheduleTick(blockpos$mutableblockpos, this, Mth.nextInt(random, 50, 100));
            }
         }

         this.incrementAge(state, level, pos);
      } else {
         level.scheduleTick(pos, this, Mth.nextInt(random, 50, 100));
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> definition) {
      super.createBlockStateDefinition(definition);
      definition.add(new Property[]{WATERLOGGED, AGE});
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      if (itemStack.is(Items.GLASS_BOTTLE)) {
         Direction dir = hit.getDirection().getOpposite();
         if (hasFace(state, dir)) {
            worldIn.setBlockAndUpdate(pos, removeStinkFace(state, dir));
            ItemStack bottle = new ItemStack((ItemLike)AMItemRegistry.STINK_BOTTLE.get());
            if (!player.addItem(bottle)) {
               player.drop(bottle, false);
            }

            if (!player.isCreative()) {
               itemStack.shrink(1);
            }

            return ItemInteractionResult.SUCCESS;
         }
      }

      return super.useItemOn(itemStack, state, worldIn, pos, player, handIn, hit);
   }

   public static BlockState removeStinkFace(BlockState state, Direction faceProperty) {
      BlockState blockstate = (BlockState)state.setValue(getFaceProperty(faceProperty), false);
      return hasAnyFace(blockstate) ? blockstate : Blocks.AIR.defaultBlockState();
   }

   private boolean incrementAge(BlockState state, Level level, BlockPos pos) {
      int i = (Integer)state.getValue(AGE);
      if (i < 3) {
         level.setBlock(pos, (BlockState)state.setValue(AGE, i + 1), 2);
         return false;
      } else {
         level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
         return true;
      }
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
      return !context.getItemInHand().is(AMBlockRegistry.SKUNK_SPRAY.get().asItem()) || super.canBeReplaced(state, context);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public MultifaceSpreader getSpreader() {
      return null;
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
      return state.getFluidState().isEmpty();
   }

   public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource) {
      if (randomSource.nextInt(2) == 0) {
         ArrayList<Direction> faces = new ArrayList<>(availableFaces(blockState));
         Direction direction = null;
         if (faces.size() == 1) {
            direction = faces.get(0);
         } else if (faces.size() > 1) {
            direction = (Direction)Util.getRandom(faces, randomSource);
         }

         if (direction != null) {
            double d0 = direction.getStepX() == 0 ? randomSource.nextDouble() : 0.5 + direction.getStepX() * 0.8;
            double d1 = direction.getStepY() == 0 ? randomSource.nextDouble() : 0.5 + direction.getStepY() * 0.8;
            double d2 = direction.getStepZ() == 0 ? randomSource.nextDouble() : 0.5 + direction.getStepZ() * 0.8;
            level.addParticle((ParticleOptions)AMParticleRegistry.SMELLY.get(), pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, 0.0, 0.0, 0.0);
         }
      }
   }
}

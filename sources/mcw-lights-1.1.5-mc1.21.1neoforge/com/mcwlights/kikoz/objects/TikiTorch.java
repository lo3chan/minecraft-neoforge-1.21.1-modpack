package com.mcwlights.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TikiTorch extends LightBaseTall {
   protected final ParticleOptions flame;
   private static final VoxelShape BASE = Block.box(6.0, 0.1, 6.0, 10.0, 16.0, 10.0);

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return BASE;
   }

   public TikiTorch(Properties properties, ParticleOptions flame) {
      super(properties);
      this.flame = flame;
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(PART, LightBaseTall.LightPart.BOTTOM))
            .setValue(POWERED, false)
      );
   }

   @Override
   protected ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      LightBaseTall.LightPart part = (LightBaseTall.LightPart)state.getValue(PART);
      Boolean lit = (Boolean)state.getValue(LIT);
      Item item = itemstack.getItem();
      if (item == this.asItem()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if ((worldIn.isClientSide() || part != LightBaseTall.LightPart.BOTTOM) && part != LightBaseTall.LightPart.MIDDLE) {
         if (part != LightBaseTall.LightPart.TOP && part != LightBaseTall.LightPart.BASE) {
            return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
         } else {
            state = (BlockState)state.cycle(LIT);
            worldIn.setBlock(pos, state, 10);
            if (!lit) {
               worldIn.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
            } else {
               worldIn.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
            }

            worldIn.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
            return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, LIT, POWERED});
   }

   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
      double d0 = pos.getX() + 0.5;
      double d1 = pos.getY() + 1.0;
      double d2 = pos.getZ() + 0.5;
      Boolean i = (Boolean)stateIn.getValue(LIT);
      LightBaseTall.LightPart part = (LightBaseTall.LightPart)stateIn.getValue(PART);
      if (i && part == LightBaseTall.LightPart.BASE) {
         worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
         worldIn.addParticle(this.flame, d0, d1, d2, 0.0, 0.0, 0.0);
      }

      if (i && part == LightBaseTall.LightPart.TOP) {
         worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
         worldIn.addParticle(this.flame, d0, d1, d2, 0.0, 0.0, 0.0);
      }

      if (i) {
         ;
      }
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      if (!this.canSurvive(state, world, pos)) {
         world.destroyBlock(pos, true);
      }
   }

   @Override
   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
      if (!this.canSurvive(state, world, currentPos)) {
         world.scheduleTick(currentPos, this, 1);
      }

      return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      BlockPos belowPos = pos.below();
      BlockState belowState = world.getBlockState(belowPos);
      boolean belowMaterial = !belowState.isAir();
      FluidState belowFluidState = world.getFluidState(belowPos);
      return belowMaterial || belowFluidState.is(FluidTags.LAVA);
   }
}

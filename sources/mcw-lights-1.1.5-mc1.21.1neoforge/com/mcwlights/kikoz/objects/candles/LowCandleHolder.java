package com.mcwlights.kikoz.objects.candles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LowCandleHolder extends Block {
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   private static final VoxelShape BASE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

   public LowCandleHolder(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true));
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      return BASE;
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
      return Shapes.empty();
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      Boolean lit = (Boolean)state.getValue(LIT);
      if (item != this.asItem()) {
         state = (BlockState)state.cycle(LIT);
         worldIn.setBlock(pos, state, 10);
         if (!lit) {
            worldIn.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
         } else {
            worldIn.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
         }

         return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
      double d0 = pos.getX() + 0.5;
      double d1 = pos.getY() + 0.5;
      double d2 = pos.getZ() + 0.5;
      Boolean i = (Boolean)stateIn.getValue(LIT);
      if (i) {
         worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
      state.add(new Property[]{LIT});
   }
}

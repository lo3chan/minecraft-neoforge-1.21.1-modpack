package com.mcwlights.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightBaseShort extends Block {
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final VoxelShape ONE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
   private static final VoxelShape BASE = Shapes.or(ONE, new VoxelShape[0]);

   public LightBaseShort(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(POWERED, false));
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item != this.asItem()) {
         state = (BlockState)state.cycle(LIT);
         worldIn.setBlock(pos, state, 10);
         worldIn.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
         return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void onBroken(Level worldIn, BlockPos pos) {
      worldIn.levelEvent(1029, pos, 0);
   }

   public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      return BASE;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType type) {
      return false;
   }

   public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block block, BlockPos postwo, boolean bool) {
      if (!worldIn.isClientSide) {
         boolean flag = worldIn.hasNeighborSignal(pos);
         if (flag != (Boolean)state.getValue(POWERED)) {
            if ((Boolean)state.getValue(LIT) != flag) {
               state = (BlockState)state.setValue(LIT, flag);
               worldIn.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
            }

            worldIn.setBlock(pos, (BlockState)state.setValue(POWERED, flag), 2);
         }
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockstate = this.defaultBlockState();
      if (context.getLevel().hasNeighborSignal(context.getClickedPos())) {
         blockstate = (BlockState)((BlockState)blockstate.setValue(LIT, true)).setValue(POWERED, true);
      }

      return blockstate;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
      state.add(new Property[]{LIT, POWERED});
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }
}

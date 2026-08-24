package com.mcwroofs.kikoz.objects.roofs;

import com.mcwroofs.kikoz.init.ItemInit;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RoofGlass extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   protected static final VoxelShape COMMON_SHAPE = Shapes.or(Block.box(0.0, 0.01, 0.0, 16.0, 8.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape CLOSED1 = Shapes.or(Block.box(0.0, 8.0, 8.0, 16.0, 15.0, 16.0), COMMON_SHAPE);
   protected static final VoxelShape CLOSED2 = Shapes.or(Block.box(8.0, 8.0, 0.0, 16.0, 15.0, 16.0), COMMON_SHAPE);
   protected static final VoxelShape CLOSED3 = Shapes.or(Block.box(0.0, 8.0, 0.0, 16.0, 15.0, 8.0), COMMON_SHAPE);
   protected static final VoxelShape CLOSED4 = Shapes.or(Block.box(0.0, 8.0, 0.0, 8.0, 15.0, 16.0), COMMON_SHAPE);

   public RoofGlass(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(OPEN, false)).setValue(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(OPEN, false)).setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void onBroken(Level worldIn, BlockPos pos) {
      worldIn.levelEvent(1029, pos, 0);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return CLOSED4;
         case SOUTH:
            return CLOSED2;
         case WEST:
            return CLOSED1;
         case EAST:
         default:
            return CLOSED3;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      boolean open = (Boolean)state.getValue(OPEN);
      Direction facing = (Direction)state.getValue(FACING);
      if (open) {
         return Shapes.empty();
      } else {
         switch (facing) {
            case NORTH:
               return CLOSED1;
            case SOUTH:
               return CLOSED2;
            case WEST:
               return CLOSED4;
            case EAST:
               return CLOSED3;
            default:
               return Shapes.empty();
         }
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item == ItemInit.ROOFING_HAMMER.get()) {
         state = (BlockState)state.cycle(FACING);
         worldIn.setBlock(pos, state, 2);
      } else {
         state = (BlockState)state.cycle(OPEN);
         worldIn.setBlock(pos, state, 2);
         worldIn.playSound(
            player,
            pos,
            state.getValue(OPEN) ? SoundEvents.WOODEN_TRAPDOOR_OPEN : SoundEvents.WOODEN_TRAPDOOR_CLOSE,
            SoundSource.BLOCKS,
            1.0F,
            worldIn.getRandom().nextFloat() * 0.1F + 1.0F
         );
      }

      return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{OPEN, FACING});
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, Level worldIn, BlockPos currentPos, BlockPos facingPos) {
      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }
}

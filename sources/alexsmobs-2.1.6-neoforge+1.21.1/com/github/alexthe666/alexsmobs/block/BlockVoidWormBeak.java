package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockVoidWormBeak extends BaseEntityBlock {
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final VoxelShape AABB = Block.box(0.0, 4.0, 0.0, 16.0, 12.0, 16.0);
   private static final VoxelShape AABB_VERTICAL = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return AMPlatform.unsupportedBlockCodec();
   }

   public BlockVoidWormBeak() {
      super(
         Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .noOcclusion()
            .sound(SoundType.ANCIENT_DEBRIS)
            .strength(1.0F)
            .noCollission()
            .requiresCorrectToolForDrops()
      );
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false));
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return ((Direction)state.getValue(FACING)).getAxis() == Axis.Y ? AABB_VERTICAL : AABB;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.isClientSide()) {
         this.updateState(state, worldIn, pos, blockIn);
      }
   }

   public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
      if (!worldIn.isClientSide()) {
         this.updateState(state, worldIn, pos, state.getBlock());
      }
   }

   public void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
      boolean flag = (Boolean)state.getValue(POWERED);
      boolean flag1 = worldIn.hasNeighborSignal(pos);
      if (flag1 != flag) {
         worldIn.setBlock(pos, (BlockState)state.setValue(POWERED, flag1), 3);
         worldIn.updateNeighborsAt(pos.below(), this);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TileEntityVoidWormBeak(pos, state);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getClickedFace()))
         .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, POWERED});
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return createTickerHelper(p_152182_, AMTileEntityRegistry.VOID_WORM_BEAK.get(), TileEntityVoidWormBeak::commonTick);
   }
}

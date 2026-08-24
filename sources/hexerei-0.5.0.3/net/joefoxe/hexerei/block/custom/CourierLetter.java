package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.CourierLetterTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CourierLetter extends BaseEntityBlock implements ITileEntity<CourierLetterTile>, SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
   public static final MapCodec<CourierLetter> CODEC = simpleCodec(CourierLetter::new);
   VoxelShape shape_turned = Stream.of(Block.box(4.5, 0.0, 2.0, 11.5, 1.0, 14.0)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
   VoxelShape shape = Stream.of(Block.box(2.0, 0.0, 4.5, 14.0, 1.0, 11.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

   public CourierLetter(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)super.defaultBlockState().setValue(WATERLOGGED, false))
               .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH))
            .setValue(SEALED, true)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (!pState.hasProperty(HorizontalDirectionalBlock.FACING)) {
         return this.shape;
      } else {
         Direction dir = (Direction)pState.getValue(HorizontalDirectionalBlock.FACING);
         return dir != Direction.NORTH && dir != Direction.SOUTH ? this.shape_turned : this.shape;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      boolean sealed = false;
      ItemStack stack = context.getItemInHand();
      CustomData data = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
      if (data != null && data.contains("Sealed") && data.copyTag().getBoolean("Sealed")) {
         sealed = true;
      }

      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
            .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER))
         .setValue(SEALED, sealed);
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      return canSupportCenter(worldIn, pos.below(), Direction.UP);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WATERLOGGED, HorizontalDirectionalBlock.FACING, SEALED});
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return !(Boolean)state.getValue(WATERLOGGED);
   }

   @Override
   public Class<CourierLetterTile> getTileEntityClass() {
      return CourierLetterTile.class;
   }

   @org.jetbrains.annotations.Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new CourierLetterTile((BlockEntityType<?>)ModTileEntities.COURIER_LETTER_TILE.get(), pPos, pState);
   }
}

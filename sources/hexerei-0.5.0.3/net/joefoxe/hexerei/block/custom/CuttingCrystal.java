package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.CuttingCrystalTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CuttingCrystal extends Block implements ITileEntity<CuttingCrystalTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final VoxelShape SHAPE = Block.box(2.0, 0.0, 5.0, 14.0, 16.0, 11.0);
   public static final VoxelShape SHAPE_TURNED = Block.box(5.0, 0.0, 2.0, 11.0, 16.0, 14.0);

   public RenderShape getRenderShape(BlockState p_60550_) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
            .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER))
         .setValue(LIT, false);
   }

   private boolean posEquals(BlockPos pos, BlockPos pos2) {
      return pos.getX() == pos2.getX() && pos.getY() == pos2.getY() && pos.getZ() == pos2.getZ();
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock()) && pLevel.getBlockEntity(pPos) instanceof CuttingCrystalTile cuttingCrystalTile) {
         if (!cuttingCrystalTile.boundPos.isEmpty()) {
            cuttingCrystalTile.boundPos.remove(pPos);
         }

         for (BlockPos pos : cuttingCrystalTile.boundPos) {
            if (pLevel.getBlockEntity(pos) instanceof CuttingCrystalTile cuttingCrystalTile1) {
               cuttingCrystalTile1.boundPos = cuttingCrystalTile.boundPos;
            }
         }
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      this.withTileEntityDo(pLevel, pPos, te -> {});
      super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
   }

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.EAST
            && p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.WEST
         ? SHAPE
         : SHAPE_TURNED;
   }

   public CuttingCrystal(Properties properties) {
      super(properties.noOcclusion());
      this.withPropertiesOf((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(LIT, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, WATERLOGGED, LIT});
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Override
   public Class<CuttingCrystalTile> getTileEntityClass() {
      return CuttingCrystalTile.class;
   }

   @javax.annotation.Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CuttingCrystalTile((BlockEntityType<?>)ModTileEntities.CUTTING_CRYSTAL_TILE.get(), pos, state);
   }

   @javax.annotation.Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.CUTTING_CRYSTAL_TILE.get() ? (world2, pos, state2, entity) -> ((CuttingCrystalTile)entity).tick() : null;
   }
}

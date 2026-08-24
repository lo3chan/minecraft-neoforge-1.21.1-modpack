package com.mcwfurnitures.kikoz.objects;

import com.mcwfurnitures.kikoz.storage.StorageTileEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class TallFurnitureHinge extends TallFurniture implements EntityBlock {
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

   public TallFurnitureHinge(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
               .setValue(CONNECTION, TallFurniture.ConnectionStatus.BASE))
            .setValue(HINGE, DoorHingeSide.LEFT)
      );
   }

   private BlockState TableState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this && state.getValue(FACING) == level.getBlockState(pos.above()).getValue(FACING);
      boolean below = level.getBlockState(pos.below()).getBlock() == this && state.getValue(FACING) == level.getBlockState(pos.below()).getValue(FACING);
      TallFurniture.ConnectionStatus connection = this.getConnectionStatus((Direction)state.getValue(FACING), above, below);
      return (BlockState)state.setValue(CONNECTION, connection);
   }

   private TallFurniture.ConnectionStatus getConnectionStatus(Direction facing, boolean above, boolean below) {
      if (above && below) {
         return TallFurniture.ConnectionStatus.MIDDLE;
      } else if (above && !below) {
         return TallFurniture.ConnectionStatus.BOTTOM;
      } else {
         return !above && below ? TallFurniture.ConnectionStatus.TOP : TallFurniture.ConnectionStatus.BASE;
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, CONNECTION, HINGE});
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.TableState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos())
            .setValue(HINGE, this.getHinge(context)))
         .setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   private DoorHingeSide getHinge(BlockPlaceContext context) {
      BlockPos blockpos = context.getClickedPos();
      Direction direction = context.getHorizontalDirection();
      int j = direction.getStepX();
      int k = direction.getStepZ();
      Vec3 vector3d = context.getClickLocation();
      double d0 = vector3d.x - blockpos.getX();
      double d1 = vector3d.z - blockpos.getZ();
      return j < 0 && d1 < 0.5 || j > 0 && d1 > 0.5 || k < 0 && d0 > 0.5 || k > 0 && d0 < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
   }

   @Override
   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livent, ItemStack stack) {
      Block block = level.getBlockState(pos).getBlock();
      Block below = level.getBlockState(pos.below(1)).getBlock();
      if (stack.getHoverName() != null) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof StorageTileEntity) {
            ((StorageTileEntity)blockentity).getName();
         }
      }

      if (block == this && below == block) {
         DoorHingeSide hinge = (DoorHingeSide)level.getBlockState(pos.below(1)).getValue(HINGE);
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(HINGE, hinge));
      }
   }
}

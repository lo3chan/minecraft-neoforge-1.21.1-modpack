package com.mcwfurnitures.kikoz.objects;

import com.mcwfurnitures.kikoz.storage.StorageTileEntity;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WideFurniture extends FurnitureObject implements EntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<WideFurniture.ConnectionStatus> CONNECTION = EnumProperty.create(
      "connection",
      WideFurniture.ConnectionStatus.class,
      new WideFurniture.ConnectionStatus[]{
         WideFurniture.ConnectionStatus.SINGLE,
         WideFurniture.ConnectionStatus.MIDDLE,
         WideFurniture.ConnectionStatus.LEFT,
         WideFurniture.ConnectionStatus.RIGHT
      }
   );
   protected static final VoxelShape EW = Shapes.or(Block.box(0.0, 0.0, 1.0, 16.0, 16.0, 15.0), new VoxelShape[0]);
   protected static final VoxelShape NS = Shapes.or(Block.box(1.0, 0.0, 0.0, 15.0, 16.0, 16.0), new VoxelShape[0]);

   public WideFurniture(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
            .setValue(CONNECTION, WideFurniture.ConnectionStatus.SINGLE)
      );
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return NS;
         case SOUTH:
            return NS;
         case WEST:
            return EW;
         case EAST:
         default:
            return EW;
      }
   }

   @Override
   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   private BlockState TableState(BlockState state, LevelAccessor level, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      boolean north = this.isSameTable(level.getBlockState(pos.north()), facing);
      boolean east = this.isSameTable(level.getBlockState(pos.east()), facing);
      boolean south = this.isSameTable(level.getBlockState(pos.south()), facing);
      boolean west = this.isSameTable(level.getBlockState(pos.west()), facing);
      WideFurniture.ConnectionStatus connection = this.getConnectionStatus(facing, north, east, south, west);
      return (BlockState)state.setValue(CONNECTION, connection);
   }

   private boolean isSameTable(BlockState neighbor, Direction facing) {
      return neighbor.getBlock() == this && neighbor.getValue(FACING) == facing;
   }

   private WideFurniture.ConnectionStatus getConnectionStatus(Direction facing, boolean north, boolean east, boolean south, boolean west) {
      if (facing == Direction.NORTH) {
         if (north && south && !east && !west) {
            return WideFurniture.ConnectionStatus.MIDDLE;
         }

         if (!north && south && !east && !west) {
            return WideFurniture.ConnectionStatus.RIGHT;
         }

         if (north && !south && !east && !west) {
            return WideFurniture.ConnectionStatus.LEFT;
         }
      }

      if (facing == Direction.SOUTH) {
         if (north && south && !east && !west) {
            return WideFurniture.ConnectionStatus.MIDDLE;
         }

         if (!north && south && !east && !west) {
            return WideFurniture.ConnectionStatus.LEFT;
         }

         if (north && !south && !east && !west) {
            return WideFurniture.ConnectionStatus.RIGHT;
         }
      }

      if (facing == Direction.EAST) {
         if (!north && !south && east && west) {
            return WideFurniture.ConnectionStatus.MIDDLE;
         }

         if (!north && !south && east && !west) {
            return WideFurniture.ConnectionStatus.LEFT;
         }

         if (!north && !south && !east && west) {
            return WideFurniture.ConnectionStatus.RIGHT;
         }
      }

      if (facing == Direction.WEST) {
         if (!north && !south && east && west) {
            return WideFurniture.ConnectionStatus.MIDDLE;
         }

         if (!north && !south && east && !west) {
            return WideFurniture.ConnectionStatus.RIGHT;
         }

         if (!north && !south && !east && west) {
            return WideFurniture.ConnectionStatus.LEFT;
         }
      }

      return WideFurniture.ConnectionStatus.SINGLE;
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bolen) {
      if (!statetwo.is(state.getBlock())) {
         this.TableState(state, level, pos);
      }
   }

   @Override
   public RenderShape getRenderShape(BlockState shape) {
      return RenderShape.MODEL;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, CONNECTION});
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return this.TableState(state, level, pos);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.TableState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos())
         .setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   public void placeAt(Level level, BlockPos pos, int num) {
      level.setBlock(pos, this.defaultBlockState(), num);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new StorageTileEntity(pos, state);
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item != this.asItem()) {
         if (!level.isClientSide() && level.getBlockEntity(pos) instanceof StorageTileEntity blockEntity) {
            player.openMenu(blockEntity);
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!state.is(statetwo.getBlock())) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof Container) {
            Containers.dropContents(level, pos, (Container)blockentity);
            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, statetwo, bool);
      }
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand) {
      BlockEntity blockentity = level.getBlockEntity(pos);
      if (blockentity instanceof StorageTileEntity) {
         ((StorageTileEntity)blockentity).recheckOpen();
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livent, ItemStack stack) {
      if (stack.getHoverName() != null) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof StorageTileEntity) {
            ((StorageTileEntity)blockentity).getName();
         }
      }
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
   }

   public static enum ConnectionStatus implements StringRepresentable {
      SINGLE("single"),
      MIDDLE("middle"),
      LEFT("left"),
      RIGHT("right");

      private final String name;

      private ConnectionStatus(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}

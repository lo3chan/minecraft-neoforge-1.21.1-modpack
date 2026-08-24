package com.mcwfurnitures.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Table extends FurnitureObjectNonFaceable {
   public static final EnumProperty<Table.ConnectionStatus> CONNECTION = EnumProperty.create("connection", Table.ConnectionStatus.class);
   protected static final VoxelShape BASE = Shapes.or(Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0), Block.box(6.0, 0.0, 6.0, 10.0, 14.0, 10.0));
   protected static final VoxelShape MIDDLE = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);

   public Table(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(CONNECTION, Table.ConnectionStatus.WITH_LEG));
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      switch ((Table.ConnectionStatus)state.getValue(CONNECTION)) {
         case CENTER:
            return MIDDLE;
         case WITH_LEG:
            return BASE;
         default:
            return BASE;
      }
   }

   private BlockState TableState(BlockState state, LevelAccessor access, BlockPos pos) {
      boolean north = access.getBlockState(pos.north()).getBlock() == this;
      boolean east = access.getBlockState(pos.east()).getBlock() == this;
      boolean south = access.getBlockState(pos.south()).getBlock() == this;
      boolean west = access.getBlockState(pos.west()).getBlock() == this;
      Table.ConnectionStatus connection = this.getConnectionStatus(north, east, south, west);
      return (BlockState)state.setValue(CONNECTION, connection);
   }

   private Table.ConnectionStatus getConnectionStatus(boolean north, boolean east, boolean south, boolean west) {
      if (north && south && !east && !west) {
         return Table.ConnectionStatus.CENTER;
      } else if (!north && !south && east && west) {
         return Table.ConnectionStatus.CENTER;
      } else if (north && east && west && south) {
         return Table.ConnectionStatus.CENTER;
      } else if (north && east && !west && south) {
         return Table.ConnectionStatus.CENTER;
      } else if (!north && east && west && south) {
         return Table.ConnectionStatus.CENTER;
      } else if (north && !east && west && south) {
         return Table.ConnectionStatus.CENTER;
      } else {
         return north && east && west && !south ? Table.ConnectionStatus.CENTER : Table.ConnectionStatus.WITH_LEG;
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livent, ItemStack stack) {
      this.TableState(state, level, pos);
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bolean) {
      if (!statetwo.is(state.getBlock())) {
         this.TableState(state, level, pos);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{CONNECTION});
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return this.TableState(state, level, pos);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.TableState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
   }

   public void placeAt(Level level, BlockPos pos, int number) {
      level.setBlock(pos, this.defaultBlockState(), number);
   }

   public static enum ConnectionStatus implements StringRepresentable {
      CENTER("center"),
      WITH_LEG("with_leg");

      private final String name;

      private ConnectionStatus(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}

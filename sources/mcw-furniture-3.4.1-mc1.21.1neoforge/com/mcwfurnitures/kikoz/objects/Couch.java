package com.mcwfurnitures.kikoz.objects;

import com.mcwfurnitures.kikoz.MacawsFurnitures;
import com.mcwfurnitures.kikoz.storage.CouchEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
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

public class Couch extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<Couch.CouchShape> SHAPE = EnumProperty.create("shape", Couch.CouchShape.class);
   private static final VoxelShape W_SINGLE = Shapes.or(
      Block.box(1.0, 0.0, 14.0, 3.0, 2.0, 16.0),
      new VoxelShape[]{
         Block.box(13.0, 0.0, 14.0, 15.0, 2.0, 16.0),
         Block.box(13.0, 0.0, 1.0, 15.0, 2.0, 3.0),
         Block.box(1.0, 7.0, 12.0, 15.0, 17.0, 16.0),
         Block.box(0.0, 7.0, 0.0, 4.0, 11.0, 15.0),
         Block.box(12.0, 7.0, 0.0, 16.0, 11.0, 15.0),
         Block.box(1.0, 2.0, 0.0, 15.0, 7.0, 16.0),
         Block.box(1.0, 0.0, 1.0, 3.0, 2.0, 3.0)
      }
   );
   private static final VoxelShape W_MIDDLE = Shapes.or(Block.box(0.0, 7.0, 12.0, 16.0, 17.0, 16.0), Block.box(0.0, 2.0, 0.0, 16.0, 7.0, 16.0));
   private static final VoxelShape W_LEFT = Shapes.or(
      Block.box(0.0, 0.0, 14.0, 2.0, 2.0, 16.0),
      new VoxelShape[]{
         Block.box(1.0, 7.0, 12.0, 16.0, 17.0, 16.0),
         Block.box(0.0, 7.0, 0.0, 4.0, 11.0, 16.0),
         Block.box(0.0, 2.0, 0.0, 16.0, 7.0, 16.0),
         Block.box(0.0, 0.0, 1.0, 2.0, 2.0, 3.0)
      }
   );
   private static final VoxelShape W_RIGHT = Shapes.or(
      Block.box(14.0, 0.0, 14.0, 16.0, 2.0, 16.0),
      new VoxelShape[]{
         Block.box(14.0, 0.0, 1.0, 16.0, 2.0, 3.0),
         Block.box(0.0, 7.0, 12.0, 15.0, 17.0, 16.0),
         Block.box(12.0, 7.0, 0.0, 16.0, 11.0, 16.0),
         Block.box(0.0, 2.0, 0.0, 16.0, 7.0, 16.0)
      }
   );
   private static final VoxelShape W_CORNER = Shapes.or(
      Block.box(14.0, 0.0, 14.0, 16.0, 2.0, 16.0),
      new VoxelShape[]{Block.box(0.0, 7.0, 12.0, 16.0, 17.0, 16.0), Block.box(12.0, 7.0, 0.0, 16.0, 17.0, 12.0), Block.box(0.0, 2.0, 0.0, 16.0, 7.0, 16.0)}
   );
   private static final VoxelShape N_SINGLE = MacawsFurnitures.calculateShapes(Direction.EAST, W_SINGLE);
   private static final VoxelShape N_MIDDLE = MacawsFurnitures.calculateShapes(Direction.EAST, W_MIDDLE);
   private static final VoxelShape N_LEFT = MacawsFurnitures.calculateShapes(Direction.EAST, W_LEFT);
   private static final VoxelShape N_RIGHT = MacawsFurnitures.calculateShapes(Direction.EAST, W_RIGHT);
   private static final VoxelShape N_CORNER = MacawsFurnitures.calculateShapes(Direction.EAST, W_CORNER);
   private static final VoxelShape S_SINGLE = MacawsFurnitures.calculateShapes(Direction.WEST, W_SINGLE);
   private static final VoxelShape S_MIDDLE = MacawsFurnitures.calculateShapes(Direction.WEST, W_MIDDLE);
   private static final VoxelShape S_LEFT = MacawsFurnitures.calculateShapes(Direction.WEST, W_LEFT);
   private static final VoxelShape S_RIGHT = MacawsFurnitures.calculateShapes(Direction.WEST, W_RIGHT);
   private static final VoxelShape S_CORNER = MacawsFurnitures.calculateShapes(Direction.WEST, W_CORNER);
   private static final VoxelShape E_SINGLE = MacawsFurnitures.calculateShapes(Direction.SOUTH, W_SINGLE);
   private static final VoxelShape E_MIDDLE = MacawsFurnitures.calculateShapes(Direction.SOUTH, W_MIDDLE);
   private static final VoxelShape E_LEFT = MacawsFurnitures.calculateShapes(Direction.SOUTH, W_LEFT);
   private static final VoxelShape E_RIGHT = MacawsFurnitures.calculateShapes(Direction.SOUTH, W_RIGHT);
   private static final VoxelShape E_CORNER = MacawsFurnitures.calculateShapes(Direction.SOUTH, W_CORNER);

   public Couch(Properties props) {
      super(props);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(SHAPE, Couch.CouchShape.SINGLE)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{SHAPE, FACING});
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      return CouchEntity.create(level, pos, 0.7, player);
   }

   private BlockState StairState(BlockState state, LevelAccessor access, BlockPos pos) {
      boolean north = access.getBlockState(pos.north()).getBlock() == this;
      boolean east = access.getBlockState(pos.east()).getBlock() == this;
      boolean south = access.getBlockState(pos.south()).getBlock() == this;
      boolean west = access.getBlockState(pos.west()).getBlock() == this;
      Direction facingDirection = (Direction)state.getValue(FACING);
      Couch.CouchShape connection = this.getCouchShape(facingDirection, north, east, south, west);
      return (BlockState)((BlockState)state.setValue(SHAPE, connection)).setValue(FACING, facingDirection);
   }

   private Couch.CouchShape getCouchShape(Direction facing, boolean north, boolean east, boolean south, boolean west) {
      switch (facing) {
         case NORTH:
            if (!north && !south) {
               return Couch.CouchShape.SINGLE;
            }

            if (north && south) {
               return Couch.CouchShape.MIDDLE;
            }

            if (!north && south && !east && !west) {
               return Couch.CouchShape.LEFT;
            }

            if (north && !south && !east && !west) {
               return Couch.CouchShape.RIGHT;
            }

            if (north && !south && east) {
               return Couch.CouchShape.LEFT_CORNER;
            }

            if (!north && south && east) {
               return Couch.CouchShape.RIGHT_CORNER;
            }
            break;
         case EAST:
            if (!east && !west) {
               return Couch.CouchShape.SINGLE;
            }

            if (east && west) {
               return Couch.CouchShape.MIDDLE;
            }

            if (!east && west && !north && !south) {
               return Couch.CouchShape.LEFT;
            }

            if (east && !west && !north && !south) {
               return Couch.CouchShape.RIGHT;
            }

            if (east && !west && south) {
               return Couch.CouchShape.LEFT_CORNER;
            }

            if (!east && west && south) {
               return Couch.CouchShape.RIGHT_CORNER;
            }
            break;
         case SOUTH:
            if (!north && !south) {
               return Couch.CouchShape.SINGLE;
            }

            if (north && south) {
               return Couch.CouchShape.MIDDLE;
            }

            if (north && !south && !east && !west) {
               return Couch.CouchShape.LEFT;
            }

            if (!north && south && !east && !west) {
               return Couch.CouchShape.RIGHT;
            }

            if (north && !south && west) {
               return Couch.CouchShape.RIGHT_CORNER;
            }

            if (!north && south && west) {
               return Couch.CouchShape.LEFT_CORNER;
            }
            break;
         case WEST:
            if (!east && !west) {
               return Couch.CouchShape.SINGLE;
            }

            if (east && west) {
               return Couch.CouchShape.MIDDLE;
            }

            if (east && !west && !north && !south) {
               return Couch.CouchShape.LEFT;
            }

            if (!east && west && !north && !south) {
               return Couch.CouchShape.RIGHT;
            }

            if (east && !west && north) {
               return Couch.CouchShape.RIGHT_CORNER;
            }

            if (!east && west && north) {
               return Couch.CouchShape.LEFT_CORNER;
            }
      }

      return Couch.CouchShape.SINGLE;
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
      Direction facing = (Direction)state.getValue(FACING);
      Couch.CouchShape shape = (Couch.CouchShape)state.getValue(SHAPE);

      return switch (facing) {
         case NORTH -> {
            switch (shape) {
               case SINGLE:
                  yield N_SINGLE;
               case LEFT:
                  yield N_LEFT;
               case MIDDLE:
                  yield N_MIDDLE;
               case RIGHT:
                  yield N_RIGHT;
               case LEFT_CORNER:
                  yield N_CORNER;
               case RIGHT_CORNER:
                  yield E_CORNER;
               default:
                  throw new MatchException(null, null);
            }
         }
         case EAST -> {
            switch (shape) {
               case SINGLE:
                  yield E_SINGLE;
               case LEFT:
                  yield E_LEFT;
               case MIDDLE:
                  yield E_MIDDLE;
               case RIGHT:
                  yield E_RIGHT;
               case LEFT_CORNER:
                  yield E_CORNER;
               case RIGHT_CORNER:
                  yield S_CORNER;
               default:
                  throw new MatchException(null, null);
            }
         }
         case SOUTH -> {
            switch (shape) {
               case SINGLE:
                  yield S_SINGLE;
               case LEFT:
                  yield S_LEFT;
               case MIDDLE:
                  yield S_MIDDLE;
               case RIGHT:
                  yield S_RIGHT;
               case LEFT_CORNER:
                  yield S_CORNER;
               case RIGHT_CORNER:
                  yield W_CORNER;
               default:
                  throw new MatchException(null, null);
            }
         }
         case WEST -> {
            switch (shape) {
               case SINGLE:
                  yield W_SINGLE;
               case LEFT:
                  yield W_LEFT;
               case MIDDLE:
                  yield W_MIDDLE;
               case RIGHT:
                  yield W_RIGHT;
               case LEFT_CORNER:
                  yield W_CORNER;
               case RIGHT_CORNER:
                  yield N_CORNER;
               default:
                  throw new MatchException(null, null);
            }
         }
         default -> N_SINGLE;
      };
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         level.setBlock(pos, this.StairState(state, level, pos), 2);
      }
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return this.StairState(state, access, pos);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      BlockPos pos = contx.getClickedPos().below();
      LevelAccessor world = contx.getLevel();
      Direction facingDirection = contx.getHorizontalDirection().getClockWise();
      return (BlockState)this.StairState(super.getStateForPlacement(contx), contx.getLevel(), contx.getClickedPos()).setValue(FACING, facingDirection);
   }

   public static enum CouchShape implements StringRepresentable {
      SINGLE("single"),
      LEFT("left"),
      MIDDLE("middle"),
      RIGHT("right"),
      LEFT_CORNER("left_corner"),
      RIGHT_CORNER("right_corner");

      private final String name;

      private CouchShape(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}

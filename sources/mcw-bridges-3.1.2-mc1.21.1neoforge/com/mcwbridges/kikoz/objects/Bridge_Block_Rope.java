package com.mcwbridges.kikoz.objects;

import com.mcwbridges.kikoz.init.ItemInit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
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

public class Bridge_Block_Rope extends Block {
   public static final EnumProperty<Bridge_Block_Rope.ConnectionStatus> CONNECTION = EnumProperty.create("connection", Bridge_Block_Rope.ConnectionStatus.class);
   protected static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 15.99, 4.0, 15.99);
   protected static final VoxelShape SIDE_0 = Shapes.or(BASE, Block.box(0.0, 2.0, 15.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape SIDE_90 = Shapes.or(BASE, Block.box(0.0, 2.0, 0.0, 1.0, 16.0, 16.0));
   protected static final VoxelShape SIDE_180 = Shapes.or(BASE, Block.box(0.0, 2.0, 0.0, 16.0, 16.0, 1.0));
   protected static final VoxelShape SIDE_270 = Shapes.or(BASE, Block.box(15.0, 2.0, 0.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape CORNER_0 = Shapes.or(SIDE_180, SIDE_90);
   protected static final VoxelShape CORNER_90 = Shapes.or(SIDE_180, SIDE_270);
   protected static final VoxelShape CORNER_180 = Shapes.or(SIDE_270, SIDE_0);
   protected static final VoxelShape CORNER_270 = Shapes.or(SIDE_0, SIDE_90);
   protected static final VoxelShape MIDDLE_90 = Shapes.or(SIDE_0, SIDE_180);
   protected static final VoxelShape MIDDLE_0 = Shapes.or(SIDE_90, SIDE_270);
   protected static final VoxelShape BASE_COLLISION = Block.box(0.0, 0.0, 0.0, 15.99, 3.0, 15.99);
   protected static final VoxelShape COLLISION_SIDE_0 = Shapes.or(BASE, Block.box(0.0, 2.0, 15.0, 16.0, 25.0, 16.0));
   protected static final VoxelShape COLLISION_SIDE_90 = Shapes.or(BASE, Block.box(0.0, 2.0, 0.0, 1.0, 25.0, 16.0));
   protected static final VoxelShape COLLISION_SIDE_180 = Shapes.or(BASE, Block.box(0.0, 2.0, 0.0, 16.0, 25.0, 1.0));
   protected static final VoxelShape COLLISION_SIDE_270 = Shapes.or(BASE, Block.box(15.0, 2.0, 0.0, 16.0, 25.0, 16.0));
   protected static final VoxelShape COLLISION_CORNER_0 = Shapes.or(COLLISION_SIDE_180, COLLISION_SIDE_90);
   protected static final VoxelShape COLLISION_CORNER_90 = Shapes.or(COLLISION_SIDE_180, COLLISION_SIDE_270);
   protected static final VoxelShape COLLISION_CORNER_180 = Shapes.or(COLLISION_SIDE_270, COLLISION_SIDE_0);
   protected static final VoxelShape COLLISION_CORNER_270 = Shapes.or(COLLISION_SIDE_0, COLLISION_SIDE_90);
   protected static final VoxelShape COLLISION_MIDDLE_90 = Shapes.or(COLLISION_SIDE_0, COLLISION_SIDE_180);
   protected static final VoxelShape COLLISION_MIDDLE_0 = Shapes.or(COLLISION_SIDE_90, COLLISION_SIDE_270);
   public static final DirectionProperty FACING_TD = DirectionProperty.create("facing", new Direction[]{Direction.NORTH, Direction.EAST});

   public Bridge_Block_Rope(Properties prop) {
      super(prop);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      switch ((Bridge_Block_Rope.ConnectionStatus)state.getValue(CONNECTION)) {
         case BASE:
            return BASE;
         case MIDDLE_NS:
            return MIDDLE_90;
         case MIDDLE_EW:
            return MIDDLE_0;
         case MIDDLE_END_N:
            return MIDDLE_0;
         case MIDDLE_END_E:
            return MIDDLE_90;
         case MIDDLE_END_S:
            return MIDDLE_0;
         case MIDDLE_END_W:
            return MIDDLE_90;
         case CORNER_NE:
            return CORNER_270;
         case CORNER_NW:
            return CORNER_180;
         case CORNER_SE:
            return CORNER_0;
         case CORNER_SW:
            return CORNER_90;
         case SIDE_N:
            return SIDE_180;
         case SIDE_E:
            return SIDE_270;
         case SIDE_S:
            return SIDE_0;
         case SIDE_W:
            return SIDE_90;
         case END_N_LEFT:
            return SIDE_270;
         case END_N_RIGHT:
            return SIDE_270;
         case END_E_LEFT:
            return SIDE_90;
         case END_E_RIGHT:
            return SIDE_90;
         case END_S_LEFT:
            return SIDE_0;
         case END_S_RIGHT:
            return SIDE_180;
         case END_W_LEFT:
            return SIDE_180;
         case END_W_RIGHT:
            return SIDE_0;
         case BASE_TOGGLED:
            return BASE;
         default:
            return BASE;
      }
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      Direction originalDirection = (Direction)state.getValue(FACING_TD);
      Direction rotatedDirection = rot.rotate(originalDirection);
      if (originalDirection != Direction.NORTH && originalDirection != Direction.SOUTH) {
         rotatedDirection = Direction.EAST;
      } else {
         rotatedDirection = Direction.NORTH;
      }

      return (BlockState)state.setValue(FACING_TD, rotatedDirection);
   }

   public BlockState mirror(BlockState state, Mirror mir) {
      Direction originalDirection = (Direction)state.getValue(FACING_TD);
      if (mir != Mirror.FRONT_BACK) {
         originalDirection.getOpposite();
      }

      Direction rotatedDirection;
      if (originalDirection != Direction.NORTH && originalDirection != Direction.SOUTH) {
         rotatedDirection = Direction.EAST;
      } else {
         rotatedDirection = Direction.NORTH;
      }

      return (BlockState)state.setValue(FACING_TD, rotatedDirection);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      switch ((Bridge_Block_Rope.ConnectionStatus)state.getValue(CONNECTION)) {
         case BASE:
            return BASE;
         case MIDDLE_NS:
            return COLLISION_MIDDLE_90;
         case MIDDLE_EW:
            return COLLISION_MIDDLE_0;
         case MIDDLE_END_N:
            return COLLISION_MIDDLE_0;
         case MIDDLE_END_E:
            return COLLISION_MIDDLE_90;
         case MIDDLE_END_S:
            return COLLISION_MIDDLE_0;
         case MIDDLE_END_W:
            return COLLISION_MIDDLE_90;
         case CORNER_NE:
            return COLLISION_CORNER_270;
         case CORNER_NW:
            return COLLISION_CORNER_180;
         case CORNER_SE:
            return COLLISION_CORNER_0;
         case CORNER_SW:
            return COLLISION_CORNER_90;
         case SIDE_N:
            return COLLISION_SIDE_180;
         case SIDE_E:
            return COLLISION_SIDE_270;
         case SIDE_S:
            return COLLISION_SIDE_0;
         case SIDE_W:
            return COLLISION_SIDE_90;
         case END_N_LEFT:
            return COLLISION_SIDE_270;
         case END_N_RIGHT:
            return COLLISION_SIDE_270;
         case END_E_LEFT:
            return COLLISION_SIDE_90;
         case END_E_RIGHT:
            return COLLISION_SIDE_90;
         case END_S_LEFT:
            return COLLISION_SIDE_0;
         case END_S_RIGHT:
            return COLLISION_SIDE_180;
         case END_W_LEFT:
            return COLLISION_SIDE_180;
         case END_W_RIGHT:
            return COLLISION_SIDE_0;
         case BASE_TOGGLED:
            return BASE_COLLISION;
         default:
            return BASE_COLLISION;
      }
   }

   private BlockState StairState(BlockState state, LevelAccessor access, BlockPos pos) {
      boolean north = access.getBlockState(pos.north()).getBlock() == this;
      boolean east = access.getBlockState(pos.east()).getBlock() == this;
      boolean south = access.getBlockState(pos.south()).getBlock() == this;
      boolean west = access.getBlockState(pos.west()).getBlock() == this;
      Bridge_Block_Rope.ConnectionStatus connection = this.getConnectionStatus(north, east, south, west);
      Direction facingDirection = (Direction)state.getValue(FACING_TD);
      return (BlockState)((BlockState)state.setValue(CONNECTION, connection)).setValue(FACING_TD, facingDirection);
   }

   private Bridge_Block_Rope.ConnectionStatus getConnectionStatus(boolean north, boolean east, boolean south, boolean west) {
      if (!north && !south && !east && !west) {
         return Bridge_Block_Rope.ConnectionStatus.MIDDLE_NS;
      } else if (north && !east && south && !west) {
         return Bridge_Block_Rope.ConnectionStatus.MIDDLE_EW;
      } else if (!north && east && !south && west) {
         return Bridge_Block_Rope.ConnectionStatus.MIDDLE_NS;
      } else if (!north && !east && south && !west) {
         return Bridge_Block_Rope.ConnectionStatus.MIDDLE_END_S;
      } else if (north && !east && !south && !west) {
         return Bridge_Block_Rope.ConnectionStatus.MIDDLE_END_N;
      } else if (!north && !east && !south && west) {
         return Bridge_Block_Rope.ConnectionStatus.MIDDLE_END_W;
      } else if (!north && east && !south && !west) {
         return Bridge_Block_Rope.ConnectionStatus.MIDDLE_END_E;
      } else if (!north && !east && south && west) {
         return Bridge_Block_Rope.ConnectionStatus.CORNER_SW;
      } else if (!north && east && south && !west) {
         return Bridge_Block_Rope.ConnectionStatus.CORNER_SE;
      } else if (north && !east && !south && west) {
         return Bridge_Block_Rope.ConnectionStatus.CORNER_NW;
      } else if (north && east && !south && !west) {
         return Bridge_Block_Rope.ConnectionStatus.CORNER_NE;
      } else if (!north && east && south && west) {
         return Bridge_Block_Rope.ConnectionStatus.SIDE_N;
      } else if (north && !east && south && west) {
         return Bridge_Block_Rope.ConnectionStatus.SIDE_E;
      } else if (north && east && !south && west) {
         return Bridge_Block_Rope.ConnectionStatus.SIDE_S;
      } else {
         return north && east && south && !west ? Bridge_Block_Rope.ConnectionStatus.SIDE_W : Bridge_Block_Rope.ConnectionStatus.BASE;
      }
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      Bridge_Block_Rope.ConnectionStatus connection = (Bridge_Block_Rope.ConnectionStatus)state.getValue(CONNECTION);
      if (item != ItemInit.PLIERS.get() && item != Items.SHEARS) {
         if (itemstack.getItem() instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)itemstack.getItem();
            if (blockItem.getBlock() == this) {
               Direction facing = player.getDirection();
               Direction blockFacingDirection = facing != Direction.NORTH && facing != Direction.SOUTH ? Direction.EAST : Direction.NORTH;
               BlockPos placePos = pos.relative(facing);
               if (level.getBlockState(placePos).isAir()) {
                  level.setBlock(placePos, (BlockState)this.defaultBlockState().setValue(FACING_TD, blockFacingDirection), 3);
                  if (!player.getAbilities().instabuild) {
                     itemstack.shrink(1);
                  }

                  level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                  return ItemInteractionResult.SUCCESS;
               }
            }
         }

         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         Bridge_Block_Rope.ConnectionStatus newConnection = Bridge_Block_Rope.ConnectionStatus.BASE;
         switch (connection) {
            case BASE:
               newConnection = Bridge_Block_Rope.ConnectionStatus.BASE_TOGGLED;
               break;
            case MIDDLE_NS:
               newConnection = Bridge_Block_Rope.ConnectionStatus.CORNER_SW;
               break;
            case MIDDLE_EW:
               newConnection = Bridge_Block_Rope.ConnectionStatus.MIDDLE_NS;
            case MIDDLE_END_N:
            case MIDDLE_END_E:
            case MIDDLE_END_S:
            case MIDDLE_END_W:
            default:
               break;
            case CORNER_NE:
               newConnection = Bridge_Block_Rope.ConnectionStatus.END_E_LEFT;
               break;
            case CORNER_NW:
               newConnection = Bridge_Block_Rope.ConnectionStatus.END_S_LEFT;
               break;
            case CORNER_SE:
               newConnection = Bridge_Block_Rope.ConnectionStatus.END_W_LEFT;
               break;
            case CORNER_SW:
               newConnection = Bridge_Block_Rope.ConnectionStatus.END_N_LEFT;
               break;
            case SIDE_N:
            case SIDE_E:
            case SIDE_S:
            case SIDE_W:
               newConnection = Bridge_Block_Rope.ConnectionStatus.BASE_TOGGLED;
               break;
            case END_N_LEFT:
               newConnection = Bridge_Block_Rope.ConnectionStatus.END_S_RIGHT;
               break;
            case END_N_RIGHT:
               newConnection = Bridge_Block_Rope.ConnectionStatus.CORNER_NE;
               break;
            case END_E_LEFT:
               newConnection = Bridge_Block_Rope.ConnectionStatus.END_W_RIGHT;
               break;
            case END_E_RIGHT:
               newConnection = Bridge_Block_Rope.ConnectionStatus.BASE_TOGGLED;
               break;
            case END_S_LEFT:
               newConnection = Bridge_Block_Rope.ConnectionStatus.END_N_RIGHT;
               break;
            case END_S_RIGHT:
               newConnection = Bridge_Block_Rope.ConnectionStatus.CORNER_NW;
               break;
            case END_W_LEFT:
               newConnection = Bridge_Block_Rope.ConnectionStatus.END_E_RIGHT;
               break;
            case END_W_RIGHT:
               newConnection = Bridge_Block_Rope.ConnectionStatus.CORNER_SE;
               break;
            case BASE_TOGGLED:
               newConnection = Bridge_Block_Rope.ConnectionStatus.MIDDLE_EW;
         }

         level.setBlock(pos, (BlockState)state.setValue(CONNECTION, newConnection), 18);
         return ItemInteractionResult.SUCCESS;
      }
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (statetwo.hasProperty(FACING_TD) && statetwo.getBlock() instanceof Bridge_Block_Rope) {
         Direction facingDirection = (Direction)statetwo.getValue(FACING_TD);
         BlockState newState = (BlockState)state.setValue(FACING_TD, facingDirection);
         if (!statetwo.is(newState.getBlock())) {
            level.setBlock(pos, this.StairState(newState, level, pos), 2);
         }
      } else {
         Direction currentFacing = (Direction)state.getValue(FACING_TD);
         BlockState defaultState = (BlockState)this.defaultBlockState().setValue(FACING_TD, currentFacing);
         level.setBlock(pos, this.StairState(defaultState, level, pos), 2);
      }
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor access, BlockPos pos, BlockPos facingPos) {
      Bridge_Block_Rope.ConnectionStatus connection = (Bridge_Block_Rope.ConnectionStatus)state.getValue(CONNECTION);
      if (connection == Bridge_Block_Rope.ConnectionStatus.BASE_TOGGLED
         || connection == Bridge_Block_Rope.ConnectionStatus.END_N_LEFT
         || connection == Bridge_Block_Rope.ConnectionStatus.END_N_RIGHT
         || connection == Bridge_Block_Rope.ConnectionStatus.END_E_LEFT
         || connection == Bridge_Block_Rope.ConnectionStatus.END_E_RIGHT
         || connection == Bridge_Block_Rope.ConnectionStatus.END_S_LEFT
         || connection == Bridge_Block_Rope.ConnectionStatus.END_S_RIGHT
         || connection == Bridge_Block_Rope.ConnectionStatus.END_W_LEFT
         || connection == Bridge_Block_Rope.ConnectionStatus.END_W_RIGHT) {
         return state;
      } else {
         return dir.getAxis().isHorizontal() && facingState.getBlock() instanceof Bridge_Block_Rope
            ? (BlockState)this.StairState(state, access, pos).setValue(FACING_TD, (Direction)state.getValue(FACING_TD))
            : state;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{CONNECTION, FACING_TD});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      Direction facingDirection = contx.getHorizontalDirection();
      if (facingDirection == Direction.WEST) {
         facingDirection = Direction.EAST;
      } else if (facingDirection == Direction.SOUTH) {
         facingDirection = Direction.NORTH;
      }

      BlockPos pos = contx.getClickedPos().below();
      LevelAccessor world = contx.getLevel();
      BlockState stateBelow = world.getBlockState(pos);
      return stateBelow.getBlock() instanceof Bridge_Block_Rope
         ? null
         : (BlockState)this.StairState(super.getStateForPlacement(contx), contx.getLevel(), contx.getClickedPos()).setValue(FACING_TD, facingDirection);
   }

   public static enum ConnectionStatus implements StringRepresentable {
      BASE("base"),
      MIDDLE_NS("middle_ns"),
      MIDDLE_EW("middle_ew"),
      MIDDLE_END_N("middle_end_n"),
      MIDDLE_END_E("middle_end_e"),
      MIDDLE_END_S("middle_end_s"),
      MIDDLE_END_W("middle_end_w"),
      CORNER_NE("corner_ne"),
      CORNER_NW("corner_nw"),
      CORNER_SE("corner_se"),
      CORNER_SW("corner_sw"),
      SIDE_N("side_n"),
      SIDE_E("side_e"),
      SIDE_S("side_s"),
      SIDE_W("side_w"),
      END_N_LEFT("end_n_left"),
      END_N_RIGHT("end_n_right"),
      END_E_LEFT("end_e_left"),
      END_E_RIGHT("end_e_right"),
      END_S_LEFT("end_s_left"),
      END_S_RIGHT("end_s_right"),
      END_W_LEFT("end_w_left"),
      END_W_RIGHT("end_w_right"),
      BASE_TOGGLED("base_toggled");

      private final String name;

      private ConnectionStatus(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}

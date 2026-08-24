package com.mcwbridges.kikoz.objects;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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

public class Bridge_Stairs extends HorizontalDirectionalBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<Bridge_Stairs.ConnectionStatus> CONNECTION = EnumProperty.create("connection", Bridge_Stairs.ConnectionStatus.class);
   public static final VoxelShape N = Shapes.or(Block.box(0.0, 8.0, 0.0, 8.0, 16.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0));
   public static final VoxelShape E = Shapes.or(Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 8.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0));
   public static final VoxelShape S = Shapes.or(Block.box(8.0, 8.0, 0.0, 16.0, 16.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0));
   public static final VoxelShape W = Shapes.or(Block.box(0.0, 8.0, 8.0, 16.0, 16.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0));
   public static final VoxelShape E_SIDE = Block.box(0.0, 0.0, 0.0, 1.0, 32.0, 16.0);
   public static final VoxelShape S_SIDE = Block.box(0.0, 0.0, 15.0, 16.0, 32.0, 16.0);
   public static final VoxelShape W_SIDE = Block.box(15.0, 0.0, 0.0, 16.0, 32.0, 16.0);
   public static final VoxelShape N_SIDE = Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 1.0);
   protected static final VoxelShape N_DUAL = Shapes.or(N_SIDE, new VoxelShape[]{S_SIDE, N});
   protected static final VoxelShape E_DUAL = Shapes.or(E_SIDE, new VoxelShape[]{W_SIDE, E});
   protected static final VoxelShape S_DUAL = Shapes.or(N_SIDE, new VoxelShape[]{S_SIDE, S});
   protected static final VoxelShape W_DUAL = Shapes.or(E_SIDE, new VoxelShape[]{W_SIDE, W});
   protected static final VoxelShape N_RIGHT = Shapes.or(S_SIDE, N);
   protected static final VoxelShape E_RIGHT = Shapes.or(W_SIDE, E);
   protected static final VoxelShape S_RIGHT = Shapes.or(S_SIDE, S);
   protected static final VoxelShape W_RIGHT = Shapes.or(W_SIDE, W);
   protected static final VoxelShape N_LEFT = Shapes.or(N_SIDE, N);
   protected static final VoxelShape E_LEFT = Shapes.or(E_SIDE, E);
   protected static final VoxelShape S_LEFT = Shapes.or(N_SIDE, S);
   protected static final VoxelShape W_LEFT = Shapes.or(E_SIDE, W);

   public Bridge_Stairs(Properties prop) {
      super(prop);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
            .setValue(CONNECTION, Bridge_Stairs.ConnectionStatus.BASE)
      );
   }

   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return null;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      Direction facing = (Direction)state.getValue(FACING);
      Bridge_Stairs.ConnectionStatus statetype = (Bridge_Stairs.ConnectionStatus)state.getValue(CONNECTION);
      switch (facing) {
         case NORTH:
            switch (statetype) {
               case BASE:
                  return N_DUAL;
               case DOUBLE:
                  return N;
               case LEFT:
                  return N_RIGHT;
               case RIGHT:
                  return N_LEFT;
            }
         case SOUTH:
            switch (statetype) {
               case BASE:
                  return S_DUAL;
               case DOUBLE:
                  return S;
               case LEFT:
                  return S_LEFT;
               case RIGHT:
                  return S_RIGHT;
            }
         case EAST:
            switch (statetype) {
               case BASE:
                  return E_DUAL;
               case DOUBLE:
                  return E;
               case LEFT:
                  return E_LEFT;
               case RIGHT:
                  return E_RIGHT;
            }
         case WEST:
            switch (statetype) {
               case BASE:
                  return W_DUAL;
               case DOUBLE:
                  return W;
               case LEFT:
                  return W_RIGHT;
               case RIGHT:
                  return W_LEFT;
            }
         default:
            return N;
      }
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      Direction facing = (Direction)state.getValue(FACING);
      switch (facing) {
         case NORTH:
            return N;
         case SOUTH:
            return S;
         case EAST:
            return E;
         case WEST:
            return W;
         default:
            return N;
      }
   }

   private BlockState StairState(BlockState state, LevelAccessor access, BlockPos pos) {
      BlockState northState = access.getBlockState(pos.north());
      BlockState eastState = access.getBlockState(pos.east());
      BlockState southState = access.getBlockState(pos.south());
      BlockState westState = access.getBlockState(pos.west());
      boolean north = northState.getBlock() == this;
      boolean east = eastState.getBlock() == this;
      boolean south = southState.getBlock() == this;
      boolean west = westState.getBlock() == this;
      Bridge_Stairs.ConnectionStatus connection = this.getConnectionStatus((Direction)state.getValue(FACING), north, east, south, west);
      return (BlockState)state.setValue(CONNECTION, connection);
   }

   private Bridge_Stairs.ConnectionStatus getConnectionStatus(Direction facing, boolean north, boolean east, boolean south, boolean west) {
      boolean isConnectedNorthSouth = north && south;
      boolean isConnectedEastWest = east && west;
      switch (facing) {
         case NORTH:
            if (isConnectedNorthSouth) {
               return Bridge_Stairs.ConnectionStatus.DOUBLE;
            } else if (south) {
               return Bridge_Stairs.ConnectionStatus.RIGHT;
            } else {
               if (north) {
                  return Bridge_Stairs.ConnectionStatus.LEFT;
               }

               return Bridge_Stairs.ConnectionStatus.BASE;
            }
         case SOUTH:
            if (isConnectedNorthSouth) {
               return Bridge_Stairs.ConnectionStatus.DOUBLE;
            } else if (north) {
               return Bridge_Stairs.ConnectionStatus.RIGHT;
            } else {
               if (south) {
                  return Bridge_Stairs.ConnectionStatus.LEFT;
               }

               return Bridge_Stairs.ConnectionStatus.BASE;
            }
         case EAST:
            if (isConnectedEastWest) {
               return Bridge_Stairs.ConnectionStatus.DOUBLE;
            } else if (west) {
               return Bridge_Stairs.ConnectionStatus.RIGHT;
            } else {
               if (east) {
                  return Bridge_Stairs.ConnectionStatus.LEFT;
               }

               return Bridge_Stairs.ConnectionStatus.BASE;
            }
         case WEST:
            if (isConnectedEastWest) {
               return Bridge_Stairs.ConnectionStatus.DOUBLE;
            } else if (east) {
               return Bridge_Stairs.ConnectionStatus.RIGHT;
            } else {
               if (west) {
                  return Bridge_Stairs.ConnectionStatus.LEFT;
               }

               return Bridge_Stairs.ConnectionStatus.BASE;
            }
         default:
            return Bridge_Stairs.ConnectionStatus.BASE;
      }
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return this.StairState(state, access, pos);
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
      return Shapes.empty();
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      if (itemstack.getItem() instanceof BlockItem) {
         BlockItem blockItem = (BlockItem)itemstack.getItem();
         if (blockItem.getBlock() == this) {
            Direction blockFacingDirection = (Direction)state.getValue(FACING);
            Direction facing = player.getDirection();
            BlockPos placePos = pos.relative(facing).above();
            if (level.getBlockState(placePos).isAir()) {
               level.setBlock(placePos, (BlockState)this.defaultBlockState().setValue(FACING, blockFacingDirection), 3);
               if (!player.getAbilities().instabuild) {
                  itemstack.shrink(1);
               }

               level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
               return ItemInteractionResult.SUCCESS;
            }
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      BlockPos pos = contx.getClickedPos().below();
      LevelAccessor world = contx.getLevel();
      BlockState stateBelow = world.getBlockState(pos);
      return stateBelow.getBlock() instanceof Bridge_Stairs
         ? null
         : (BlockState)this.StairState(super.getStateForPlacement(contx), contx.getLevel(), contx.getClickedPos())
            .setValue(FACING, contx.getHorizontalDirection().getClockWise());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, CONNECTION});
   }

   public static enum ConnectionStatus implements StringRepresentable {
      BASE("base"),
      DOUBLE("double"),
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

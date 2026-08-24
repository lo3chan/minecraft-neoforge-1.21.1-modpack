package com.mcwbridges.kikoz.objects.items;

import com.mcwbridges.kikoz.objects.Bridge_Block;
import com.mcwbridges.kikoz.objects.Bridge_Block_Rope;
import com.mcwbridges.kikoz.objects.Bridge_Stairs;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Bridge_Torch extends HorizontalDirectionalBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<Bridge_Torch.LightState> LIGHTSTATE = EnumProperty.create("lightstate", Bridge_Torch.LightState.class);
   protected static final VoxelShape EAST = Block.box(6.2, 0.0, 0.0, 9.8, 8.0, 2.8);
   protected static final VoxelShape SOUTH = Block.box(13.2, 0.0, 6.2, 16.0, 8.0, 9.8);
   protected static final VoxelShape WEST = Block.box(6.2, 0.0, 13.2, 9.8, 8.0, 16.0);
   protected static final VoxelShape NORTH = Block.box(0.0, 0.0, 6.2, 2.8, 8.0, 9.8);
   protected static final VoxelShape EAST_STAIR = Block.box(6.2, 6.0, 0.0, 9.8, 16.0, 2.8);
   protected static final VoxelShape SOUTH_STAIR = Block.box(13.2, 6.0, 6.2, 16.0, 16.0, 9.8);
   protected static final VoxelShape WEST_STAIR = Block.box(6.2, 6.0, 13.2, 9.8, 16.0, 16.0);
   protected static final VoxelShape NORTH_STAIR = Block.box(0.0, 6.0, 6.2, 2.8, 16.0, 9.8);
   protected int lightValue;

   public Bridge_Torch(Properties prop, int lightValue) {
      super(prop);
      this.lightValue = lightValue;
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
            .setValue(LIGHTSTATE, Bridge_Torch.LightState.BRIDGE)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      Direction facing = (Direction)state.getValue(FACING);
      Bridge_Torch.LightState lightState = (Bridge_Torch.LightState)state.getValue(LIGHTSTATE);
      if (lightState == Bridge_Torch.LightState.BRIDGE) {
         switch (facing) {
            case NORTH:
               return NORTH;
            case SOUTH:
               return SOUTH;
            case WEST:
               return WEST;
            case EAST:
            default:
               return EAST;
         }
      } else {
         switch (facing) {
            case NORTH:
               return NORTH_STAIR;
            case SOUTH:
               return SOUTH_STAIR;
            case WEST:
               return WEST_STAIR;
            case EAST:
            default:
               return EAST_STAIR;
         }
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      return Shapes.empty();
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean bool) {
      if (fromPos.above().equals(pos) && !level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below())) {
         level.destroyBlock(pos, true);
      }
   }

   public int getLightEmission(BlockState state, BlockGetter reader, BlockPos pos) {
      return this.lightValue;
   }

   public int getLightValue(BlockState state, BlockGetter reader, BlockPos pos) {
      return 15;
   }

   public float getAmbientOcclusionLightValue(BlockState state, BlockGetter reader, BlockPos pos) {
      return 1.0F;
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
      return Shapes.empty();
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return null;
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, LIGHTSTATE});
   }

   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      BlockPos belowPos = contx.getClickedPos().below();
      BlockState belowState = contx.getLevel().getBlockState(belowPos);
      BlockState state;
      if (belowState.getBlock() instanceof Bridge_Block) {
         state = this.handleBridgeBlockPlacement(belowState);
      } else if (belowState.getBlock() instanceof Bridge_Block_Rope) {
         state = this.handleBridgeBlockRopePlacement(belowState);
      } else if (belowState.getBlock() instanceof Bridge_Stairs) {
         state = this.handleBridgeStairsPlacement(belowState);
      } else {
         state = (BlockState)this.defaultBlockState().setValue(FACING, contx.getHorizontalDirection().getClockWise());
      }

      return state != null ? (BlockState)state.setValue(LIGHTSTATE, Bridge_Torch.LightState.byState(belowState)) : null;
   }

   private BlockState handleBridgeBlockPlacement(BlockState belowState) {
      Bridge_Block.ConnectionStatus connectionStatus = (Bridge_Block.ConnectionStatus)belowState.getValue(Bridge_Block.CONNECTION);
      switch (connectionStatus) {
         case BASE:
            return null;
         case SIDE_E:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.SOUTH);
         case SIDE_N:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case SIDE_S:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.WEST);
         case SIDE_W:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case MIDDLE_EW:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case MIDDLE_NS:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case CORNER_NE:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case CORNER_NW:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.WEST);
         case CORNER_SE:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case CORNER_SW:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         default:
            return this.defaultBlockState();
      }
   }

   private BlockState handleBridgeBlockRopePlacement(BlockState belowState) {
      Bridge_Block_Rope.ConnectionStatus connectionStatus = (Bridge_Block_Rope.ConnectionStatus)belowState.getValue(Bridge_Block_Rope.CONNECTION);
      switch (connectionStatus) {
         case BASE:
         case BASE_TOGGLED:
            return null;
         case SIDE_E:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.SOUTH);
         case SIDE_N:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case SIDE_S:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.WEST);
         case SIDE_W:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case MIDDLE_EW:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case MIDDLE_NS:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case CORNER_NE:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case CORNER_NW:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.WEST);
         case CORNER_SE:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case CORNER_SW:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case MIDDLE_END_N:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case MIDDLE_END_E:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case MIDDLE_END_S:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case MIDDLE_END_W:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case END_E_LEFT:
         case END_E_RIGHT:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH);
         case END_N_LEFT:
         case END_N_RIGHT:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.SOUTH);
         case END_S_LEFT:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.WEST);
         case END_S_RIGHT:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case END_W_LEFT:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.EAST);
         case END_W_RIGHT:
            return (BlockState)this.defaultBlockState().setValue(FACING, Direction.WEST);
         default:
            return this.defaultBlockState();
      }
   }

   private BlockState handleBridgeStairsPlacement(BlockState belowState) {
      Bridge_Stairs.ConnectionStatus connectionStatus = (Bridge_Stairs.ConnectionStatus)belowState.getValue(Bridge_Stairs.CONNECTION);
      Direction stairsFacing = (Direction)belowState.getValue(Bridge_Stairs.FACING);
      if (connectionStatus == Bridge_Stairs.ConnectionStatus.DOUBLE) {
         return null;
      } else {
         Direction newFacing;
         if (connectionStatus == Bridge_Stairs.ConnectionStatus.LEFT) {
            newFacing = this.rotateLeft(stairsFacing);
         } else {
            newFacing = this.rotateRight(stairsFacing);
         }

         return (BlockState)this.defaultBlockState().setValue(FACING, newFacing);
      }
   }

   private Direction rotateLeft(Direction facing) {
      switch (facing) {
         case NORTH:
            return Direction.WEST;
         case SOUTH:
            return Direction.EAST;
         case WEST:
            return Direction.SOUTH;
         case EAST:
            return Direction.NORTH;
         default:
            return facing;
      }
   }

   private Direction rotateRight(Direction facing) {
      switch (facing) {
         case NORTH:
            return Direction.EAST;
         case SOUTH:
            return Direction.WEST;
         case WEST:
            return Direction.NORTH;
         case EAST:
            return Direction.SOUTH;
         default:
            return facing;
      }
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return dir == Direction.DOWN
         ? (BlockState)state.setValue(LIGHTSTATE, Bridge_Torch.LightState.byState(statetwo))
         : super.updateShape(state, dir, statetwo, access, pos, postwo);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
      Direction facing = (Direction)stateIn.getValue(FACING);
      double d0 = pos.getY() + 0.5;
      double d1 = pos.getY() + 0.9;
      double southX = pos.getX() + 0.5;
      double southZ = pos.getZ() + 0.95;
      double northZ = pos.getZ() + 0.05;
      double westX = pos.getX() + 0.05;
      double westZ = pos.getZ() + 0.5;
      double eastX = pos.getX() + 0.95;
      double eastZ = pos.getZ() + 0.5;
      Bridge_Torch.LightState i = (Bridge_Torch.LightState)stateIn.getValue(LIGHTSTATE);
      if (i == Bridge_Torch.LightState.BRIDGE) {
         switch (facing) {
            case NORTH:
               worldIn.addParticle(ParticleTypes.SMOKE, westX, d0, westZ, 0.0, 0.0, 0.0);
               worldIn.addParticle(ParticleTypes.FLAME, westX, d0, westZ, 0.0, 0.0, 0.0);
               break;
            case SOUTH:
               worldIn.addParticle(ParticleTypes.SMOKE, eastX, d0, eastZ, 0.0, 0.0, 0.0);
               worldIn.addParticle(ParticleTypes.FLAME, eastX, d0, eastZ, 0.0, 0.0, 0.0);
               break;
            case WEST:
               worldIn.addParticle(ParticleTypes.SMOKE, southX, d0, southZ, 0.0, 0.0, 0.0);
               worldIn.addParticle(ParticleTypes.FLAME, southX, d0, southZ, 0.0, 0.0, 0.0);
               break;
            case EAST:
               worldIn.addParticle(ParticleTypes.SMOKE, southX, d0, northZ, 0.0, 0.0, 0.0);
               worldIn.addParticle(ParticleTypes.FLAME, southX, d0, northZ, 0.0, 0.0, 0.0);
         }
      } else {
         switch (facing) {
            case NORTH:
               worldIn.addParticle(ParticleTypes.SMOKE, westX, d1, westZ, 0.0, 0.0, 0.0);
               worldIn.addParticle(ParticleTypes.FLAME, westX, d1, westZ, 0.0, 0.0, 0.0);
               break;
            case SOUTH:
               worldIn.addParticle(ParticleTypes.SMOKE, eastX, d1, eastZ, 0.0, 0.0, 0.0);
               worldIn.addParticle(ParticleTypes.FLAME, eastX, d1, eastZ, 0.0, 0.0, 0.0);
               break;
            case WEST:
               worldIn.addParticle(ParticleTypes.SMOKE, southX, d1, southZ, 0.0, 0.0, 0.0);
               worldIn.addParticle(ParticleTypes.FLAME, southX, d1, southZ, 0.0, 0.0, 0.0);
               break;
            case EAST:
               worldIn.addParticle(ParticleTypes.SMOKE, southX, d1, northZ, 0.0, 0.0, 0.0);
               worldIn.addParticle(ParticleTypes.FLAME, southX, d1, northZ, 0.0, 0.0, 0.0);
         }
      }
   }

   public static enum LightState implements StringRepresentable {
      BRIDGE("bridge"),
      STAIR("stair");

      private final String name;

      private LightState(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Bridge_Torch.LightState byState(BlockState state) {
         return state.getBlock() instanceof Bridge_Stairs ? STAIR : BRIDGE;
      }
   }
}

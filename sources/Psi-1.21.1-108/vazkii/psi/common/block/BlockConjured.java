package vazkii.psi.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.tile.TileConjured;

public class BlockConjured extends Block implements EntityBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty SOLID = BooleanProperty.create("solid");
   public static final BooleanProperty LIGHT = BooleanProperty.create("light");
   public static final BooleanProperty BLOCK_UP = BooleanProperty.create("block_up");
   public static final BooleanProperty BLOCK_DOWN = BooleanProperty.create("block_down");
   public static final BooleanProperty BLOCK_NORTH = BooleanProperty.create("block_north");
   public static final BooleanProperty BLOCK_SOUTH = BooleanProperty.create("block_south");
   public static final BooleanProperty BLOCK_WEST = BooleanProperty.create("block_west");
   public static final BooleanProperty BLOCK_EAST = BooleanProperty.create("block_east");
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape LIGHT_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0);

   public BlockConjured(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getStateDefinition()
                                       .any())
                                    .setValue(LIGHT, false))
                                 .setValue(SOLID, false))
                              .setValue(WATERLOGGED, false))
                           .setValue(BLOCK_DOWN, false))
                        .setValue(BLOCK_UP, false))
                     .setValue(BLOCK_EAST, false))
                  .setValue(BLOCK_WEST, false))
               .setValue(BLOCK_NORTH, false))
            .setValue(BLOCK_SOUTH, false)
      );
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(@NotNull BlockState stateIn, Level worldIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
      BlockEntity inWorld = worldIn.getBlockEntity(pos);
      if (inWorld instanceof TileConjured) {
         ((TileConjured)inWorld).doParticles();
      }
   }

   public Integer getBeaconColorMultiplier(@NotNull BlockState state, LevelReader world, @NotNull BlockPos pos, @NotNull BlockPos beaconPos) {
      BlockEntity inWorld = world.getBlockEntity(pos);
      return inWorld instanceof TileConjured ? Psi.proxy.getColorForColorizer(((TileConjured)inWorld).colorizer) : null;
   }

   public void tick(@NotNull BlockState state, ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource random) {
      world.removeBlock(pos, false);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{SOLID, LIGHT, BLOCK_UP, BLOCK_DOWN, BLOCK_NORTH, BLOCK_SOUTH, BLOCK_WEST, BLOCK_EAST, WATERLOGGED});
   }

   public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
      return true;
   }

   @NotNull
   public BlockState updateShape(
      @NotNull BlockState state,
      Direction facing,
      @NotNull BlockState facingState,
      @NotNull LevelAccessor world,
      @NotNull BlockPos currentPos,
      @NotNull BlockPos facingPos
   ) {
      BooleanProperty prop = switch (facing) {
         case UP -> BLOCK_UP;
         case NORTH -> BLOCK_NORTH;
         case SOUTH -> BLOCK_SOUTH;
         case WEST -> BLOCK_WEST;
         case EAST -> BLOCK_EAST;
         default -> BLOCK_DOWN;
      };
      if ((Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return state.getBlock() == facingState.getBlock()
            && state.getValue(LIGHT) == facingState.getValue(LIGHT)
            && state.getValue(SOLID) == facingState.getValue(SOLID)
         ? (BlockState)state.setValue(prop, true)
         : (BlockState)state.setValue(prop, false);
   }

   public int getLightEmission(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
      return state.getValue(LIGHT) ? 15 : 0;
   }

   @NotNull
   public VoxelShape getCollisionShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return state.getValue(SOLID) ? Shapes.block() : Shapes.empty();
   }

   @NotNull
   public VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return state.getValue(SOLID) ? Shapes.block() : LIGHT_SHAPE;
   }

   @NotNull
   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @NotNull
   public VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return Shapes.empty();
   }

   public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
      return 1.0F;
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new TileConjured(pos, state);
   }
}

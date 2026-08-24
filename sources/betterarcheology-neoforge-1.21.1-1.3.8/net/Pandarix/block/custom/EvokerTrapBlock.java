package net.Pandarix.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvokerTrapBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<EvokerTrapBlock> CODEC = simpleCodec(EvokerTrapBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");
   private static final int fangCooldown = 40;
   public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

   @NotNull
   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   public EvokerTrapBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(TRIGGERED, false)).setValue(ACTIVE, false)
      );
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos sourcePos, boolean notify) {
      super.neighborChanged(blockState, level, blockPos, block, sourcePos, notify);
      boolean powered = level.hasNeighborSignal(blockPos) || level.hasNeighborSignal(blockPos.above());
      boolean active = (Boolean)blockState.getValue(ACTIVE);
      if (powered && !active) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(ACTIVE, true), 3);
         this.spawnFangs(blockState, level, blockPos, level.getRandom());
         level.scheduleTick(blockPos, this, 40);
      } else if (!powered && active) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(TRIGGERED, false), 4);
      }
   }

   private void spawnFangs(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (!level.isClientSide()) {
         int maxFangs = 3;
         switch ((Direction)state.getValue(FACING)) {
            case NORTH:
               for (int i = 0; i < maxFangs; i++) {
                  level.addFreshEntity(new EvokerFangs(level, pos.getX() + 0.5, pos.getY(), pos.getZ() - 0.5 - i * 1.5, (float)Math.toRadians(90.0), 0, null));
               }
               break;
            case SOUTH:
               for (int i = 0; i < maxFangs; i++) {
                  level.addFreshEntity(new EvokerFangs(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 1.5 + i * 1.5, (float)Math.toRadians(90.0), 0, null));
               }
               break;
            case EAST:
               for (int i = 0; i < maxFangs; i++) {
                  level.addFreshEntity(new EvokerFangs(level, pos.getX() + 1.5 + i * 1.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0, null));
               }
               break;
            case WEST:
               for (int i = 0; i < maxFangs; i++) {
                  level.addFreshEntity(new EvokerFangs(level, pos.getX() - 0.5 - i * 1.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0, null));
               }
               break;
            default:
               for (int i = 0; i < maxFangs; i++) {
                  level.addFreshEntity(new EvokerFangs(level, pos.getX() + 0.5, pos.getY(), pos.getZ() - 0.5 - i * 1.5, 0.0F, 0, null));
               }
         }
      }
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      super.tick(pState, pLevel, pPos, pRandom);
      pLevel.setBlock(pPos, (BlockState)pState.setValue(ACTIVE, false), 3);
   }

   @NotNull
   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{FACING, TRIGGERED, ACTIVE});
   }
}

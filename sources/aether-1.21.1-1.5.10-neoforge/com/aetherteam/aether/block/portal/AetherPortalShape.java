package com.aetherteam.aether.block.portal;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AetherPortalShape {
   private static final StatePredicate FRAME = (state, level, pos) -> state.is(AetherTags.Blocks.AETHER_PORTAL_BLOCKS);
   private final LevelAccessor level;
   private final Axis axis;
   private final Direction rightDir;
   private int numPortalBlocks;
   @Nullable
   private BlockPos bottomLeft;
   private int height;
   private final int width;

   public static Optional<AetherPortalShape> findEmptyAetherPortalShape(LevelAccessor level, BlockPos bottomLeft, Axis axis) {
      return findPortalShape(level, bottomLeft, shape -> shape.isValid() && shape.numPortalBlocks == 0, axis);
   }

   public static Optional<AetherPortalShape> findPortalShape(LevelAccessor level, BlockPos bottomLeft, Predicate<AetherPortalShape> predicate, Axis axis) {
      Optional<AetherPortalShape> optional = Optional.of(new AetherPortalShape(level, bottomLeft, axis)).filter(predicate);
      if (optional.isPresent()) {
         return optional;
      } else {
         Axis directionAxis = axis == Axis.X ? Axis.Z : Axis.X;
         return Optional.of(new AetherPortalShape(level, bottomLeft, directionAxis)).filter(predicate);
      }
   }

   public AetherPortalShape(LevelAccessor level, BlockPos bottomLeft, Axis axis) {
      this.level = level;
      this.axis = axis;
      this.rightDir = axis == Axis.X ? Direction.WEST : Direction.SOUTH;
      this.bottomLeft = this.calculateBottomLeft(bottomLeft);
      if (this.bottomLeft == null) {
         this.bottomLeft = bottomLeft;
         this.width = 1;
         this.height = 1;
      } else {
         this.width = this.calculateWidth();
         if (this.width > 0) {
            this.height = this.calculateHeight();
         }
      }
   }

   @Nullable
   private BlockPos calculateBottomLeft(BlockPos pos) {
      int i = Math.max(this.level.getMinBuildHeight(), pos.getY() - 21);

      while (pos.getY() > i && isEmpty(this.level.getBlockState(pos.below()))) {
         pos = pos.below();
      }

      Direction direction = this.rightDir.getOpposite();
      int j = this.getDistanceUntilEdgeAboveFrame(pos, direction) - 1;
      return j < 0 ? null : pos.relative(direction, j);
   }

   private int calculateWidth() {
      int i = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
      return i >= 2 && i <= 21 ? i : 0;
   }

   private int getDistanceUntilEdgeAboveFrame(BlockPos pos, Direction direction) {
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = 0; i <= 21; i++) {
         mutablePos.set(pos).move(direction, i);
         BlockState blockState = this.level.getBlockState(mutablePos);
         if (!isEmpty(blockState)) {
            if (FRAME.test(blockState, this.level, mutablePos)) {
               return i;
            }
            break;
         }

         BlockState belowState = this.level.getBlockState(mutablePos.move(Direction.DOWN));
         if (!FRAME.test(belowState, this.level, mutablePos)) {
            break;
         }
      }

      return 0;
   }

   private int calculateHeight() {
      MutableBlockPos mutablePos = new MutableBlockPos();
      int i = this.getDistanceUntilTop(mutablePos);
      return i >= 3 && i <= 21 && this.hasTopFrame(mutablePos, i) ? i : 0;
   }

   private boolean hasTopFrame(MutableBlockPos mutablePos, int amount) {
      for (int i = 0; i < this.width; i++) {
         MutableBlockPos movedPos = mutablePos.set(this.bottomLeft).move(Direction.UP, amount).move(this.rightDir, i);
         if (!FRAME.test(this.level.getBlockState(movedPos), this.level, movedPos)) {
            return false;
         }
      }

      return true;
   }

   private int getDistanceUntilTop(MutableBlockPos mutablePos) {
      for (int i = 0; i < 21; i++) {
         mutablePos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
         if (!FRAME.test(this.level.getBlockState(mutablePos), this.level, mutablePos)) {
            return i;
         }

         mutablePos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
         if (!FRAME.test(this.level.getBlockState(mutablePos), this.level, mutablePos)) {
            return i;
         }

         for (int j = 0; j < this.width; j++) {
            mutablePos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
            BlockState blockState = this.level.getBlockState(mutablePos);
            if (!isEmpty(blockState)) {
               return i;
            }

            if (blockState.is((Block)AetherBlocks.AETHER_PORTAL.get())) {
               this.numPortalBlocks++;
            }
         }
      }

      return 21;
   }

   private static boolean isEmpty(BlockState state) {
      return state.isAir() || state.is(Blocks.WATER) || state.is((Block)AetherBlocks.AETHER_PORTAL.get());
   }

   public boolean isValid() {
      return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
   }

   public void createPortalBlocks() {
      BlockState blockState = (BlockState)((AetherPortalBlock)AetherBlocks.AETHER_PORTAL.get()).defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
      BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1))
         .forEach(pos -> this.level.setBlock(pos, blockState, 18));
   }

   public boolean isComplete() {
      return this.isValid() && this.numPortalBlocks == this.width * this.height;
   }

   public static Vec3 findCollisionFreePosition(Vec3 pos, ServerLevel level, Entity entity, EntityDimensions dimensions) {
      if (!(dimensions.width() > 4.0F) && !(dimensions.height() > 4.0F)) {
         double y = dimensions.height() / 2.0;
         Vec3 vec3 = pos.add(0.0, y, 0.0);
         VoxelShape shape = Shapes.create(AABB.ofSize(vec3, dimensions.width(), 0.0, dimensions.width()).expandTowards(0.0, 1.0, 0.0).inflate(1.0E-6));
         Optional<Vec3> optional = level.findFreePosition(entity, shape, vec3, dimensions.width(), dimensions.height(), dimensions.width());
         Optional<Vec3> optionalPos = optional.map(vec -> vec.subtract(0.0, y, 0.0));
         return optionalPos.orElse(pos);
      } else {
         return pos;
      }
   }
}

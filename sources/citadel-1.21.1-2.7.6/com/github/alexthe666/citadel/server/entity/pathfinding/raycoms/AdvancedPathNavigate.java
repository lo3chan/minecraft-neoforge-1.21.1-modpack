package com.github.alexthe666.citadel.server.entity.pathfinding.raycoms;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.pathjobs.AbstractPathJob;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.pathjobs.PathJobMoveAwayFromLocation;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.pathjobs.PathJobMoveToLocation;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.pathjobs.PathJobRandomPos;
import com.github.alexthe666.citadel.server.world.WorldChunkUtil;
import java.util.Arrays;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class AdvancedPathNavigate extends AbstractAdvancedPathNavigate {
   public static final double MIN_Y_DISTANCE = 0.001;
   public static final int MAX_SPEED_ALLOWED = 2;
   public static final double MIN_SPEED_ALLOWED = 0.1;
   @Nullable
   private PathResult<AbstractPathJob> pathResult;
   private long pathStartTime = 0L;
   private final BlockPos spawnedPos = BlockPos.ZERO;
   private BlockPos desiredPos;
   private int desiredPosTimeout = 0;
   private IStuckHandler stuckHandler;
   private boolean isSneaking = true;
   private double swimSpeedFactor = 1.0;
   public boolean overrideDefaultDimensions = false;
   private float width = 1.0F;
   private float height = 1.0F;

   public AdvancedPathNavigate(Mob entity, Level world) {
      this(entity, world, AdvancedPathNavigate.MovementType.WALKING);
   }

   public AdvancedPathNavigate(Mob entity, Level world, AdvancedPathNavigate.MovementType type) {
      this(entity, world, type, 1.0F, 1.0F);
   }

   public AdvancedPathNavigate(Mob entity, Level world, AdvancedPathNavigate.MovementType type, float width, float height) {
      this(entity, world, type, width, height, PathingStuckHandler.createStuckHandler().withTeleportSteps(6).withTeleportOnFullStuck());
   }

   public AdvancedPathNavigate(Mob entity, Level world, AdvancedPathNavigate.MovementType type, float width, float height, PathingStuckHandler stuckHandler) {
      super(entity, world);
      switch (type) {
         case WALKING:
            this.nodeEvaluator = new WalkNodeEvaluator();
            break;
         case FLYING:
            this.nodeEvaluator = new FlyNodeEvaluator();
            this.getPathingOptions().setIsFlying(true);
            break;
         case CLIMBING:
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.getPathingOptions().setCanClimb(true);
      }

      this.nodeEvaluator.setCanPassDoors(true);
      this.getPathingOptions().setEnterDoors(true);
      this.nodeEvaluator.setCanOpenDoors(true);
      this.getPathingOptions().setCanOpenDoors(true);
      this.nodeEvaluator.setCanFloat(true);
      this.getPathingOptions().setCanSwim(true);
      this.width = width;
      this.height = height;
      this.stuckHandler = stuckHandler;
   }

   @Override
   public BlockPos getDestination() {
      return this.destination;
   }

   @Nullable
   @Override
   public PathResult moveAwayFromXYZ(BlockPos avoid, double range, double speedFactor, boolean safeDestination) {
      BlockPos start = AbstractPathJob.prepareStart(this.ourEntity);
      return this.setPathJob(
         new PathJobMoveAwayFromLocation(
            this.ourEntity.level(), start, avoid, (int)range, (int)this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(), this.ourEntity
         ),
         null,
         speedFactor,
         safeDestination
      );
   }

   @Nullable
   @Override
   public PathResult moveToRandomPos(double range, double speedFactor) {
      if (this.pathResult != null && this.pathResult.getJob() instanceof PathJobRandomPos) {
         return this.pathResult;
      } else {
         this.desiredPos = BlockPos.ZERO;
         int theRange = (int)(this.mob.getRandom().nextInt((int)range) + range / 2.0);
         BlockPos start = AbstractPathJob.prepareStart(this.ourEntity);
         return this.setPathJob(
            new PathJobRandomPos(this.ourEntity.level(), start, theRange, (int)this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(), this.ourEntity),
            null,
            speedFactor,
            true
         );
      }
   }

   @Nullable
   @Override
   public PathResult moveToRandomPosAroundX(int range, double speedFactor, BlockPos pos) {
      if (this.pathResult != null
         && this.pathResult.getJob() instanceof PathJobRandomPos
         && ((PathJobRandomPos)this.pathResult.getJob()).posAndRangeMatch(range, pos)) {
         return this.pathResult;
      } else {
         this.desiredPos = BlockPos.ZERO;
         return this.setPathJob(
            new PathJobRandomPos(
               this.ourEntity.level(),
               AbstractPathJob.prepareStart(this.ourEntity),
               3,
               (int)this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
               range,
               this.ourEntity,
               pos
            ),
            pos,
            speedFactor,
            true
         );
      }
   }

   @Override
   public PathResult moveToRandomPos(
      int range, double speedFactor, Tuple<BlockPos, BlockPos> corners, AbstractAdvancedPathNavigate.RestrictionType restrictionType
   ) {
      if (this.pathResult != null && this.pathResult.getJob() instanceof PathJobRandomPos) {
         return this.pathResult;
      } else {
         this.desiredPos = BlockPos.ZERO;
         int theRange = this.mob.getRandom().nextInt(range) + range / 2;
         BlockPos start = AbstractPathJob.prepareStart(this.ourEntity);
         return this.setPathJob(
            new PathJobRandomPos(
               this.ourEntity.level(),
               start,
               theRange,
               (int)this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
               this.ourEntity,
               (BlockPos)corners.getA(),
               (BlockPos)corners.getB(),
               restrictionType
            ),
            null,
            speedFactor,
            true
         );
      }
   }

   @Nullable
   public PathResult setPathJob(AbstractPathJob job, BlockPos dest, double speedFactor, boolean safeDestination) {
      this.stop();
      this.destination = dest;
      this.originalDestination = dest;
      if (safeDestination) {
         this.desiredPos = dest;
         if (dest != null) {
            this.desiredPosTimeout = 1000;
         }
      }

      this.walkSpeedFactor = speedFactor;
      if (!(speedFactor > 2.0) && !(speedFactor < 0.1)) {
         job.setPathingOptions(this.getPathingOptions());
         this.pathResult = job.getResult();
         this.pathResult.startJob(Pathfinding.getExecutor());
         return this.pathResult;
      } else {
         Citadel.LOGGER.error("Tried to set a bad speed:{} for entity:{}", speedFactor, this.ourEntity, new Exception());
         return null;
      }
   }

   public boolean isDone() {
      return (this.pathResult == null || this.pathResult.isFinished() && this.pathResult.getStatus() != PathFindingStatus.CALCULATION_COMPLETE)
         && super.isDone();
   }

   public void tick() {
      if (this.overrideDefaultDimensions) {
         this.nodeEvaluator.entityWidth = Mth.floor(this.width + 1.0F);
         this.nodeEvaluator.entityHeight = Mth.floor(this.height + 1.0F);
         this.nodeEvaluator.entityDepth = Mth.floor(this.width + 1.0F);
      } else {
         this.nodeEvaluator.entityWidth = Mth.floor(this.ourEntity.getBbWidth() + 1.0F);
         this.nodeEvaluator.entityHeight = Mth.floor(this.ourEntity.getBbHeight() + 1.0F);
         this.nodeEvaluator.entityDepth = Mth.floor(this.ourEntity.getBbWidth() + 1.0F);
      }

      if (this.desiredPosTimeout > 0) {
         this.desiredPosTimeout--;
      }

      if (this.pathResult != null) {
         if (!this.pathResult.isFinished()) {
            return;
         }

         if (this.pathResult.getStatus() == PathFindingStatus.CALCULATION_COMPLETE) {
            this.processCompletedCalculationResult();
         }
      }

      int oldIndex = this.isDone() ? 0 : this.getPath().getNextNodeIndex();
      if (this.isSneaking) {
         this.isSneaking = false;
         this.mob.setShiftKeyDown(false);
      }

      this.ourEntity.setYya(0.0F);
      if (this.handleLadders(oldIndex)) {
         this.followThePath();
         this.stuckHandler.checkStuck(this);
      } else if (this.handleRails()) {
         this.stuckHandler.checkStuck(this);
      } else {
         this.tick++;
         if (!this.isDone()) {
            if (this.canUpdatePath()) {
               this.followThePath();
            } else if (this.path != null && !this.path.isDone()) {
               Vec3 vector3d = this.getTempMobPos();
               Vec3 vector3d1 = this.getEntityPosAtNode(this.path.getNextNodeIndex());
               if (vector3d.y > vector3d1.y
                  && !this.mob.onGround()
                  && Mth.floor(vector3d.x) == Mth.floor(vector3d1.x)
                  && Mth.floor(vector3d.z) == Mth.floor(vector3d1.z)) {
                  this.path.advance();
               }
            }

            DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.isDone()) {
               Vec3 vector3d2 = this.getEntityPosAtNode(this.path.getNextNodeIndex());
               BlockPos blockpos = BlockPos.containing(vector3d2);
               if (isEntityBlockLoaded(this.level, blockpos)) {
                  this.mob
                     .getMoveControl()
                     .setWantedPosition(
                        vector3d2.x,
                        this.level.getBlockState(blockpos.below()).isAir() ? vector3d2.y : getSmartGroundY(this.level, blockpos),
                        vector3d2.z,
                        this.speedModifier
                     );
               }
            }
         }

         if (this.hasDelayedRecomputation) {
            this.recomputePath();
         }

         if (this.pathResult != null && this.isDone()) {
            this.pathResult.setStatus(PathFindingStatus.COMPLETE);
            this.pathResult = null;
         }

         if (!(this.mob instanceof TamableAnimal tamableAnimal && tamableAnimal.isInSittingPose())) {
            if (!(this.mob instanceof IAdvancedPathingMob advancedPathingMob && advancedPathingMob.stopTickingPathing())) {
               this.stuckHandler.checkStuck(this);
            }
         }
      }
   }

   public static double getSmartGroundY(BlockGetter world, BlockPos pos) {
      BlockPos blockpos = pos.below();
      VoxelShape voxelshape = world.getBlockState(blockpos).getBlockSupportShape(world, blockpos);
      return !voxelshape.isEmpty() && !(voxelshape.max(Axis.Y) < 1.0) ? blockpos.getY() + voxelshape.max(Axis.Y) : pos.getY();
   }

   @Nullable
   @Override
   public PathResult moveToXYZ(double x, double y, double z, double speedFactor) {
      int newX = Mth.floor(x);
      int newY = (int)y;
      int newZ = Mth.floor(z);
      if (this.pathResult == null
         || !(this.pathResult.getJob() instanceof PathJobMoveToLocation)
         || !this.pathResult.isComputing()
            && (this.destination == null || !isEqual(this.destination, newX, newY, newZ))
            && (this.originalDestination == null || !isEqual(this.originalDestination, newX, newY, newZ))) {
         BlockPos start = AbstractPathJob.prepareStart(this.ourEntity);
         this.desiredPos = new BlockPos(newX, newY, newZ);
         return this.setPathJob(
            new PathJobMoveToLocation(
               this.ourEntity.level(), start, this.desiredPos, (int)this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(), this.ourEntity
            ),
            this.desiredPos,
            speedFactor,
            true
         );
      } else {
         return this.pathResult;
      }
   }

   @Override
   public boolean tryMoveToBlockPos(BlockPos pos, double speedFactor) {
      this.moveToXYZ(pos.getX(), pos.getY(), pos.getZ(), speedFactor);
      return true;
   }

   @NotNull
   protected PathFinder createPathFinder(int p_179679_1_) {
      return new PathFinder(new WalkNodeEvaluator(), p_179679_1_);
   }

   protected boolean canUpdatePath() {
      if (this.ourEntity.getVehicle() != null) {
         PathPointExtended pEx = (PathPointExtended)this.getPath().getNode(this.getPath().getNextNodeIndex());
         if (pEx.isRailsExit()) {
            Entity entity = this.ourEntity.getVehicle();
            this.ourEntity.stopRiding();
            entity.remove(RemovalReason.DISCARDED);
         } else if (!pEx.isOnRails()) {
            if (this.destination == null || this.mob.distanceToSqr(this.destination.getX(), this.destination.getY(), this.destination.getZ()) > 2.0) {
               this.ourEntity.stopRiding();
            }
         } else if ((Math.abs(pEx.x - this.mob.getX()) > 7.0 || Math.abs(pEx.z - this.mob.getZ()) > 7.0) && this.ourEntity.getVehicle() != null) {
            Entity entity = this.ourEntity.getVehicle();
            this.ourEntity.stopRiding();
            entity.remove(RemovalReason.DISCARDED);
         }
      }

      return true;
   }

   @NotNull
   protected Vec3 getTempMobPos() {
      return this.ourEntity.position();
   }

   public Path createPath(@NotNull BlockPos pos, int accuracy) {
      return null;
   }

   protected boolean canMoveDirectly(@NotNull Vec3 start, @NotNull Vec3 end) {
      return super.canMoveDirectly(start, end);
   }

   public double getSpeedFactor() {
      if (this.ourEntity.isInWater()) {
         this.speedModifier = this.walkSpeedFactor * this.swimSpeedFactor;
         return this.speedModifier;
      } else {
         this.speedModifier = this.walkSpeedFactor;
         return this.walkSpeedFactor;
      }
   }

   public void setSpeedModifier(double speedFactor) {
      if (!(speedFactor > 2.0) && !(speedFactor < 0.1)) {
         this.walkSpeedFactor = speedFactor;
      } else {
         Citadel.LOGGER.debug("Tried to set a bad speed:{} for entity:{}", speedFactor, this.ourEntity);
      }
   }

   public boolean moveTo(double x, double y, double z, double speedFactor) {
      if (x == 0.0 && y == 0.0 && z == 0.0) {
         return false;
      } else {
         this.moveToXYZ(x, y, z, speedFactor);
         return true;
      }
   }

   public boolean moveTo(Entity entityIn, double speedFactor) {
      return this.tryMoveToBlockPos(entityIn.blockPosition(), speedFactor);
   }

   protected void trimPath() {
   }

   public boolean moveTo(@Nullable Path path, double speedFactor) {
      if (path == null) {
         this.stop();
         return false;
      } else {
         this.pathStartTime = this.level.getGameTime();
         return super.moveTo(this.convertPath(path), speedFactor);
      }
   }

   private Path convertPath(Path path) {
      int pathLength = path.getNodeCount();
      Path tempPath = null;
      if (pathLength > 0 && !(path.getNode(0) instanceof PathPointExtended)) {
         PathPointExtended[] newPoints = new PathPointExtended[pathLength];

         for (int i = 0; i < pathLength; i++) {
            Node point = path.getNode(i);
            if (!(point instanceof PathPointExtended)) {
               newPoints[i] = new PathPointExtended(new BlockPos(point.x, point.y, point.z));
            } else {
               newPoints[i] = (PathPointExtended)point;
            }
         }

         tempPath = new Path(Arrays.asList(newPoints), path.getTarget(), path.canReach());
         PathPointExtended finalPoint = newPoints[pathLength - 1];
         this.destination = new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z);
      }

      return tempPath == null ? path : tempPath;
   }

   private boolean processCompletedCalculationResult() {
      this.pathResult.getJob().synchToClient(this.mob);
      this.moveTo(this.pathResult.getPath(), this.getSpeedFactor());
      if (this.pathResult != null) {
         this.pathResult.setStatus(PathFindingStatus.IN_PROGRESS_FOLLOWING);
      }

      return false;
   }

   private boolean handleLadders(int oldIndex) {
      if (!this.isDone()) {
         PathPointExtended pEx = (PathPointExtended)this.getPath().getNode(this.getPath().getNextNodeIndex());
         PathPointExtended pExNext = this.getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 1
            ? (PathPointExtended)this.getPath().getNode(this.getPath().getNextNodeIndex() + 1)
            : null;
         BlockPos pos = new BlockPos(pEx.x, pEx.y, pEx.z);
         if (pEx.isOnLadder()
            && pExNext != null
            && (pEx.y != pExNext.y || this.mob.getY() > pEx.y)
            && this.level.getBlockState(pos).isLadder(this.level, pos, this.ourEntity)) {
            return this.handlePathPointOnLadder(pEx);
         }

         if (this.ourEntity.isInWater()) {
            return this.handleEntityInWater(oldIndex, pEx);
         }

         if (this.level.random.nextInt(10) == 0) {
            if (!pEx.isOnLadder() && pExNext != null && pExNext.isOnLadder()) {
               this.speedModifier = this.getSpeedFactor() / 4.0;
            } else {
               this.speedModifier = this.getSpeedFactor();
            }
         }
      }

      return false;
   }

   private BlockPos findBlockUnderEntity(Entity parEntity) {
      int blockX = (int)Math.round(parEntity.getX());
      int blockY = Mth.floor(parEntity.getY() - 0.2);
      int blockZ = (int)Math.round(parEntity.getZ());
      return new BlockPos(blockX, blockY, blockZ);
   }

   private boolean handleRails() {
      if (!this.isDone()) {
         PathPointExtended pEx = (PathPointExtended)this.getPath().getNode(this.getPath().getNextNodeIndex());
         PathPointExtended pExNext = this.getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 1
            ? (PathPointExtended)this.getPath().getNode(this.getPath().getNextNodeIndex() + 1)
            : null;
         if (pExNext != null && pEx.x == pExNext.x && pEx.z == pExNext.z) {
            pExNext = this.getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 2
               ? (PathPointExtended)this.getPath().getNode(this.getPath().getNextNodeIndex() + 2)
               : null;
         }

         if (pEx.isOnRails() || pEx.isRailsExit()) {
            return this.handlePathOnRails(pEx, pExNext);
         }
      }

      return false;
   }

   private boolean handlePathOnRails(PathPointExtended pEx, PathPointExtended pExNext) {
      return false;
   }

   private boolean handlePathPointOnLadder(PathPointExtended pEx) {
      Vec3 vec3 = this.getEntityPosAtNode(this.path.getNextNodeIndex());
      BlockPos entityPos = new BlockPos(this.ourEntity.blockPosition());
      if (vec3.distanceToSqr(this.ourEntity.getX(), vec3.y, this.ourEntity.getZ()) < 0.6 && Math.abs(vec3.y - entityPos.getY()) <= 2.0) {
         double newSpeed = 0.3;
         switch (pEx.getLadderFacing()) {
            case NORTH:
               vec3 = vec3.add(0.0, 0.0, 0.4);
               break;
            case SOUTH:
               vec3 = vec3.add(0.0, 0.0, -0.4);
               break;
            case WEST:
               vec3 = vec3.add(0.4, 0.0, 0.0);
               break;
            case EAST:
               vec3 = vec3.add(-0.4, 0.0, 0.0);
               break;
            case UP:
               vec3 = vec3.add(0.0, 1.0, 0.0);
               break;
            default:
               newSpeed = 0.0;
               this.mob.setShiftKeyDown(true);
               this.isSneaking = true;
               this.ourEntity.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, 0.2);
         }

         if (!(newSpeed > 0.0)) {
            if (this.level.getBlockState(entityPos.below()).isLadder(this.level, entityPos.below(), this.ourEntity)) {
               this.ourEntity.setYya(-0.5F);
               return true;
            }

            return false;
         }

         if (!(this.level.getBlockState(this.ourEntity.blockPosition()).getBlock() instanceof LadderBlock)) {
            this.ourEntity.setDeltaMovement(this.ourEntity.getDeltaMovement().add(0.0, 0.1, 0.0));
         }

         this.ourEntity.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, newSpeed);
      }

      return false;
   }

   private boolean handleEntityInWater(int oldIndex, PathPointExtended pEx) {
      int curIndex = this.getPath().getNextNodeIndex();
      if (curIndex > 0 && curIndex + 1 < this.getPath().getNodeCount() && this.getPath().getNode(curIndex - 1).y != pEx.y) {
         oldIndex = curIndex + 1;
      }

      this.getPath().setNextNodeIndex(oldIndex);
      Vec3 vec3d = this.getEntityPosAtNode(this.path.getNextNodeIndex());
      if (vec3d.distanceToSqr(new Vec3(this.ourEntity.getX(), vec3d.y, this.ourEntity.getZ())) < 0.1 && Math.abs(this.ourEntity.getY() - vec3d.y) < 0.5) {
         this.getPath().advance();
         if (this.isDone()) {
            return true;
         }

         vec3d = this.getEntityPosAtNode(this.path.getNextNodeIndex());
      }

      this.ourEntity.getMoveControl().setWantedPosition(vec3d.x, vec3d.y, vec3d.z, this.getSpeedFactor());
      return false;
   }

   protected void followThePath() {
      this.getSpeedFactor();
      int curNode = this.path.getNextNodeIndex();
      int curNodeNext = curNode + 1;
      if (curNodeNext < this.path.getNodeCount()) {
         if (!(this.path.getNode(curNode) instanceof PathPointExtended)) {
            this.path = this.convertPath(this.path);
         }

         PathPointExtended pEx = (PathPointExtended)this.path.getNode(curNode);
         PathPointExtended pExNext = (PathPointExtended)this.path.getNode(curNodeNext);
         if (pEx.isOnLadder() && pEx.getLadderFacing() == Direction.DOWN && !pExNext.isOnLadder()) {
            Vec3 vec3 = this.getTempMobPos();
            if (vec3.y - pEx.y < 0.001) {
               this.path.setNextNodeIndex(curNodeNext);
            }

            return;
         }
      }

      this.maxDistanceToWaypoint = this.calculateMaxDistanceToWaypoint();
      boolean wentAhead = false;
      boolean isTracking = AbstractPathJob.trackingMap.containsValue(this.ourEntity.getUUID());
      int maxDropHeight = 3;
      HashSet<BlockPos> reached = new HashSet<>();

      for (int i = this.path.getNextNodeIndex(); i < Math.min(this.path.getNodeCount(), this.path.getNextNodeIndex() + 4); i++) {
         Vec3 next = this.getEntityPosAtNode(i);
         if (Math.abs(this.mob.getX() - next.x) < this.maxDistanceToWaypoint - Math.abs(this.mob.getY() - next.y) * 0.1
            && Math.abs(this.mob.getZ() - next.z) < this.maxDistanceToWaypoint - Math.abs(this.mob.getY() - next.y) * 0.1
            && (
               Math.abs(this.mob.getY() - next.y) <= Math.min(1.0, Math.ceil(this.mob.getBbHeight() / 2.0F))
                  || Math.abs(this.mob.getY() - next.y) <= Math.ceil(this.mob.getBbWidth() / 2.0F) * maxDropHeight
            )) {
            this.path.advance();
            wentAhead = true;
            if (isTracking) {
               Node point = this.path.getNode(i);
               reached.add(new BlockPos(point.x, point.y, point.z));
            }
         }
      }

      if (isTracking) {
         AbstractPathJob.synchToClient(reached, this.ourEntity);
         reached.clear();
      }

      if (this.path.isDone()) {
         this.onPathFinish();
      } else if (!wentAhead) {
         if (curNode < this.path.getNodeCount() && curNode > 1) {
            Vec3 curr = this.getEntityPosAtNode(curNode - 1);
            Vec3 next = this.getEntityPosAtNode(curNode);
            Vec3i currI = new Vec3i((int)Math.round(curr.x), (int)Math.round(curr.y), (int)Math.round(curr.z));
            Vec3i nextI = new Vec3i((int)Math.round(next.x), (int)Math.round(next.y), (int)Math.round(next.z));
            if (this.mob.blockPosition().closerThan(currI, 2.0) && this.mob.blockPosition().closerThan(nextI, 2.0)) {
               for (int currentIndex = curNode - 1; currentIndex > 0; currentIndex--) {
                  Vec3 tempoPos = this.getEntityPosAtNode(currentIndex);
                  Vec3i tempoPosI = new Vec3i((int)Math.round(tempoPos.x), (int)Math.round(tempoPos.y), (int)Math.round(tempoPos.z));
                  if (this.mob.blockPosition().closerThan(tempoPosI, 1.0)) {
                     this.path.setNextNodeIndex(currentIndex);
                  } else if (isTracking) {
                     reached.add(new BlockPos(tempoPosI));
                  }
               }
            }

            if (isTracking) {
               AbstractPathJob.synchToClient(reached, this.ourEntity);
               reached.clear();
            }
         }
      }
   }

   protected float calculateMaxDistanceToWaypoint() {
      return this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
   }

   public Vec3 getEntityPosAtNode(int index) {
      if (this.path == null) {
         return this.getTempMobPos();
      } else {
         Node node = this.path.getNode(index);
         double d0 = node.x + 0.5;
         double d1 = node.y;
         double d2 = node.z + 0.5;
         return new Vec3(d0, d1, d2);
      }
   }

   private void onPathFinish() {
      this.stop();
   }

   public void recomputePath() {
   }

   protected void doStuckDetection(@NotNull Vec3 positionVec3) {
   }

   public boolean entityOnAndBelowPath(Entity entity, Vec3 slack) {
      Path path = this.getPath();
      if (path == null) {
         return false;
      } else {
         int closest = path.getNextNodeIndex();

         for (int i = 0; i < path.getNodeCount() - 1; i++) {
            if (closest + i < path.getNodeCount()) {
               Node currentPoint = path.getNode(closest + i);
               if (this.entityNearAndBelowPoint(currentPoint, entity, slack)) {
                  return true;
               }
            }

            if (closest - i >= 0) {
               Node currentPoint = path.getNode(closest - i);
               if (this.entityNearAndBelowPoint(currentPoint, entity, slack)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean entityNearAndBelowPoint(Node currentPoint, Entity entity, Vec3 slack) {
      return Math.abs(currentPoint.x - entity.getX()) < slack.x()
         && currentPoint.y - entity.getY() + slack.y() > 0.0
         && Math.abs(currentPoint.z - entity.getZ()) < slack.z();
   }

   public void stop() {
      if (this.pathResult != null) {
         this.pathResult.cancel();
         this.pathResult.setStatus(PathFindingStatus.CANCELLED);
         this.pathResult = null;
      }

      this.destination = null;
      super.stop();
   }

   @Nullable
   @Override
   public PathResult moveToLivingEntity(Entity e, double speed) {
      return this.moveToXYZ(e.getX(), e.getY(), e.getZ(), speed);
   }

   @Nullable
   @Override
   public PathResult moveAwayFromLivingEntity(Entity e, double distance, double speed) {
      return this.moveAwayFromXYZ(new BlockPos(e.blockPosition()), distance, speed, true);
   }

   public void setCanFloat(boolean canSwim) {
      super.setCanFloat(canSwim);
      this.getPathingOptions().setCanSwim(canSwim);
   }

   @Override
   public BlockPos getDesiredPos() {
      return this.desiredPos;
   }

   @Override
   public void setStuckHandler(IStuckHandler stuckHandler) {
      this.stuckHandler = stuckHandler;
   }

   @Override
   public void setSwimSpeedFactor(double factor) {
      this.swimSpeedFactor = factor;
   }

   public static boolean isEqual(BlockPos coords, int x, int y, int z) {
      return coords.getX() == x && coords.getY() == y && coords.getZ() == z;
   }

   public static boolean isEntityBlockLoaded(LevelAccessor world, BlockPos pos) {
      return WorldChunkUtil.isEntityBlockLoaded(world, pos);
   }

   public static enum MovementType {
      WALKING,
      FLYING,
      CLIMBING;
   }
}

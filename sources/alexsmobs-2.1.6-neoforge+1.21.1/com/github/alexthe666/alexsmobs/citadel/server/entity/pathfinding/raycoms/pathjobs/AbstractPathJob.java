package com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.pathjobs;

import com.github.alexthe666.alexsmobs.citadel.Citadel;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.AbstractAdvancedPathNavigate;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.ChunkCache;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.IPassabilityNavigator;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.ITallWalker;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.MNode;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.PathPointExtended;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.PathResult;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.Pathfinding;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.PathfindingConstants;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.PathingOptions;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.SurfaceType;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.datafixers.util.Pair;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractPathJob implements Callable<Path> {
   public static final Map<Player, UUID> trackingMap = new HashMap<>();
   protected final BlockPos start;
   protected final LevelReader world;
   protected final PathResult result;
   private final Queue<MNode> nodesOpen = new PriorityQueue<>(500);
   private final Map<Integer, MNode> nodesVisited = new HashMap<>();
   private final AbstractAdvancedPathNavigate.RestrictionType restrictionType;
   private final boolean hardXzRestriction;
   private final boolean xzRestricted = false;
   protected int maxRange;
   protected BlockPos end = null;
   protected boolean debugDrawEnabled = false;
   @Nullable
   protected Set<MNode> debugNodesVisited = new HashSet<>();
   @Nullable
   protected Set<MNode> debugNodesNotVisited = new HashSet<>();
   @Nullable
   protected Set<MNode> debugNodesPath = new HashSet<>();
   protected WeakReference<LivingEntity> entity;
   IPassabilityNavigator passabilityNavigator;
   private boolean allowJumpPointSearchTypeWalk;
   private int entitySizeXZStart = 0;
   private int entitySizeXZEnd = 1;
   private int entitySizeY = 1;
   private int totalNodesAdded = 0;
   private int totalNodesVisited = 0;
   private PathingOptions pathingOptions = new PathingOptions();
   private int maxX;
   private int minX;
   private int maxZ;
   private int minZ;
   private int maxY;
   private int minY;
   private double maxJumpHeight = 1.3;
   private int maxNavigableGroundDist = 1;

   public AbstractPathJob(Level world, BlockPos start, BlockPos end, int range, LivingEntity entity) {
      this(world, start, end, range, new PathResult(), entity);
   }

   public AbstractPathJob(Level world, BlockPos start, BlockPos end, int range, PathResult result, LivingEntity entity) {
      int minX = Math.min(start.getX(), end.getX()) - range / 2;
      int minZ = Math.min(start.getZ(), end.getZ()) - range / 2;
      int maxX = Math.max(start.getX(), end.getX()) + range / 2;
      int maxZ = Math.max(start.getZ(), end.getZ()) + range / 2;
      this.restrictionType = AbstractAdvancedPathNavigate.RestrictionType.NONE;
      this.hardXzRestriction = false;
      this.world = new ChunkCache(
         world,
         new BlockPos(minX, AMCompat.minBuildHeight(world), minZ),
         new BlockPos(maxX, AMCompat.maxBuildHeight(world), maxZ),
         range,
         world.dimensionType()
      );
      this.start = new BlockPos(start);
      this.end = end;
      this.maxRange = range;
      this.result = result;
      result.setJob(this);
      this.allowJumpPointSearchTypeWalk = false;
      if (entity != null && trackingMap.containsValue(entity.getUUID())) {
         this.debugDrawEnabled = true;
         this.debugNodesVisited = new HashSet<>();
         this.debugNodesNotVisited = new HashSet<>();
         this.debugNodesPath = new HashSet<>();
      }

      this.setEntitySizes(entity);
      if (entity instanceof IPassabilityNavigator) {
         this.passabilityNavigator = (IPassabilityNavigator)entity;
         this.maxRange = this.passabilityNavigator.maxSearchNodes();
      }

      if (entity instanceof ITallWalker tallWalker) {
         this.maxNavigableGroundDist = tallWalker.getMaxNavigableDistanceToGround();
      }

      this.maxJumpHeight = (float)Math.floor(entity.maxUpStep() - 0.2F) + 1.3F;
      this.entity = new WeakReference<>(entity);
   }

   public AbstractPathJob(
      Level world,
      BlockPos start,
      BlockPos startRestriction,
      BlockPos endRestriction,
      int range,
      boolean hardRestriction,
      PathResult result,
      LivingEntity entity,
      AbstractAdvancedPathNavigate.RestrictionType restrictionType
   ) {
      this(world, start, startRestriction, endRestriction, range, Vec3i.ZERO, hardRestriction, result, entity, restrictionType);
      this.setEntitySizes(entity);
      if (entity instanceof IPassabilityNavigator) {
         this.passabilityNavigator = (IPassabilityNavigator)entity;
         this.maxRange = this.passabilityNavigator.maxSearchNodes();
      }

      this.maxJumpHeight = (float)Math.floor(entity.maxUpStep() - 0.2F) + 1.3F;
   }

   public AbstractPathJob(
      Level world,
      BlockPos start,
      BlockPos startRestriction,
      BlockPos endRestriction,
      int range,
      Vec3i grow,
      boolean hardRestriction,
      PathResult result,
      LivingEntity entity,
      AbstractAdvancedPathNavigate.RestrictionType restrictionType
   ) {
      this.minX = Math.min(startRestriction.getX(), endRestriction.getX()) - grow.getX();
      this.minZ = Math.min(startRestriction.getZ(), endRestriction.getZ()) - grow.getZ();
      this.maxX = Math.max(startRestriction.getX(), endRestriction.getX()) + grow.getX();
      this.maxZ = Math.max(startRestriction.getZ(), endRestriction.getZ()) + grow.getZ();
      this.minY = Math.min(startRestriction.getY(), endRestriction.getY()) - grow.getY();
      this.maxY = Math.max(startRestriction.getY(), endRestriction.getY()) + grow.getY();
      this.restrictionType = restrictionType;
      this.hardXzRestriction = hardRestriction;
      this.world = new ChunkCache(
         world,
         new BlockPos(this.minX, AMCompat.minBuildHeight(world), this.minZ),
         new BlockPos(this.maxX, AMCompat.maxBuildHeight(world), this.maxZ),
         range,
         world.dimensionType()
      );
      this.start = start;
      this.maxRange = range;
      this.result = result;
      result.setJob(this);
      this.allowJumpPointSearchTypeWalk = false;
      if (entity != null && trackingMap.containsValue(entity.getUUID())) {
         this.debugDrawEnabled = true;
         this.debugNodesVisited = new HashSet<>();
         this.debugNodesNotVisited = new HashSet<>();
         this.debugNodesPath = new HashSet<>();
      }

      this.entity = new WeakReference<>(entity);
   }

   public static void synchToClient(HashSet<BlockPos> reached, Mob mob) {
   }

   public static BlockPos prepareStart(LivingEntity entity) {
      MutableBlockPos pos = new MutableBlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
      Level world = entity.level();
      BlockState bs = world.getBlockState(pos);
      VoxelShape collisionShape = bs.getBlockSupportShape(world, pos);
      if (bs.blocksMotion() && collisionShape.max(Axis.X) > 0.0) {
         double relPosX = Math.abs(entity.getX() % 1.0);
         double relPosZ = Math.abs(entity.getZ() % 1.0);

         for (AABB box : collisionShape.toAabbs()) {
            if (relPosX >= box.minX && relPosX <= box.maxX && relPosZ >= box.minZ && relPosZ <= box.maxZ && box.maxY > 0.0) {
               pos.set(pos.getX(), pos.getY() + 1, pos.getZ());
               bs = world.getBlockState(pos);
               break;
            }
         }
      }

      BlockState down = world.getBlockState(pos.below());

      while (!bs.blocksMotion() && !down.blocksMotion() && !AMCompat.isLadder(down, world, pos.below(), entity) && bs.getFluidState().isEmpty()) {
         pos.move(Direction.DOWN, 1);
         bs = down;
         down = world.getBlockState(pos.below());
         if (pos.getY() < AMCompat.minBuildHeight(world)) {
            return entity.blockPosition();
         }
      }

      Block b = bs.getBlock();
      if (entity.isInWater()) {
         while (!bs.getFluidState().isEmpty()) {
            pos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            bs = world.getBlockState(pos);
         }
      } else if (b instanceof FenceBlock || b instanceof WallBlock || bs.isSolid()) {
         double dX = entity.getX() - Math.floor(entity.getX());
         double dZ = entity.getZ() - Math.floor(entity.getZ());
         if (dX < 0.25) {
            pos.set(pos.getX() - 1, pos.getY(), pos.getZ());
         } else if (dX > 0.75) {
            pos.set(pos.getX() + 1, pos.getY(), pos.getZ());
         }

         if (dZ < 0.25) {
            pos.set(pos.getX(), pos.getY(), pos.getZ() - 1);
         } else if (dZ > 0.75) {
            pos.set(pos.getX(), pos.getY(), pos.getZ() + 1);
         }
      }

      return pos.immutable();
   }

   private static void setLadderFacing(LevelReader world, BlockPos pos, PathPointExtended p) {
      BlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      if (block instanceof VineBlock) {
         if ((Boolean)state.getValue(VineBlock.SOUTH)) {
            p.setLadderFacing(Direction.NORTH);
         } else if ((Boolean)state.getValue(VineBlock.WEST)) {
            p.setLadderFacing(Direction.EAST);
         } else if ((Boolean)state.getValue(VineBlock.NORTH)) {
            p.setLadderFacing(Direction.SOUTH);
         } else if ((Boolean)state.getValue(VineBlock.EAST)) {
            p.setLadderFacing(Direction.WEST);
         }
      } else if (block instanceof LadderBlock) {
         p.setLadderFacing((Direction)state.getValue(LadderBlock.FACING));
      } else {
         p.setLadderFacing(Direction.UP);
      }
   }

   private static boolean onALadder(MNode node, @Nullable MNode nextInPath, BlockPos pos) {
      return nextInPath != null && node.isLadder() && nextInPath.pos.getX() == pos.getX() && nextInPath.pos.getZ() == pos.getZ();
   }

   private static int computeNodeKey(BlockPos pos) {
      return (pos.getX() & 4095) << 20 | (pos.getY() & 0xFF) << 12 | pos.getZ() & 4095;
   }

   private static boolean nodeClosed(@Nullable MNode node) {
      return node != null && node.isClosed();
   }

   private static boolean calculateSwimming(LevelReader world, BlockPos pos, @Nullable MNode node) {
      return node == null ? SurfaceType.isWater(world, pos.below()) : node.isSwimming();
   }

   public static Direction getXZFacing(BlockPos pos, BlockPos neighbor) {
      BlockPos vector = neighbor.subtract(pos);
      return Direction.getNearest(vector.getX(), 0.0F, vector.getZ());
   }

   public void synchToClient(LivingEntity mob) {
   }

   protected boolean onLadderGoingUp(MNode currentNode, BlockPos dPos) {
      return currentNode.isLadder() && (dPos.getY() >= 0 || dPos.getX() != 0 || dPos.getZ() != 0);
   }

   public void setEntitySizes(LivingEntity entity) {
      if (entity instanceof ICustomSizeNavigator) {
         this.entitySizeXZStart = -((int)((ICustomSizeNavigator)entity).getXZNavSize());
         this.entitySizeXZEnd = (int)((ICustomSizeNavigator)entity).getXZNavSize();
         this.entitySizeY = ((ICustomSizeNavigator)entity).getYNavSize();
      } else {
         float bbWidth = entity.getBbWidth();
         if (bbWidth <= 1.0F) {
            this.entitySizeXZStart = 0;
            this.entitySizeXZEnd = 1;
         } else {
            this.entitySizeXZStart = -((int)Math.floor(entity.getBbWidth() / 2.0F));
            this.entitySizeXZEnd = (int)Math.floor(entity.getBbWidth() / 2.0F);
         }

         this.entitySizeY = Mth.ceil(entity.getBbHeight());
      }

      this.allowJumpPointSearchTypeWalk = false;
   }

   protected double computeCost(
      BlockPos dPos,
      boolean isSwimming,
      boolean onPath,
      boolean onRails,
      boolean railsExit,
      boolean swimStart,
      boolean corner,
      BlockState state,
      BlockPos blockPos
   ) {
      double cost = Math.sqrt(dPos.getX() * dPos.getX() + dPos.getY() * dPos.getY() + dPos.getZ() * dPos.getZ());
      if (dPos.getY() != 0 && (Math.abs(dPos.getY()) > 1 || !(this.world.getBlockState(blockPos).getBlock() instanceof StairBlock))) {
         if (dPos.getY() > 0) {
            cost *= this.pathingOptions.jumpCost * Math.abs(dPos.getY());
         } else {
            cost *= this.pathingOptions.dropCost * Math.abs(dPos.getY());
         }
      }

      if (this.world.getBlockState(blockPos).hasProperty(BlockStateProperties.OPEN)) {
         cost *= this.pathingOptions.traverseToggleAbleCost;
      }

      if (onPath) {
         cost *= this.pathingOptions.onPathCost;
      }

      if (onRails) {
         cost *= this.pathingOptions.onRailCost;
      }

      if (railsExit) {
         cost *= this.pathingOptions.railsExitCost;
      }

      if (state.getBlock() instanceof VineBlock) {
         cost *= this.pathingOptions.vineCost;
      }

      if (isSwimming) {
         if (swimStart) {
            cost *= this.pathingOptions.swimCostEnter;
         } else {
            cost *= this.pathingOptions.swimCost;
         }
      }

      return cost;
   }

   public PathResult getResult() {
      return this.result;
   }

   public final Path call() {
      try {
         return this.search();
      } catch (Exception var2) {
         Citadel.LOGGER.warn("Pathfinding Exception", var2);
         return null;
      }
   }

   protected Path search() {
      MNode bestNode = this.getAndSetupStartNode();
      double bestNodeResultScore = 1.7976931348623157E308;

      while (!this.nodesOpen.isEmpty()) {
         if (Thread.currentThread().isInterrupted()) {
            return null;
         }

         MNode currentNode = this.nodesOpen.poll();
         this.totalNodesVisited++;
         if (this.totalNodesVisited > PathfindingConstants.maxPathingNodes || this.totalNodesVisited > this.maxRange * this.maxRange) {
            break;
         }

         currentNode.setCounterVisited(this.totalNodesVisited);
         this.handleDebugOptions(currentNode);
         currentNode.setClosed();
         boolean isViablePosition = this.isInRestrictedArea(currentNode.pos)
            && SurfaceType.getSurfaceType(this.world, this.world.getBlockState(currentNode.pos.below()), currentNode.pos.below()) == SurfaceType.WALKABLE;
         if (isViablePosition && this.isAtDestination(currentNode)) {
            bestNode = currentNode;
            this.result.setPathReachesDestination(true);
            break;
         }

         double nodeResultScore = this.getNodeResultScore(currentNode);
         if (isViablePosition && nodeResultScore < bestNodeResultScore && !currentNode.isCornerNode()) {
            bestNode = currentNode;
            bestNodeResultScore = nodeResultScore;
         }

         if (!this.hardXzRestriction || isViablePosition) {
            this.walkCurrentNode(currentNode);
         }
      }

      return this.finalizePath(bestNode);
   }

   private void handleDebugOptions(MNode currentNode) {
      if (this.debugDrawEnabled) {
         this.addNodeToDebug(currentNode);
      }

      if (Pathfinding.isDebug()) {
         Citadel.LOGGER
            .info(
               String.format(
                  "Examining MNode [%d,%d,%d] ; g=%f ; f=%f",
                  currentNode.pos.getX(),
                  currentNode.pos.getY(),
                  currentNode.pos.getZ(),
                  currentNode.getCost(),
                  currentNode.getScore()
               )
            );
      }
   }

   private void addNodeToDebug(MNode currentNode) {
      this.debugNodesNotVisited.remove(currentNode);
      this.debugNodesVisited.add(currentNode);
   }

   private void addPathNodeToDebug(MNode node) {
      this.debugNodesVisited.remove(node);
      this.debugNodesPath.add(node);
   }

   private void walkCurrentNode(MNode currentNode) {
      BlockPos dPos = PathfindingConstants.BLOCKPOS_IDENTITY;
      if (currentNode.parent != null) {
         dPos = currentNode.pos.subtract(currentNode.parent.pos);
      }

      if (this.onLadderGoingUp(currentNode, dPos)) {
         this.walk(currentNode, PathfindingConstants.BLOCKPOS_UP);
      }

      if (this.onLadderGoingDown(currentNode, dPos)) {
         this.walk(currentNode, PathfindingConstants.BLOCKPOS_DOWN);
      }

      if (this.pathingOptions.canClimb()) {
         if ((Integer)this.getHighest(currentNode).getFirst() > 1) {
            this.walk(currentNode, PathfindingConstants.BLOCKPOS_IDENTITY.above((Integer)this.getHighest(currentNode).getFirst()));
         }

         if (currentNode.parent != null && dPos.getX() == 0 && dPos.getZ() == 0 && dPos.getY() > 1 && this.getHighest(currentNode.parent).getSecond() != null) {
            this.walk(currentNode, (BlockPos)this.getHighest(currentNode.parent).getSecond());
         }
      }

      if ((currentNode.parent == null || !currentNode.parent.pos.equals(currentNode.pos.below())) && currentNode.isCornerNode()) {
         this.walk(currentNode, PathfindingConstants.BLOCKPOS_DOWN);
      } else {
         if (this.isPassable(currentNode.pos.below(), currentNode.parent)
            && !currentNode.isSwimming()
            && this.isLiquid(this.world.getBlockState(currentNode.pos.below()))) {
            this.walk(currentNode, PathfindingConstants.BLOCKPOS_DOWN);
         }

         if (dPos.getZ() <= 0) {
            this.walk(currentNode, PathfindingConstants.BLOCKPOS_NORTH);
         }

         if (dPos.getX() >= 0) {
            this.walk(currentNode, PathfindingConstants.BLOCKPOS_EAST);
         }

         if (dPos.getZ() >= 0) {
            this.walk(currentNode, PathfindingConstants.BLOCKPOS_SOUTH);
         }

         if (dPos.getX() <= 0) {
            this.walk(currentNode, PathfindingConstants.BLOCKPOS_WEST);
         }
      }
   }

   private boolean onLadderGoingDown(MNode currentNode, BlockPos dPos) {
      return (dPos.getY() <= 0 || dPos.getX() != 0 || dPos.getZ() != 0) && this.isLadder(currentNode.pos.below());
   }

   private MNode getAndSetupStartNode() {
      MNode startNode = new MNode(this.start, this.computeHeuristic(this.start));
      if (this.pathingOptions.isFlying() && this.start.closerThan(this.end, this.maxRange)) {
         startNode = new MNode(this.end, this.computeHeuristic(this.end));
      }

      if (this.isLadder(this.start)) {
         startNode.setLadder();
      } else if (this.isLiquid(this.world.getBlockState(this.start.below()))) {
         startNode.setSwimming();
      }

      startNode.setOnRails(this.pathingOptions.canUseRails() && this.world.getBlockState(this.start).getBlock() instanceof BaseRailBlock);
      this.nodesOpen.offer(startNode);
      this.nodesVisited.put(computeNodeKey(this.start), startNode);
      this.totalNodesAdded++;
      return startNode;
   }

   public boolean isLiquid(BlockState state) {
      return state.liquid() || !state.blocksMotion() && !state.getFluidState().isEmpty();
   }

   private Path finalizePath(MNode targetNode) {
      int pathLength = 1;
      int railsLength = 0;

      MNode node;
      for (node = targetNode; node.parent != null; node = node.parent) {
         pathLength++;
         if (node.isOnRails()) {
            railsLength++;
         }
      }

      Node[] points = new Node[pathLength];
      points[0] = new PathPointExtended(node.pos);
      if (this.debugDrawEnabled) {
         this.addPathNodeToDebug(node);
      }

      MNode nextInPath = null;
      Node next = null;

      for (MNode var11 = targetNode; var11.parent != null; var11 = var11.parent) {
         if (this.debugDrawEnabled) {
            this.addPathNodeToDebug(var11);
         }

         pathLength--;
         BlockPos pos = var11.pos;
         if (var11.isSwimming()) {
            pos.offset(PathfindingConstants.BLOCKPOS_DOWN);
         }

         PathPointExtended p = new PathPointExtended(pos);
         if (railsLength >= 8) {
            p.setOnRails(var11.isOnRails());
            if (!p.isOnRails() || var11.parent.isOnRails() && var11.parent.parent != null) {
               if (p.isOnRails() && points.length > pathLength + 1) {
                  PathPointExtended point = (PathPointExtended)points[pathLength + 1];
                  if (!point.isOnRails()) {
                     point.setRailsExit();
                  }
               }
            } else {
               p.setRailsEntry();
            }
         }

         if (onALadder(var11, nextInPath, pos)) {
            p.setOnLadder(true);
            if (nextInPath.pos.getY() > pos.getY()) {
               setLadderFacing(this.world, pos, p);
            }
         } else if (onALadder(var11.parent, var11.parent, pos)) {
            p.setOnLadder(true);
         }

         if (next != null) {
            next.cameFrom = p;
         }

         next = p;
         points[pathLength] = p;
         nextInPath = var11;
      }

      this.doDebugPrinting(points);
      return new Path(Arrays.asList(points), this.getPathTargetPos(targetNode), this.isAtDestination(targetNode));
   }

   protected BlockPos getPathTargetPos(MNode finalNode) {
      return finalNode.pos;
   }

   private void doDebugPrinting(Node[] points) {
      if (Pathfinding.isDebug()) {
         Citadel.LOGGER.info("Path found:");

         for (Node p : points) {
            Citadel.LOGGER.info(String.format("Step: [%d,%d,%d]", p.x, p.y, p.z));
         }

         Citadel.LOGGER.info(String.format("Total Nodes Visited %d / %d", this.totalNodesVisited, this.totalNodesAdded));
      }
   }

   protected abstract double computeHeuristic(BlockPos var1);

   protected abstract boolean isAtDestination(MNode var1);

   protected abstract double getNodeResultScore(MNode var1);

   protected final boolean walk(MNode parent, BlockPos dPos) {
      BlockPos pos = parent.pos.offset(dPos);
      int newY = this.getGroundHeight(parent, pos);
      if (newY < AMCompat.minBuildHeight(this.world)) {
         return false;
      } else {
         boolean corner = false;
         if (pos.getY() != newY) {
            if (parent.isCornerNode()
               || newY - pos.getY() <= 0
               || parent.parent != null && parent.parent.pos.equals(parent.pos.offset(new BlockPos(0, newY - pos.getY(), 0)))) {
               if (parent.isCornerNode()
                  || newY - pos.getY() >= 0
                  || dPos.getX() == 0 && dPos.getZ() == 0
                  || parent.parent != null && parent.pos.below().equals(parent.parent.pos)) {
                  if (!this.pathingOptions.canClimb() || dPos.getY() <= 1) {
                     dPos = dPos.offset(0, newY - pos.getY(), 0);
                     pos = new BlockPos(pos.getX(), newY, pos.getZ());
                  }
               } else {
                  dPos = new BlockPos(dPos.getX(), 0, dPos.getZ());
                  pos = parent.pos.offset(dPos);
                  corner = true;
               }
            } else {
               dPos = new BlockPos(0, newY - pos.getY(), 0);
               pos = parent.pos.offset(dPos);
               corner = true;
            }
         }

         int nodeKey = computeNodeKey(pos);
         MNode node = this.nodesVisited.get(nodeKey);
         if (nodeClosed(node)) {
            return false;
         } else {
            boolean isSwimming = calculateSwimming(this.world, pos, node);
            if (isSwimming && !this.pathingOptions.canSwim()) {
               return false;
            } else {
               boolean swimStart = isSwimming && !parent.isSwimming();
               BlockState state = this.world.getBlockState(pos);
               boolean onRoad = false;
               boolean onRails = this.pathingOptions.canUseRails() && this.world.getBlockState(corner ? pos.below() : pos).getBlock() instanceof BaseRailBlock;
               boolean railsExit = !onRails && parent != null && parent.isOnRails();
               double stepCost = this.computeCost(dPos, isSwimming, false, onRails, railsExit, swimStart, corner, state, pos);
               double heuristic = this.computeHeuristic(pos);
               double cost = parent.getCost() + stepCost;
               double score = cost + heuristic;
               if (node == null) {
                  node = this.createNode(parent, pos, nodeKey, isSwimming, heuristic, cost, score);
                  node.setOnRails(onRails);
                  node.setCornerNode(corner);
               } else if (this.updateCurrentNode(parent, node, heuristic, cost, score)) {
                  return false;
               }

               this.nodesOpen.offer(node);
               if (this.pathingOptions.canClimb() && dPos.getY() > 1) {
                  return true;
               } else {
                  this.performJumpPointSearch(parent, dPos, node);
                  return true;
               }
            }
         }
      }
   }

   private void performJumpPointSearch(MNode parent, BlockPos dPos, MNode node) {
      if (this.allowJumpPointSearchTypeWalk && node.getHeuristic() <= parent.getHeuristic()) {
         this.walk(node, dPos);
      }
   }

   private MNode createNode(MNode parent, BlockPos pos, int nodeKey, boolean isSwimming, double heuristic, double cost, double score) {
      MNode node = new MNode(parent, pos, cost, heuristic, score);
      this.nodesVisited.put(nodeKey, node);
      if (this.debugDrawEnabled) {
         this.debugNodesNotVisited.add(node);
      }

      if (this.isLadder(pos)) {
         node.setLadder();
      } else if (isSwimming) {
         node.setSwimming();
      }

      this.totalNodesAdded++;
      node.setCounterAdded(this.totalNodesAdded);
      return node;
   }

   private boolean updateCurrentNode(MNode parent, MNode node, double heuristic, double cost, double score) {
      if (score >= node.getScore()) {
         return true;
      } else if (!this.nodesOpen.remove(node)) {
         return true;
      } else {
         node.parent = parent;
         node.setSteps(parent.getSteps() + 1);
         node.setCost(cost);
         node.setHeuristic(heuristic);
         node.setScore(score);
         return false;
      }
   }

   protected int getGroundHeight(MNode parent, BlockPos pos) {
      if (this.checkHeadBlock(parent, pos)) {
         return this.handleTargetNotPassable(parent, pos.above(), this.world.getBlockState(pos.above()));
      } else {
         BlockState target = this.world.getBlockState(pos);
         if (parent != null && !this.isPassableBB(parent.pos, pos, parent)) {
            return this.handleTargetNotPassable(parent, pos, target);
         } else {
            int i = 0;
            BlockState below = null;
            SurfaceType lastSurfaceType = null;

            while (i < this.maxNavigableGroundDist) {
               below = this.world.getBlockState(pos.below(++i));
               if (this.pathingOptions.isFlying()) {
                  lastSurfaceType = this.isFlyable(below, pos, parent);
                  if (lastSurfaceType == SurfaceType.FLYABLE) {
                     return pos.getY();
                  }
               } else {
                  lastSurfaceType = this.isWalkableSurface(below, pos);
                  if (lastSurfaceType == SurfaceType.WALKABLE) {
                     return pos.getY();
                  }
               }
            }

            return lastSurfaceType != SurfaceType.NOT_PASSABLE && below != null ? this.handleNotStanding(parent, pos, below) : -1;
         }
      }
   }

   private int handleNotStanding(@Nullable MNode parent, BlockPos pos, BlockState below) {
      boolean isSwimming = parent != null && parent.isSwimming();
      if (this.isLiquid(below)) {
         return this.handleInLiquid(pos, below, isSwimming);
      } else {
         return this.isLadder(below.getBlock(), pos.below()) ? pos.getY() : this.checkDrop(parent, pos, isSwimming);
      }
   }

   private int checkDrop(@Nullable MNode parent, BlockPos pos, boolean isSwimming) {
      boolean canDrop = parent != null && !parent.isLadder();
      boolean isChonker = true;
      if (this.pathingOptions.canClimb() && parent != null && pos.getY() > parent.pos.getY() + 1) {
         return pos.getY();
      } else if (isChonker
         || canDrop
            && !isSwimming
            && (
               parent.pos.getX() == pos.getX() && parent.pos.getZ() == pos.getZ()
                  || !this.isPassableBBFull(parent.pos.below(), parent)
                  || this.isWalkableSurface(this.world.getBlockState(parent.pos.below()), parent.pos.below()) != SurfaceType.DROPABLE
            )) {
         for (int i = 2; i <= 10; i++) {
            BlockState below = this.world.getBlockState(pos.below(i));
            if (this.isWalkableSurface(below, pos) == SurfaceType.WALKABLE && i <= 4 || below.liquid()) {
               return pos.getY() - i + 1;
            }

            if (below.isAir()) {
               return -1;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int handleInLiquid(BlockPos pos, BlockState below, boolean isSwimming) {
      if (isSwimming) {
         return pos.getY();
      } else {
         return this.pathingOptions.canSwim() && SurfaceType.isWater(this.world, pos.below()) ? pos.getY() : -1;
      }
   }

   private int handleTargetNotPassable(@Nullable MNode parent, BlockPos pos, BlockState target) {
      boolean canJump = parent != null && !parent.isLadder() && !parent.isSwimming();
      if (canJump && SurfaceType.getSurfaceType(this.world, target, pos) == SurfaceType.WALKABLE) {
         if (!this.isPassable(pos.above(2), parent)) {
            VoxelShape bb1 = this.world.getBlockState(pos).getBlockSupportShape(this.world, pos);
            VoxelShape bb2 = this.world.getBlockState(pos.above(2)).getBlockSupportShape(this.world, pos.above(2));
            if (pos.above(2).getY() + this.getStartY(bb2, 1) - (pos.getY() + this.getEndY(bb1, 0)) < 2.0) {
               return -1;
            }
         }

         if (!this.isPassable(parent.pos.above(2), parent)) {
            VoxelShape bb1 = this.world.getBlockState(pos).getBlockSupportShape(this.world, pos);
            VoxelShape bb2 = this.world.getBlockState(parent.pos.above(2)).getBlockSupportShape(this.world, parent.pos.above(2));
            if (parent.pos.above(2).getY() + this.getStartY(bb2, 1) - (pos.getY() + this.getEndY(bb1, 0)) < 2.0) {
               return -1;
            }
         }

         BlockState parentBelow = this.world.getBlockState(parent.pos.below());
         VoxelShape parentBB = parentBelow.getBlockSupportShape(this.world, parent.pos.below());
         double parentY = parentBB.max(Axis.Y);
         double parentMaxY = parentY + parent.pos.below().getY();
         double targetMaxY = target.getBlockSupportShape(this.world, pos).max(Axis.Y) + pos.getY();
         if (targetMaxY - parentMaxY < this.maxJumpHeight) {
            return pos.getY() + 1;
         } else {
            return target.getBlock() instanceof StairBlock
                  && parentY - 0.5 < this.maxJumpHeight
                  && target.getValue(StairBlock.HALF) == Half.BOTTOM
                  && getXZFacing(parent.pos, pos) == target.getValue(StairBlock.FACING)
               ? pos.getY() + 1
               : -1;
         }
      } else {
         return -1;
      }
   }

   private Pair<Integer, BlockPos> getHighest(MNode node) {
      int max = 1;
      BlockPos pos = node.pos;
      BlockPos direction = null;
      if (this.world.getBlockState(pos.north()).canOcclude() && this.climbableTop(pos.north(), Direction.SOUTH, node) > max) {
         max = this.climbableTop(pos.north(), Direction.SOUTH, node);
         direction = PathfindingConstants.BLOCKPOS_NORTH;
      }

      if (this.world.getBlockState(pos.east()).canOcclude() && this.climbableTop(pos.east(), Direction.WEST, node) > max) {
         max = this.climbableTop(pos.east(), Direction.WEST, node);
         direction = PathfindingConstants.BLOCKPOS_EAST;
      }

      if (this.world.getBlockState(pos.south()).canOcclude() && this.climbableTop(pos.south(), Direction.NORTH, node) > max) {
         max = this.climbableTop(pos.south(), Direction.NORTH, node);
         direction = PathfindingConstants.BLOCKPOS_SOUTH;
      }

      if (this.world.getBlockState(pos.west()).canOcclude() && this.climbableTop(pos.west(), Direction.EAST, node) > max) {
         max = this.climbableTop(pos.west(), Direction.EAST, node);
         direction = PathfindingConstants.BLOCKPOS_WEST;
      }

      return new Pair(max, direction);
   }

   private int climbableTop(BlockPos pos, Direction direction, MNode node) {
      BlockState target = this.world.getBlockState(pos);

      int i;
      for (i = 0; target.canOcclude(); i++) {
         pos = pos.above();
         target = this.world.getBlockState(pos);
         BlockState origin = this.world.getBlockState(pos.relative(direction));
         if (!this.isPassable(origin, pos.relative(direction), node)) {
            i = 0;
            break;
         }
      }

      return i;
   }

   private boolean checkHeadBlock(@Nullable MNode parent, BlockPos pos) {
      BlockPos localPos = pos;
      VoxelShape bb = this.world.getBlockState(pos).getCollisionShape(this.world, pos);
      if (bb.max(Axis.Y) < 1.0) {
         localPos = pos.above();
      }

      if (parent == null || !this.isPassableBB(parent.pos, pos.above(), parent)) {
         VoxelShape bb1 = this.world.getBlockState(pos.below()).getBlockSupportShape(this.world, pos.below());
         VoxelShape bb2 = this.world.getBlockState(pos.above()).getBlockSupportShape(this.world, pos.above());
         if (pos.above().getY() + this.getStartY(bb2, 1) - (pos.below().getY() + this.getEndY(bb1, 0)) < 2.0) {
            return true;
         }

         if (parent != null) {
            VoxelShape bb3 = this.world.getBlockState(parent.pos.below()).getBlockSupportShape(this.world, pos.below());
            if (pos.above().getY() + this.getStartY(bb2, 1) - (parent.pos.below().getY() + this.getEndY(bb3, 0)) < 1.75) {
               return true;
            }
         }
      }

      if (parent != null) {
         BlockState hereState = this.world.getBlockState(localPos.below());
         VoxelShape bb1x = this.world.getBlockState(pos).getBlockSupportShape(this.world, pos);
         VoxelShape bb2x = this.world.getBlockState(localPos.above()).getBlockSupportShape(this.world, localPos.above());
         return localPos.above().getY() + this.getStartY(bb2x, 1) - (pos.getY() + this.getEndY(bb1x, 0)) >= 2.0
            ? false
            : this.isLiquid(hereState) && !this.isPassable(pos, parent);
      } else {
         return false;
      }
   }

   private double getStartY(VoxelShape bb, int def) {
      return bb.isEmpty() ? def : bb.min(Axis.Y);
   }

   private double getEndY(VoxelShape bb, int def) {
      return bb.isEmpty() ? def : bb.max(Axis.Y);
   }

   protected boolean isPassable(BlockState block, BlockPos pos, MNode parent) {
      BlockPos parentPos = parent == null ? this.start : parent.pos;
      BlockState parentBlock = this.world.getBlockState(parentPos);
      if (parentBlock.getBlock() instanceof TrapDoorBlock) {
         BlockPos dir = pos.subtract(parentPos);
         if (dir.getX() != 0 || dir.getZ() != 0) {
            Direction direction = getXZFacing(parentPos, pos);
            Direction facing = (Direction)parentBlock.getValue(TrapDoorBlock.FACING);
            if (direction == facing.getOpposite()) {
               return false;
            }
         }
      }

      if (block.isAir()) {
         return true;
      } else {
         VoxelShape shape = block.getBlockSupportShape(this.world, pos);
         if (block.blocksMotion() && !shape.isEmpty() && !(shape.max(Axis.Y) <= 0.1)) {
            if (block.getBlock() instanceof TrapDoorBlock) {
               BlockPos dir = pos.subtract(parentPos);
               if (dir.getY() != 0 && dir.getX() == 0 && dir.getZ() == 0) {
                  return true;
               } else {
                  Direction direction = getXZFacing(parentPos, pos);
                  Direction facing = (Direction)block.getValue(TrapDoorBlock.FACING);
                  return direction == facing.getOpposite() ? true : direction != facing;
               }
            } else {
               return this.pathingOptions.canEnterDoors() && (block.getBlock() instanceof DoorBlock || block.getBlock() instanceof FenceGateBlock)
                  || block.getBlock() instanceof PressurePlateBlock
                  || block.getBlock() instanceof SignBlock
                  || block.getBlock() instanceof AbstractBannerBlock;
            }
         } else if (!(block.getBlock() instanceof FireBlock) && !(block.getBlock() instanceof SweetBerryBushBlock)) {
            return this.isLadder(block.getBlock(), pos)
               ? true
               : shape.isEmpty()
                  || shape.max(Axis.Y) <= 0.125
                     && !this.isLiquid(block)
                     && (block.getBlock() != Blocks.SNOW || (Integer)block.getValue(SnowLayerBlock.LAYERS) == 1);
         } else {
            return false;
         }
      }
   }

   protected boolean isPassable(BlockPos pos, MNode parent) {
      BlockState state = this.world.getBlockState(pos);
      VoxelShape shape = state.getBlockSupportShape(this.world, pos);
      if (this.passabilityNavigator != null && this.passabilityNavigator.isBlockExplicitlyNotPassable(state, pos, pos)) {
         return false;
      } else if (!shape.isEmpty() && !(shape.max(Axis.Y) <= 0.1)) {
         return this.isPassable(state, pos, parent);
      } else {
         return this.passabilityNavigator != null && this.passabilityNavigator.isBlockExplicitlyPassable(state, pos, pos)
            ? this.isPassable(state, pos, parent)
            : true;
      }
   }

   protected boolean isPassableBBFull(BlockPos pos, MNode parent) {
      for (int i = this.entitySizeXZStart; i <= this.entitySizeXZEnd; i++) {
         for (int j = 0; j < this.entitySizeY; j++) {
            for (int k = this.entitySizeXZStart; k <= this.entitySizeXZEnd; k++) {
               if (!this.isPassable(pos.offset(i, j, k), parent)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   protected boolean isPassableBB(BlockPos parentPos, BlockPos pos, MNode parent) {
      Direction facingDir = getXZFacing(parentPos, pos);
      if (facingDir != Direction.DOWN && facingDir != Direction.UP) {
         facingDir = facingDir.getClockWise();

         for (int i = this.entitySizeXZStart; i <= this.entitySizeXZEnd; i++) {
            for (int j = 0; j < this.entitySizeY; j++) {
               if (!this.isPassable(pos.relative(facingDir, i).above(j), parent)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean isPassableBBDown(BlockPos parentPos, BlockPos pos, MNode parent) {
      Direction facingDir = getXZFacing(parentPos, pos);
      if (facingDir != Direction.DOWN && facingDir != Direction.UP) {
         facingDir = facingDir.getClockWise();

         for (int i = this.entitySizeXZStart; i <= this.entitySizeXZEnd; i++) {
            for (int j = 0; j < this.entitySizeY; j++) {
               if (!this.isPassable(pos.relative(facingDir, i).above(j), parent) || pos.getY() <= parentPos.getY()) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected SurfaceType isFlyable(BlockState blockState, BlockPos pos, MNode parent) {
      Block block = blockState.getBlock();
      if (!(block instanceof FenceBlock)
         && !(block instanceof FenceGateBlock)
         && !(block instanceof WallBlock)
         && !(block instanceof FireBlock)
         && !(block instanceof CampfireBlock)
         && !(block instanceof BambooStalkBlock)
         && !(block instanceof BambooSaplingBlock)
         && !(blockState.getShape(this.world, pos).max(Axis.Y) > 1.0)) {
         FluidState fluid = this.world.getFluidState(pos);
         if (fluid == null || fluid.isEmpty() || fluid.getType() != Fluids.LAVA && fluid.getType() != Fluids.FLOWING_LAVA) {
            return this.isPassable(blockState, pos, parent) ? SurfaceType.FLYABLE : SurfaceType.DROPABLE;
         } else {
            return SurfaceType.NOT_PASSABLE;
         }
      } else {
         return SurfaceType.NOT_PASSABLE;
      }
   }

   protected SurfaceType isWalkableSurface(BlockState blockState, BlockPos pos) {
      Block block = blockState.getBlock();
      if (!(block instanceof FenceBlock)
         && !(block instanceof FenceGateBlock)
         && !(block instanceof WallBlock)
         && !(block instanceof FireBlock)
         && !(block instanceof CampfireBlock)
         && !(block instanceof BambooStalkBlock)
         && !(block instanceof BambooSaplingBlock)
         && !(blockState.getShape(this.world, pos).max(Axis.Y) > 1.0)) {
         FluidState fluid = this.world.getFluidState(pos);
         if (fluid == null || fluid.isEmpty() || fluid.getType() != Fluids.LAVA && fluid.getType() != Fluids.FLOWING_LAVA) {
            if (block instanceof SignBlock) {
               return SurfaceType.DROPABLE;
            } else {
               return !blockState.isSolid()
                     && (blockState.getBlock() != Blocks.SNOW || blockState.getValue(SnowLayerBlock.LAYERS) <= 1)
                     && !(block instanceof WoolCarpetBlock)
                  ? SurfaceType.DROPABLE
                  : SurfaceType.WALKABLE;
            }
         } else {
            return SurfaceType.NOT_PASSABLE;
         }
      } else {
         return SurfaceType.NOT_PASSABLE;
      }
   }

   protected boolean isLadder(Block block, BlockPos pos) {
      return AMCompat.isLadder(this.world.getBlockState(pos), this.world, pos, this.entity.get());
   }

   protected boolean isLadder(BlockPos pos) {
      return this.isLadder(this.world.getBlockState(pos).getBlock(), pos);
   }

   public void setPathingOptions(PathingOptions pathingOptions) {
      this.pathingOptions = pathingOptions;
   }

   public boolean isInRestrictedArea(BlockPos pos) {
      if (this.restrictionType == AbstractAdvancedPathNavigate.RestrictionType.NONE) {
         return true;
      } else {
         boolean isInXZ = pos.getX() <= this.maxX && pos.getZ() <= this.maxZ && pos.getZ() >= this.minZ && pos.getX() >= this.minX;
         if (!isInXZ) {
            return false;
         } else {
            return this.restrictionType == AbstractAdvancedPathNavigate.RestrictionType.XZ ? true : pos.getY() <= this.maxY && pos.getY() >= this.minY;
         }
      }
   }
}

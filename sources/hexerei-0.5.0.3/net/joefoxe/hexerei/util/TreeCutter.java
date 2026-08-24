package net.joefoxe.hexerei.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.KelpPlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class TreeCutter {
   public static final TreeCutter.Tree NO_TREE = new TreeCutter.Tree(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

   public static boolean canDynamicTreeCutFrom(Block startBlock) {
      return false;
   }

   @Nonnull
   public static Optional<AbstractBlockBreakQueue> findDynamicTree(Block startBlock, BlockPos pos) {
      return Optional.empty();
   }

   @Nonnull
   public static TreeCutter.Tree findTree(@Nullable BlockGetter reader, BlockPos pos) {
      if (reader == null) {
         return NO_TREE;
      } else {
         List<BlockPos> logs = new ArrayList<>();
         List<BlockPos> leaves = new ArrayList<>();
         List<BlockPos> attachments = new ArrayList<>();
         Set<BlockPos> visited = new HashSet<>();
         List<BlockPos> frontier = new LinkedList<>();
         BlockState stateAbove = reader.getBlockState(pos.above());
         if (isVerticalPlant(stateAbove)) {
            logs.add(pos.above());

            for (int i = 1; i < 256; i++) {
               BlockPos current = pos.above(i);
               if (!isVerticalPlant(reader.getBlockState(current))) {
                  break;
               }

               logs.add(current);
            }

            Collections.reverse(logs);
            return new TreeCutter.Tree(logs, leaves, attachments);
         } else if (isChorus(stateAbove)) {
            frontier.add(pos.above());

            while (!frontier.isEmpty()) {
               BlockPos current = frontier.remove(0);
               visited.add(current);
               logs.add(current);

               for (Direction direction : Direction.values()) {
                  BlockPos offset = current.relative(direction);
                  if (!visited.contains(offset) && isChorus(reader.getBlockState(offset))) {
                     frontier.add(offset);
                  }
               }
            }

            Collections.reverse(logs);
            return new TreeCutter.Tree(logs, leaves, attachments);
         } else if (!validateCut(reader, pos)) {
            return NO_TREE;
         } else {
            visited.add(pos);
            BlockPos.betweenClosedStream(pos.offset(-1, 0, -1), pos.offset(1, 1, 1)).forEach(p -> frontier.add(new BlockPos(p)));
            boolean hasRoots = false;

            while (!frontier.isEmpty()) {
               BlockPos currentPos = frontier.remove(0);
               if (visited.add(currentPos)) {
                  BlockState currentState = reader.getBlockState(currentPos);
                  if (isRoot(currentState)) {
                     hasRoots = true;
                  } else if (!isLog(currentState)) {
                     continue;
                  }

                  logs.add(currentPos);
                  forNeighbours(currentPos, visited, TreeCutter.SearchDirection.UP, p -> frontier.add(new BlockPos(p)));
               }
            }

            visited.clear();
            visited.addAll(logs);
            frontier.addAll(logs);
            if (hasRoots) {
               while (!frontier.isEmpty()) {
                  BlockPos currentPos = frontier.remove(0);
                  if (logs.contains(currentPos) || visited.add(currentPos)) {
                     BlockState currentState = reader.getBlockState(currentPos);
                     if (isRoot(currentState)) {
                        logs.add(currentPos);
                        forNeighbours(currentPos, visited, TreeCutter.SearchDirection.DOWN, p -> frontier.add(new BlockPos(p)));
                     }
                  }
               }

               visited.clear();
               visited.addAll(logs);
               frontier.addAll(logs);
            }

            while (!frontier.isEmpty()) {
               BlockPos prevPos = frontier.remove(0);
               if (logs.contains(prevPos) || visited.add(prevPos)) {
                  BlockState prevState = reader.getBlockState(prevPos);
                  int prevLeafDistance = isLeaf(prevState) ? getLeafDistance(prevState) : 0;
                  forNeighbours(prevPos, visited, TreeCutter.SearchDirection.BOTH, currentPos -> {
                     BlockState state = reader.getBlockState(currentPos);
                     BlockPos subtract = currentPos.subtract(pos);
                     BlockPos currentPosImmutable = currentPos.immutable();
                     int horizontalDistance = Math.max(Math.abs(subtract.getX()), Math.abs(subtract.getZ()));
                     if (horizontalDistance <= nonDecayingLeafDistance(state)) {
                        leaves.add(currentPosImmutable);
                        frontier.add(currentPosImmutable);
                     } else {
                        if (isLeaf(state) && getLeafDistance(state) > prevLeafDistance) {
                           leaves.add(currentPosImmutable);
                           frontier.add(currentPosImmutable);
                        }
                     }
                  });
               }
            }

            return new TreeCutter.Tree(logs, leaves, attachments);
         }
      }
   }

   private static int getLeafDistance(BlockState state) {
      IntegerProperty distanceProperty = LeavesBlock.DISTANCE;

      for (Property<?> property : state.getValues().keySet()) {
         if (property instanceof IntegerProperty ip && property.getName().equals("distance")) {
            distanceProperty = ip;
         }
      }

      return (Integer)state.getValue(distanceProperty);
   }

   public static boolean isChorus(BlockState stateAbove) {
      return stateAbove.getBlock() instanceof ChorusPlantBlock || stateAbove.getBlock() instanceof ChorusFlowerBlock;
   }

   public static boolean isVerticalPlant(BlockState stateAbove) {
      Block block = stateAbove.getBlock();
      if (block instanceof BambooStalkBlock) {
         return true;
      } else if (block instanceof CactusBlock) {
         return true;
      } else if (block instanceof SugarCaneBlock) {
         return true;
      } else {
         return block instanceof KelpPlantBlock ? true : block instanceof KelpBlock;
      }
   }

   private static boolean validateCut(BlockGetter reader, BlockPos pos) {
      Set<BlockPos> visited = new HashSet<>();
      List<BlockPos> frontier = new LinkedList<>();
      frontier.add(pos);
      frontier.add(pos.above());
      int posY = pos.getY();

      while (!frontier.isEmpty()) {
         BlockPos currentPos = frontier.remove(0);
         BlockPos belowPos = currentPos.below();
         visited.add(currentPos);
         boolean lowerLayer = currentPos.getY() == posY;
         BlockState currentState = reader.getBlockState(currentPos);
         BlockState belowState = reader.getBlockState(belowPos);
         if (isLog(currentState) || isRoot(currentState)) {
            if (!lowerLayer && !pos.equals(belowPos) && (isLog(belowState) || isRoot(belowState))) {
               return false;
            }

            for (Direction direction : Direction.values()) {
               if (direction != Direction.DOWN && (direction != Direction.UP || lowerLayer)) {
                  BlockPos offset = currentPos.relative(direction);
                  if (!visited.contains(offset)) {
                     frontier.add(offset);
                  }
               }
            }
         }
      }

      return true;
   }

   private static void forNeighbours(BlockPos pos, Set<BlockPos> visited, TreeCutter.SearchDirection direction, Consumer<BlockPos> acceptor) {
      BlockPos.betweenClosedStream(pos.offset(-1, direction.minY, -1), pos.offset(1, direction.maxY, 1)).filter((visited::contains).negate()).forEach(acceptor);
   }

   public static boolean isRoot(BlockState state) {
      return state.is(Blocks.MANGROVE_ROOTS);
   }

   public static boolean isLog(BlockState state) {
      return state.is(BlockTags.LOGS) || state.is(Blocks.MUSHROOM_STEM);
   }

   private static int nonDecayingLeafDistance(BlockState state) {
      if (state.is(Blocks.RED_MUSHROOM_BLOCK)) {
         return 2;
      } else if (state.is(Blocks.BROWN_MUSHROOM_BLOCK)) {
         return 3;
      } else {
         return !state.is(BlockTags.WART_BLOCKS) && !state.is(Blocks.WEEPING_VINES) && !state.is(Blocks.WEEPING_VINES_PLANT) ? -1 : 3;
      }
   }

   private static boolean isLeaf(BlockState state) {
      for (Property<?> property : state.getValues().keySet()) {
         if (property instanceof IntegerProperty && property.getName().equals("distance")) {
            return true;
         }
      }

      return false;
   }

   private static enum SearchDirection {
      UP(0, 1),
      DOWN(-1, 0),
      BOTH(-1, 1);

      int minY;
      int maxY;

      private SearchDirection(int minY, int maxY) {
         this.minY = minY;
         this.maxY = maxY;
      }
   }

   public static class Tree extends AbstractBlockBreakQueue {
      private final List<BlockPos> logs;
      private final List<BlockPos> leaves;
      private final List<BlockPos> attachments;

      public Tree(List<BlockPos> logs, List<BlockPos> leaves, List<BlockPos> attachments) {
         this.logs = logs;
         this.leaves = leaves;
         this.attachments = attachments;
      }

      @Override
      public void destroyBlocks(Level world, ItemStack toDamage, @Nullable Player playerEntity, BiConsumer<BlockPos, ItemStack> drop) {
         this.attachments.forEach(this.makeCallbackFor(world, 0.03125F, toDamage, playerEntity, drop));
         this.logs.forEach(this.makeCallbackFor(world, 0.5F, toDamage, playerEntity, drop));
         this.leaves.forEach(this.makeCallbackFor(world, 0.125F, toDamage, playerEntity, drop));
      }
   }
}

package com.seibel.distanthorizons.core.util.objects.quadTree;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.gridList.MovableGridRingList;
import com.seibel.distanthorizons.coreapi.util.BitShiftUtil;
import com.seibel.distanthorizons.coreapi.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import org.jetbrains.annotations.Nullable;

public class QuadTree<T> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public final byte treeRootDetailLevel;
   public final byte treeLeafDetailLevel;
   private final int diameterInBlocks;
   private final MovableGridRingList<QuadNode<T>> topRingList;
   private DhBlockPos2D centerBlockPos;
   private int blockDistanceForNodeClearing;

   public QuadTree(int diameterInBlocks, int blockDistanceForNodeClearing, DhBlockPos2D centerBlockPos, byte treeLeafDetailLevel) {
      this.centerBlockPos = centerBlockPos;
      this.diameterInBlocks = diameterInBlocks;
      this.blockDistanceForNodeClearing = blockDistanceForNodeClearing;
      this.treeLeafDetailLevel = treeLeafDetailLevel;
      this.treeRootDetailLevel = (byte)Math.max(Math.max(1, this.treeLeafDetailLevel), MathUtil.log2(diameterInBlocks));
      int halfSizeInRootNodes = Math.floorDiv(this.diameterInBlocks, 2) / BitShiftUtil.powerOfTwo(this.treeRootDetailLevel);
      halfSizeInRootNodes++;
      MovableGridRingList.Pos2D ringListCenterPos = new MovableGridRingList.Pos2D(
         BitShiftUtil.divideByPowerOfTwo(this.centerBlockPos.x, this.treeRootDetailLevel),
         BitShiftUtil.divideByPowerOfTwo(this.centerBlockPos.z, this.treeRootDetailLevel)
      );
      this.topRingList = new MovableGridRingList<>(halfSizeInRootNodes, ringListCenterPos.getX(), ringListCenterPos.getY());
   }

   public int nodeCount() {
      int count = 0;

      for (QuadNode<T> node : this.topRingList) {
         if (node != null) {
            Iterator<QuadNode<T>> nodeIterator = node.getNodeIterator();

            while (nodeIterator.hasNext()) {
               if (nodeIterator.next().value != null) {
                  count++;
               }
            }
         }
      }

      return count;
   }

   public int leafNodeCount() {
      int count = 0;

      for (QuadNode<T> rootNode : this.topRingList) {
         if (rootNode != null) {
            Iterator<QuadNode<T>> leafNodeIterator = rootNode.getLeafNodeIterator();

            while (leafNodeIterator.hasNext()) {
               QuadNode<T> node = leafNodeIterator.next();
               if (node != null && this.isSectionPosInBounds(node.sectionPos)) {
                  count++;
               }
            }
         }
      }

      return count;
   }

   public int ringListWidth() {
      return 3;
   }

   public int ringListHalfWidth() {
      return 1;
   }

   public int diameterInBlocks() {
      return this.diameterInBlocks;
   }

   @Nullable
   public final T tryGetValue(long pos) {
      QuadNode<T> node = this.tryGetNode(pos);
      return node != null ? node.value : null;
   }

   @Nullable
   public final QuadNode<T> tryGetNode(long pos) {
      return this.getOrSetNode(pos, false, null, false);
   }

   @Nullable
   public final QuadNode<T> getNode(long pos) throws IndexOutOfBoundsException {
      return this.getOrSetNode(pos, false, null, true);
   }

   @Nullable
   public final T getValue(long pos) throws IndexOutOfBoundsException {
      QuadNode<T> node = this.getNode(pos);
      return node != null ? node.value : null;
   }

   @Nullable
   public final T setValue(long pos, T value) throws IndexOutOfBoundsException {
      T previousValue = this.getValue(pos);
      this.getOrSetNode(pos, true, value, true);
      return previousValue;
   }

   @Nullable
   protected final QuadNode<T> getOrSetNode(long pos, boolean setNewValue, T newValue, boolean throwIfOutOfBounds) throws IndexOutOfBoundsException {
      if (!this.isSectionPosInBounds(pos)) {
         if (throwIfOutOfBounds) {
            int radius = this.diameterInBlocks() / 2;
            DhBlockPos2D minBlockPos = this.getCenterBlockPos().add(new DhBlockPos2D(-radius, -radius));
            DhBlockPos2D maxBlockPos = this.getCenterBlockPos().add(new DhBlockPos2D(radius, radius));
            throw new IndexOutOfBoundsException(
               "QuadTree GetOrSet failed. Position out of bounds, min block pos: ["
                  + minBlockPos
                  + "], max block pos: ["
                  + maxBlockPos
                  + "], leaf detail level: ["
                  + this.treeLeafDetailLevel
                  + "], root detail level: ["
                  + this.treeRootDetailLevel
                  + "]. Requested section pos: ["
                  + DhSectionPos.toString(pos)
                  + "]."
            );
         } else {
            return null;
         }
      } else {
         long rootPos = DhSectionPos.convertToDetailLevel(pos, this.treeRootDetailLevel);
         int ringListPosX = DhSectionPos.getX(rootPos);
         int ringListPosZ = DhSectionPos.getZ(rootPos);
         QuadNode<T> rootQuadNode = this.topRingList.get(ringListPosX, ringListPosZ);
         if (rootQuadNode == null) {
            if (!setNewValue) {
               return null;
            }

            rootQuadNode = new QuadNode<>(rootPos, this.treeLeafDetailLevel);
            boolean successfullyAdded = this.topRingList.set(ringListPosX, ringListPosZ, rootQuadNode);
            if (!successfullyAdded) {
               LodUtil.assertNotReach("Failed to add root quadTree node at position: [" + DhSectionPos.toString(rootPos) + "]");
            }
         }

         if (!DhSectionPos.contains(rootQuadNode.sectionPos, pos)) {
            LodUtil.assertNotReach("failed to get a root node that contains the input position: " + pos + " root node pos: " + rootQuadNode.sectionPos);
         }

         QuadNode<T> returnNode = rootQuadNode.getNode(pos);
         if (setNewValue) {
            rootQuadNode.setValue(pos, newValue);
         }

         return returnNode;
      }
   }

   public boolean isSectionPosInBounds(long testPos) {
      boolean detailLevelWithinBounds = this.treeLeafDetailLevel <= DhSectionPos.getDetailLevel(testPos)
         && DhSectionPos.getDetailLevel(testPos) <= this.treeRootDetailLevel;
      if (!detailLevelWithinBounds) {
         return false;
      } else {
         DhBlockPos2D treeBlockCorner = this.centerBlockPos.add(new DhBlockPos2D(-this.diameterInBlocks / 2, -this.diameterInBlocks / 2));
         long treeCornerPos = DhSectionPos.encode((byte)0, treeBlockCorner.x, treeBlockCorner.z);
         long inputSectionCorner = DhSectionPos.convertToDetailLevel(testPos, (byte)0);
         long inputCornerPos = DhSectionPos.encode((byte)0, DhSectionPos.getX(inputSectionCorner), DhSectionPos.getZ(inputSectionCorner));
         int inputBlockWidth = BitShiftUtil.powerOfTwo(DhSectionPos.getDetailLevel(testPos));
         return DoSquaresOverlap(treeCornerPos, this.diameterInBlocks, inputCornerPos, inputBlockWidth);
      }
   }

   private static boolean DoSquaresOverlap(long square1Min, int square1Width, long square2Min, int square2Width) {
      float rect1MinX = DhSectionPos.getX(square1Min);
      float rect1MaxX = DhSectionPos.getX(square1Min) + square1Width;
      float rect1MinZ = DhSectionPos.getZ(square1Min);
      float rect1MaxZ = DhSectionPos.getZ(square1Min) + square1Width;
      float rect2MinX = DhSectionPos.getX(square2Min);
      float rect2MaxX = DhSectionPos.getX(square2Min) + square2Width;
      float rect2MinZ = DhSectionPos.getZ(square2Min);
      float rect2MaxZ = DhSectionPos.getZ(square2Min) + square2Width;
      return rect1MinX < rect2MaxX && rect1MaxX > rect2MinX && rect1MinZ < rect2MaxZ && rect1MaxZ > rect2MinZ;
   }

   public int getNonNullChildCountAtPos(long pos) {
      return this.getChildCountAtPos(pos, false);
   }

   public int getChildCountAtPos(long pos, boolean includeNullValues) {
      int childCount = 0;

      for (int i = 0; i < 4; i++) {
         long childPos = DhSectionPos.getChildByIndex(pos, i);
         if (this.isSectionPosInBounds(childPos)) {
            T value = this.getValue(childPos);
            if (includeNullValues || value != null) {
               childCount++;
            }
         }
      }

      return childCount;
   }

   public LongIterator rootNodePosIterator() {
      return new QuadTree.QuadTreeRootPosIterator(true, null);
   }

   public Iterator<QuadNode<T>> nodeIteratorWithStoppingFilter(QuadTree.INodeIteratorStoppingFunc<T> stoppingFilterFunc) {
      return new QuadTree.QuadTreeNodeIterator(false, stoppingFilterFunc);
   }

   public Iterator<QuadNode<T>> nodeIterator() {
      return new QuadTree.QuadTreeNodeIterator(false, null);
   }

   public Iterator<QuadNode<T>> leafNodeIterator() {
      return new QuadTree.QuadTreeNodeIterator(true, null);
   }

   public void setCenterBlockPos(DhBlockPos2D newCenterPos) {
      this.setCenterBlockPos(newCenterPos, null, null);
   }

   public void setCenterBlockPos(
      DhBlockPos2D newCenterPos, @Nullable Consumer<? super T> removedConsumer, @Nullable Consumer<? super T> mutateOutOfBoundConsumer
   ) {
      boolean ringListMoved = false;
      int newCenterPosX = BitShiftUtil.divideByPowerOfTwo(newCenterPos.x, this.treeRootDetailLevel);
      int newCenterPosZ = BitShiftUtil.divideByPowerOfTwo(newCenterPos.z, this.treeRootDetailLevel);
      if (this.topRingList.getCenter().getX() != newCenterPosX || this.topRingList.getCenter().getY() != newCenterPosZ) {
         ringListMoved = true;
      }

      boolean recalculateOutOfBoundNodes = false;
      int centerBlockDistance = this.centerBlockPos.manhattanDist(newCenterPos);
      if (centerBlockDistance >= this.blockDistanceForNodeClearing) {
         recalculateOutOfBoundNodes = true;
      }

      if (ringListMoved || recalculateOutOfBoundNodes) {
         this.centerBlockPos = newCenterPos;
         this.topRingList.moveTo(newCenterPosX, newCenterPosZ, quadNode -> {
            if (quadNode != null) {
               quadNode.deleteAllChildren(removedConsumer);
               if (removedConsumer != null) {
                  removedConsumer.accept(quadNode.value);
               }
            }
         });
         this.topRingList.forEach(rootNode -> this.mutateOutOfBoundChildNodes((QuadNode<T>)rootNode, mutateOutOfBoundConsumer));
      }
   }

   private void mutateOutOfBoundChildNodes(@Nullable QuadNode<T> quadNode, @Nullable Consumer<? super T> mutateOutOfBoundConsumer) {
      if (quadNode != null) {
         for (int i = 0; i < 4; i++) {
            QuadNode<T> childNode = quadNode.getChildByIndex(i);
            if (childNode != null && childNode.value != null) {
               this.mutateOutOfBoundChildNodes(childNode, mutateOutOfBoundConsumer);
               if (!this.isSectionPosInBounds(childNode.sectionPos) && mutateOutOfBoundConsumer != null) {
                  mutateOutOfBoundConsumer.accept(childNode.value);
               }
            }
         }
      }
   }

   public final DhBlockPos2D getCenterBlockPos() {
      return this.centerBlockPos;
   }

   public boolean isEmpty() {
      return this.nodeCount() == 0;
   }

   @Override
   public String toString() {
      return "center block: "
         + this.centerBlockPos
         + ", block width: "
         + this.diameterInBlocks
         + ", detail level range: ["
         + this.treeLeafDetailLevel
         + "-"
         + this.treeRootDetailLevel
         + "], leaf #: "
         + this.leafNodeCount();
   }

   @FunctionalInterface
   public interface INodeIteratorStoppingFunc<T> {
      boolean iteratorShouldStop(QuadNode<T> quadNode);
   }

   private class QuadTreeNodeIterator implements Iterator<QuadNode<T>> {
      private final QuadTree<T>.QuadTreeRootPosIterator rootNodePosIterator;
      private Iterator<QuadNode<T>> currentNodeIterator;
      private QuadNode<T> lastNode = null;
      private final boolean onlyReturnLeaves;
      @Nullable
      private final QuadTree.INodeIteratorStoppingFunc<T> stopIteratingFunc;

      public QuadTreeNodeIterator(boolean onlyReturnLeaves, @Nullable QuadTree.INodeIteratorStoppingFunc<T> stopIteratingFunc) {
         this.rootNodePosIterator = QuadTree.this.new QuadTreeRootPosIterator(false, stopIteratingFunc);
         this.onlyReturnLeaves = onlyReturnLeaves;
         this.stopIteratingFunc = stopIteratingFunc;
      }

      @Override
      public boolean hasNext() {
         if (!this.rootNodePosIterator.hasNext() && this.currentNodeIterator != null && !this.currentNodeIterator.hasNext()) {
            return false;
         } else {
            if (this.currentNodeIterator == null || !this.currentNodeIterator.hasNext()) {
               this.currentNodeIterator = this.getNextChildNodeIterator();
            }

            return this.currentNodeIterator != null && this.currentNodeIterator.hasNext();
         }
      }

      public QuadNode<T> next() {
         if (this.currentNodeIterator == null || !this.currentNodeIterator.hasNext()) {
            this.currentNodeIterator = this.getNextChildNodeIterator();
         }

         this.lastNode = this.currentNodeIterator.next();
         return this.lastNode;
      }

      private Iterator<QuadNode<T>> getNextChildNodeIterator() {
         Iterator<QuadNode<T>> nodeIterator = null;

         while ((nodeIterator == null || !nodeIterator.hasNext()) && this.rootNodePosIterator.hasNext()) {
            long sectionPos = this.rootNodePosIterator.nextLong();
            QuadNode<T> rootNode = QuadTree.this.tryGetNode(sectionPos);
            if (rootNode != null) {
               nodeIterator = this.onlyReturnLeaves ? rootNode.getLeafNodeIterator() : rootNode.getNodeIterator(this.stopIteratingFunc);
            }
         }

         return nodeIterator;
      }

      @Override
      public void remove() {
         if (this.lastNode == null) {
            throw new NoSuchElementException("No last node found.");
         } else {
            QuadNode<T> node = QuadTree.this.getOrSetNode(this.lastNode.sectionPos, true, null, false);
            if (node != null) {
               node.deleteAllChildren();
            }
         }
      }

      @Override
      public void forEachRemaining(Consumer<? super QuadNode<T>> action) {
         Iterator.super.forEachRemaining(action);
      }
   }

   private class QuadTreeRootPosIterator implements LongIterator {
      private final LongArrayFIFOQueue iteratorPosQueue = new LongArrayFIFOQueue();
      @Nullable
      private final QuadTree.INodeIteratorStoppingFunc<T> stopIteratingFunc;

      public QuadTreeRootPosIterator(boolean includeNullNodes, @Nullable QuadTree.INodeIteratorStoppingFunc<T> stopIteratingFunc) {
         this.stopIteratingFunc = stopIteratingFunc;
         QuadTree.this.topRingList.forEachPosOrdered((node, pos2D) -> {
            if (this.stopIteratingFunc == null || !this.stopIteratingFunc.iteratorShouldStop(node)) {
               if (node != null || includeNullNodes) {
                  long rootPos = DhSectionPos.encode(QuadTree.this.treeRootDetailLevel, pos2D.getX(), pos2D.getY());
                  if (QuadTree.this.isSectionPosInBounds(rootPos)) {
                     this.iteratorPosQueue.enqueue(rootPos);
                  }
               }
            }
         });
      }

      public boolean hasNext() {
         return this.iteratorPosQueue.size() != 0;
      }

      public long nextLong() {
         if (this.iteratorPosQueue.size() == 0) {
            throw new NoSuchElementException();
         } else {
            return this.iteratorPosQueue.dequeueLong();
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("remove");
      }

      public void forEachRemaining(LongConsumer action) {
         super.forEachRemaining(action);
      }
   }
}

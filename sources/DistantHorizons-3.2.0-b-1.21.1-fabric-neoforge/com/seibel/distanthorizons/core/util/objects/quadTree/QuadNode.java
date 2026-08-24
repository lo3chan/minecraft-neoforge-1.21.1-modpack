package com.seibel.distanthorizons.core.util.objects.quadTree;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.objects.quadTree.iterators.QuadNodeDirectChildIterator;
import com.seibel.distanthorizons.core.util.objects.quadTree.iterators.QuadNodeDirectChildPosIterator;
import com.seibel.distanthorizons.core.util.objects.quadTree.iterators.QuadTreeNodeIterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class QuadNode<T> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public final long sectionPos;
   public final byte parentTreeLeafDetailLevel;
   @Nullable
   public T value;
   @Nullable
   public QuadNode<T> nwChild;
   @Nullable
   public QuadNode<T> neChild;
   @Nullable
   public QuadNode<T> swChild;
   @Nullable
   public QuadNode<T> seChild;

   public QuadNode(long sectionPos, byte parentTreeLeafDetailLevel) {
      this.sectionPos = sectionPos;
      this.parentTreeLeafDetailLevel = parentTreeLeafDetailLevel;
   }

   public int getDirectChildCount() {
      int count = 0;
      if (this.nwChild != null) {
         count++;
      }

      if (this.neChild != null) {
         count++;
      }

      if (this.swChild != null) {
         count++;
      }

      if (this.seChild != null) {
         count++;
      }

      return count;
   }

   public int getTotalChildCount() {
      int count = 0;

      for (int i = 0; i < 4; i++) {
         if (this.getChildByIndex(i) != null) {
            count++;
         }
      }

      return count;
   }

   public int getNonNullChildCount() {
      int count = 0;

      for (int i = 0; i < 4; i++) {
         QuadNode<T> child = this.getChildByIndex(i);
         if (child != null && (child.value != null || child.getNonNullChildCount() != 0)) {
            count++;
         }
      }

      return count;
   }

   @Nullable
   public QuadNode<T> getChildByIndex(int child0to3) throws IllegalArgumentException {
      switch (child0to3) {
         case 0:
            return this.nwChild;
         case 1:
            return this.swChild;
         case 2:
            return this.neChild;
         case 3:
            return this.seChild;
         default:
            throw new IllegalArgumentException("child0to3 must be between 0 and 3");
      }
   }

   public QuadNode<T> getNode(long sectionPos) throws IllegalArgumentException {
      return this.getOrSetValue(sectionPos, false, null);
   }

   public T setValue(long sectionPos, T newValue) throws IllegalArgumentException {
      QuadNode<T> previousNode = this.getNode(sectionPos);
      if (previousNode != null) {
         T previousValue = previousNode.value;
         previousNode.value = newValue;
         return previousValue;
      } else {
         this.getOrSetValue(sectionPos, true, newValue);
         return null;
      }
   }

   private QuadNode<T> getOrSetValue(long inputSectionPos, boolean replaceValue, T newValue) throws IllegalArgumentException {
      if (!DhSectionPos.contains(this.sectionPos, inputSectionPos)) {
         LOGGER.error(
            (replaceValue ? "set " : "get ")
               + inputSectionPos
               + " center block: "
               + DhSectionPos.getCenterBlockPos(inputSectionPos)
               + ", this pos: "
               + this.sectionPos
               + " this center block: "
               + DhSectionPos.getCenterBlockPos(this.sectionPos)
         );
         throw new IllegalArgumentException(
            "Input section pos "
               + inputSectionPos
               + " outside of this quadNode's pos: "
               + this.sectionPos
               + ", this node's blockPos: "
               + DhSectionPos.convertToDetailLevel(this.sectionPos, (byte)0)
               + " block width: "
               + DhSectionPos.getBlockWidth(this.sectionPos)
               + " input detail level: "
               + DhSectionPos.convertToDetailLevel(inputSectionPos, (byte)0)
               + " width: "
               + DhSectionPos.getBlockWidth(inputSectionPos)
         );
      } else if (DhSectionPos.getDetailLevel(inputSectionPos) > DhSectionPos.getDetailLevel(this.sectionPos)) {
         throw new IllegalArgumentException(
            "detail level higher than this node. Node Detail level: "
               + DhSectionPos.getDetailLevel(this.sectionPos)
               + " input detail level: "
               + DhSectionPos.getDetailLevel(inputSectionPos)
         );
      } else if (DhSectionPos.getDetailLevel(inputSectionPos) == DhSectionPos.getDetailLevel(this.sectionPos) && inputSectionPos != this.sectionPos) {
         throw new IllegalArgumentException(
            "Node and input detail level are equal, however positions are not; this tree doesn't contain the requested position. Node pos: "
               + this.sectionPos
               + ", input pos: "
               + DhSectionPos.toString(inputSectionPos)
         );
      } else if (DhSectionPos.getDetailLevel(inputSectionPos) < this.parentTreeLeafDetailLevel) {
         throw new IllegalArgumentException(
            "Input position is requesting a detail level lower than what this node can provide. Tree leaf detail level: "
               + this.parentTreeLeafDetailLevel
               + ", input pos: "
               + DhSectionPos.toString(inputSectionPos)
         );
      } else if (DhSectionPos.getDetailLevel(inputSectionPos) == DhSectionPos.getDetailLevel(this.sectionPos)) {
         if (replaceValue) {
            this.value = newValue;
         }

         return this;
      } else {
         long nwPos = DhSectionPos.getChildByIndex(this.sectionPos, 0);
         long swPos = DhSectionPos.getChildByIndex(this.sectionPos, 1);
         long nePos = DhSectionPos.getChildByIndex(this.sectionPos, 2);
         long sePos = DhSectionPos.getChildByIndex(this.sectionPos, 3);
         if (DhSectionPos.contains(nwPos, inputSectionPos)) {
            if (replaceValue && this.nwChild == null) {
               this.nwChild = new QuadNode<>(nwPos, this.parentTreeLeafDetailLevel);
            }

            QuadNode<T> childNode = this.nwChild;
            return childNode != null ? childNode.getOrSetValue(inputSectionPos, replaceValue, newValue) : null;
         } else if (DhSectionPos.contains(swPos, inputSectionPos)) {
            if (replaceValue && this.swChild == null) {
               this.swChild = new QuadNode<>(swPos, this.parentTreeLeafDetailLevel);
            }

            QuadNode<T> childNode = this.swChild;
            return childNode != null ? childNode.getOrSetValue(inputSectionPos, replaceValue, newValue) : null;
         } else if (DhSectionPos.contains(nePos, inputSectionPos)) {
            if (replaceValue && this.neChild == null) {
               this.neChild = new QuadNode<>(nePos, this.parentTreeLeafDetailLevel);
            }

            QuadNode<T> childNode = this.neChild;
            return childNode != null ? childNode.getOrSetValue(inputSectionPos, replaceValue, newValue) : null;
         } else if (DhSectionPos.contains(sePos, inputSectionPos)) {
            if (replaceValue && this.seChild == null) {
               this.seChild = new QuadNode<>(sePos, this.parentTreeLeafDetailLevel);
            }

            QuadNode<T> childNode = this.seChild;
            return childNode != null ? childNode.getOrSetValue(inputSectionPos, replaceValue, newValue) : null;
         } else {
            throw new IllegalStateException(
               "input position not contained by any node children. This should've been caught by the this.sectionPos.contains(inputPos) assert before this point."
            );
         }
      }
   }

   public Iterator<QuadNode<T>> getNodeIterator() {
      return new QuadTreeNodeIterator<>(this, false, null);
   }

   public Iterator<QuadNode<T>> getNodeIterator(@Nullable QuadTree.INodeIteratorStoppingFunc<T> stopIteratingFunc) {
      return new QuadTreeNodeIterator<>(this, false, stopIteratingFunc);
   }

   public Iterator<QuadNode<T>> getLeafNodeIterator() {
      return new QuadTreeNodeIterator<>(this, true, null);
   }

   public LongIterator getChildPosIterator() {
      return new QuadNodeDirectChildPosIterator<>(this);
   }

   public Iterator<QuadNode<T>> getChildNodeIterator() {
      return new QuadNodeDirectChildIterator<>(this);
   }

   public void deleteAllChildren() {
      this.deleteAllChildren(null);
   }

   public void deleteAllChildren(@Nullable Consumer<? super T> removedItemConsumer) {
      for (int i = 0; i < 4; i++) {
         QuadNode<T> childNode = this.getChildByIndex(i);
         if (childNode != null) {
            childNode.deleteAllChildren(removedItemConsumer);
         }
      }

      if (this.nwChild != null && removedItemConsumer != null) {
         removedItemConsumer.accept(this.nwChild.value);
      }

      this.nwChild = null;
      if (this.neChild != null && removedItemConsumer != null) {
         removedItemConsumer.accept(this.neChild.value);
      }

      this.neChild = null;
      if (this.seChild != null && removedItemConsumer != null) {
         removedItemConsumer.accept(this.seChild.value);
      }

      this.seChild = null;
      if (this.swChild != null && removedItemConsumer != null) {
         removedItemConsumer.accept(this.swChild.value);
      }

      this.swChild = null;
   }

   @Override
   public String toString() {
      return "pos: " + DhSectionPos.toString(this.sectionPos) + ", children #: " + this.getTotalChildCount() + ", value: " + this.value;
   }
}

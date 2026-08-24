package com.seibel.distanthorizons.core.util.objects.quadTree.iterators;

import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadTree;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class QuadTreeNodeIterator<T> implements Iterator<QuadNode<T>> {
   private final byte leafDetailLevel;
   private final Queue<QuadNode<T>> validNodesForDetailLevel = new ArrayDeque<>();
   private final Queue<QuadNode<T>> iteratorNodeQueue = new ArrayDeque<>();
   private byte iteratorDetailLevel = 0;
   private final boolean onlyReturnLeafValues;
   @Nullable
   private final QuadTree.INodeIteratorStoppingFunc<T> stopIteratingFunc;

   public QuadTreeNodeIterator(QuadNode<T> rootNode, boolean onlyReturnLeafValues, @Nullable QuadTree.INodeIteratorStoppingFunc<T> stopIteratingFunc) {
      this.onlyReturnLeafValues = onlyReturnLeafValues;
      this.stopIteratingFunc = stopIteratingFunc;
      this.leafDetailLevel = rootNode.parentTreeLeafDetailLevel;
      this.iteratorDetailLevel = DhSectionPos.getDetailLevel(rootNode.sectionPos);
      if (!this.onlyReturnLeafValues) {
         this.validNodesForDetailLevel.add(rootNode);
         this.iteratorNodeQueue.add(rootNode);
      } else {
         Queue<QuadNode<T>> parentNodeQueue = new ArrayDeque<>();
         parentNodeQueue.add(rootNode);

         while (parentNodeQueue.peek() != null) {
            QuadNode<T> parentNode = parentNodeQueue.poll();
            if (stopIteratingFunc == null || !stopIteratingFunc.iteratorShouldStop(parentNode)) {
               for (int i = 0; i < 4; i++) {
                  QuadNode<T> childNode = parentNode.getChildByIndex(i);
                  if (childNode != null) {
                     if (childNode.getTotalChildCount() == 0) {
                        this.iteratorNodeQueue.add(childNode);
                     } else {
                        parentNodeQueue.add(childNode);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean hasNext() {
      return this.iteratorNodeQueue.size() != 0;
   }

   public QuadNode<T> next() {
      if (this.iteratorDetailLevel < this.leafDetailLevel) {
         throw new NoSuchElementException("Leaf detail level reached [" + this.leafDetailLevel + "].");
      } else if (this.iteratorNodeQueue.size() == 0) {
         throw new NoSuchElementException();
      } else {
         QuadNode<T> currentNode = this.iteratorNodeQueue.poll();
         if (this.iteratorNodeQueue.size() == 0 && !this.onlyReturnLeafValues) {
            this.iteratorDetailLevel--;
            if (this.iteratorDetailLevel >= this.leafDetailLevel) {
               Queue<QuadNode<T>> parentNodes = new LinkedList<>(this.validNodesForDetailLevel);
               this.validNodesForDetailLevel.clear();

               for (QuadNode<T> parentNode : parentNodes) {
                  if (this.stopIteratingFunc == null || !this.stopIteratingFunc.iteratorShouldStop(parentNode)) {
                     for (int i = 0; i < 4; i++) {
                        QuadNode<T> childNode = parentNode.getChildByIndex(i);
                        if (childNode != null && (this.stopIteratingFunc == null || !this.stopIteratingFunc.iteratorShouldStop(childNode))) {
                           this.iteratorNodeQueue.add(childNode);
                           this.validNodesForDetailLevel.add(childNode);
                        }
                     }
                  }
               }
            }
         }

         return currentNode;
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("remove");
   }

   @Override
   public void forEachRemaining(Consumer<? super QuadNode<T>> action) {
      Iterator.super.forEachRemaining(action);
   }
}

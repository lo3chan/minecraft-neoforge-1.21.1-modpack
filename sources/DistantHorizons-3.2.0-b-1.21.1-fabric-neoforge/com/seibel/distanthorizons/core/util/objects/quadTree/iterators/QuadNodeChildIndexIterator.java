package com.seibel.distanthorizons.core.util.objects.quadTree.iterators;

import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Consumer;

public class QuadNodeChildIndexIterator<T> implements Iterator<Integer> {
   private final Queue<Integer> iteratorQueue = new ArrayDeque<>();

   public QuadNodeChildIndexIterator(QuadNode<T> parentNode, boolean returnNullChildPos) {
      if (DhSectionPos.getDetailLevel(parentNode.sectionPos) > parentNode.parentTreeLeafDetailLevel) {
         for (int i = 0; i < 4; i++) {
            if (returnNullChildPos || parentNode.getChildByIndex(i) != null) {
               this.iteratorQueue.add(i);
            }
         }
      }
   }

   @Override
   public boolean hasNext() {
      return this.iteratorQueue.size() != 0;
   }

   public Integer next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.iteratorQueue.poll();
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("remove");
   }

   @Override
   public void forEachRemaining(Consumer<? super Integer> action) {
      Iterator.super.forEachRemaining(action);
   }
}

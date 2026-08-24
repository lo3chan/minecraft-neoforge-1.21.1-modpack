package com.seibel.distanthorizons.core.util.objects.quadTree.iterators;

import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

public class QuadNodeDirectChildPosIterator<T> implements LongIterator {
   private final QuadNodeChildIndexIterator<T> childIndexIterator;
   private final QuadNode<T> parentNode;

   public QuadNodeDirectChildPosIterator(QuadNode<T> parentNode) {
      this.parentNode = parentNode;
      this.childIndexIterator = new QuadNodeChildIndexIterator<>(this.parentNode, true);
   }

   public boolean hasNext() {
      return this.childIndexIterator.hasNext();
   }

   public long nextLong() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         int childIndex = this.childIndexIterator.next();
         return DhSectionPos.getChildByIndex(this.parentNode.sectionPos, childIndex);
      }
   }

   public void remove() {
      throw new UnsupportedOperationException("remove");
   }

   public void forEachRemaining(LongConsumer action) {
      super.forEachRemaining(action);
   }
}

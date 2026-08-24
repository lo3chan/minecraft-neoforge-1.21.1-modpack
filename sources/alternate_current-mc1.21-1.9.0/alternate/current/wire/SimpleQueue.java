package alternate.current.wire;

import java.util.AbstractQueue;
import java.util.Iterator;

public class SimpleQueue extends AbstractQueue<WireNode> {
   private WireNode head;
   private WireNode tail;
   private int size;

   SimpleQueue() {
   }

   public boolean offer(WireNode node) {
      if (node == null) {
         throw new NullPointerException();
      } else {
         if (this.tail == null) {
            this.head = this.tail = node;
         } else {
            this.tail.next_wire = node;
            this.tail = node;
         }

         this.size++;
         return true;
      }
   }

   public WireNode poll() {
      if (this.head == null) {
         return null;
      } else {
         WireNode node = this.head;
         WireNode next = node.next_wire;
         if (next == null) {
            this.head = this.tail = null;
         } else {
            node.next_wire = null;
            this.head = next;
         }

         this.size--;
         return node;
      }
   }

   public WireNode peek() {
      return this.head;
   }

   @Override
   public void clear() {
      WireNode node = this.head;

      while (node != null) {
         WireNode n = node;
         node = node.next_wire;
         n.next_wire = null;
      }

      this.head = null;
      this.tail = null;
      this.size = 0;
   }

   @Override
   public Iterator<WireNode> iterator() {
      return new SimpleQueue.SimpleIterator();
   }

   @Override
   public int size() {
      return this.size;
   }

   private class SimpleIterator implements Iterator<WireNode> {
      private WireNode curr;
      private WireNode next = SimpleQueue.this.head;

      @Override
      public boolean hasNext() {
         if (this.next == null && this.curr != null) {
            this.next = this.curr.next_wire;
         }

         return this.next != null;
      }

      public WireNode next() {
         this.curr = this.next;
         this.next = this.curr.next_wire;
         return this.curr;
      }
   }
}

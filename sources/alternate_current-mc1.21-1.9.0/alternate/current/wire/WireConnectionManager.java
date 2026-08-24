package alternate.current.wire;

import java.util.Arrays;
import java.util.function.Consumer;

public class WireConnectionManager {
   final WireNode owner;
   private final WireConnection[] heads;
   private WireConnection head;
   private WireConnection tail;
   int total;
   private int flowTotal;
   int iFlowDir;

   WireConnectionManager(WireNode owner) {
      this.owner = owner;
      this.heads = new WireConnection[WireHandler.Directions.HORIZONTAL.length];
      this.total = 0;
      this.flowTotal = 0;
      this.iFlowDir = -1;
   }

   void set(WireHandler.NodeProvider nodes) {
      if (this.total > 0) {
         this.clear();
      }

      boolean belowIsConductor = nodes.getNeighbor(this.owner, 4).isConductor();
      boolean aboveIsConductor = nodes.getNeighbor(this.owner, 5).isConductor();

      for (int iDir = 0; iDir < WireHandler.Directions.HORIZONTAL.length; iDir++) {
         Node neighbor = nodes.getNeighbor(this.owner, iDir);
         if (neighbor.isWire()) {
            this.add(neighbor.asWire(), iDir, true, true);
         } else {
            boolean sideIsConductor = neighbor.isConductor();
            if (!sideIsConductor) {
               Node node = nodes.getNeighbor(neighbor, 4);
               if (node.isWire()) {
                  this.add(node.asWire(), iDir, belowIsConductor, true);
               }
            }

            if (!aboveIsConductor) {
               Node node = nodes.getNeighbor(neighbor, 5);
               if (node.isWire()) {
                  this.add(node.asWire(), iDir, true, sideIsConductor);
               }
            }
         }
      }

      if (this.total > 0) {
         this.iFlowDir = WireHandler.FLOW_IN_TO_FLOW_OUT[this.flowTotal];
      }
   }

   private void clear() {
      Arrays.fill(this.heads, null);
      this.head = null;
      this.tail = null;
      this.total = 0;
      this.flowTotal = 0;
      this.iFlowDir = -1;
   }

   private void add(WireNode wire, int iDir, boolean offer, boolean accept) {
      this.add(new WireConnection(wire, iDir, offer, accept));
   }

   private void add(WireConnection connection) {
      if (this.head == null) {
         this.head = connection;
         this.tail = connection;
      } else {
         this.tail.next = connection;
         this.tail = connection;
      }

      this.total++;
      if (this.heads[connection.iDir] == null) {
         this.heads[connection.iDir] = connection;
         this.flowTotal = this.flowTotal | 1 << connection.iDir;
      }
   }

   void forEach(Consumer<WireConnection> consumer) {
      for (WireConnection c = this.head; c != null; c = c.next) {
         consumer.accept(c);
      }
   }

   void forEach(Consumer<WireConnection> consumer, UpdateOrder updateOrder, int iFlowDir) {
      for (int iDir : updateOrder.cardinalNeighbors(iFlowDir)) {
         for (WireConnection c = this.heads[iDir]; c != null && c.iDir == iDir; c = c.next) {
            consumer.accept(c);
         }
      }
   }
}

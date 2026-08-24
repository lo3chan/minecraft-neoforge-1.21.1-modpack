package at.petrak.hexcasting.api.casting.math

import kotlin.jvm.internal.markers.KMappedMarker

public data class HexCoord(q: Int, r: Int) {
   public final val q: Int
   public final val r: Int

   init {
      this.q = q;
      this.r = r;
   }

   public fun s(): Int {
      return -this.q - this.r;
   }

   public fun shiftedBy(x: HexCoord): HexCoord {
      return new HexCoord(this.q + x.q, this.r + x.r);
   }

   public fun shiftedBy(d: HexDir): HexCoord {
      return this.shiftedBy(d.asDelta());
   }

   public fun delta(x: HexCoord): HexCoord {
      return new HexCoord(this.q - x.q, this.r - x.r);
   }

   public operator fun plus(x: HexCoord): HexCoord {
      return this.shiftedBy(x);
   }

   public operator fun plus(d: HexDir): HexCoord {
      return this.shiftedBy(d);
   }

   public operator fun minus(x: HexCoord): HexCoord {
      return this.delta(x);
   }

   public fun distanceTo(x: HexCoord): Int {
      return (Math.abs(this.q - x.q) + Math.abs(this.q + this.r - x.q - x.r) + Math.abs(this.r - x.r)) / 2;
   }

   public fun rangeAround(radius: Int): Iterator<HexCoord> {
      return new HexCoord.RingIter(this, radius);
   }

   public fun immediateDelta(neighbor: HexCoord): HexDir? {
      val var2: HexCoord = neighbor.minus(this);
      return if (var2 == new HexCoord(1, 0))
         HexDir.EAST
         else
         (
            if (var2 == new HexCoord(0, 1))
               HexDir.SOUTH_EAST
               else
               (
                  if (var2 == new HexCoord(-1, 1))
                     HexDir.SOUTH_WEST
                     else
                     (
                        if (var2 == new HexCoord(-1, 0))
                           HexDir.WEST
                           else
                           (if (var2 == new HexCoord(0, -1)) HexDir.NORTH_WEST else (if (var2 == new HexCoord(1, -1)) HexDir.NORTH_EAST else null))
                     )
               )
         );
   }

   public operator fun component1(): Int {
      return this.q;
   }

   public operator fun component2(): Int {
      return this.r;
   }

   public fun copy(q: Int = this.q, r: Int = this.r): HexCoord {
      return new HexCoord(q, r);
   }

   public override fun toString(): String {
      return "HexCoord(q=${this.q}, r=${this.r})";
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.q) * 31 + Integer.hashCode(this.r);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is HexCoord) {
         return false;
      } else {
         val var2: HexCoord = other as HexCoord;
         if (this.q != (other as HexCoord).q) {
            return false;
         } else {
            return this.r == var2.r;
         }
      }
   }

   public companion object {
      @JvmStatic
      public final val Origin: HexCoord
   }

   private class RingIter(center: HexCoord, radius: Int) : java.util.Iterator<HexCoord>, KMappedMarker {
      public final val center: HexCoord
      public final val radius: Int

      public final var q: Int
         internal set

      public final var r: Int
         internal set

      init {
         this.center = center;
         this.radius = radius;
         this.q = -this.radius;
         this.r = Math.max(-this.radius, 0);
      }

      public override operator fun hasNext(): Boolean {
         return this.r <= this.radius + Math.min(0, -this.q) || this.q < this.radius;
      }

      public open operator fun next(): HexCoord {
         if (this.r > this.radius + Math.min(0, -this.q)) {
            val out: Int = this.q++;
            this.r = -this.radius + Math.max(0, -this.q);
         }

         val var3: HexCoord = new HexCoord(this.center.getQ() + this.q, this.center.getR() + this.r);
         val var2: Int = this.r++;
         return var3;
      }

      override fun remove() {
         throw new UnsupportedOperationException("Operation is not supported for read-only collection");
      }
   }
}

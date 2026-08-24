package at.petrak.hexcasting.api.casting

import at.petrak.hexcasting.api.casting.iota.Iota
import java.util.ArrayList
import kotlin.jvm.internal.markers.KMappedMarker

public sealed class SpellList protected constructor() : java.lang.Iterable<Iota>, KMappedMarker {
   public abstract val nonEmpty: Boolean
   public abstract val car: Iota
   public abstract val cdr: SpellList

   public fun modifyAt(startIdx: Int, modify: (SpellList) -> SpellList): SpellList {
      val stack: java.util.List = new ArrayList();
      val ptr: SpellList.SpellListIterator = this.iterator();
      var idx: Int = startIdx;
      if (startIdx < 0) {
         return this;
      } else {
         while (idx > 0) {
            if (!ptr.hasNext()) {
               return this;
            }

            idx--;
            stack.add(ptr.next());
         }

         var value: SpellList = modify.invoke(ptr.getList()) as SpellList;

         for (Iota datum : CollectionsKt.asReversedMutable(stack)) {
            value = new SpellList.LPair(datum, value);
         }

         return value;
      }
   }

   public fun getAt(startIdx: Int): Iota {
      var ptr: SpellList = this;
      var idx: Int = startIdx;
      if (startIdx < 0) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         while (idx > 0) {
            if (ptr !is SpellList.LPair) {
               if (ptr is SpellList.LList) {
                  return (ptr as SpellList.LList).getList().get((ptr as SpellList.LList).getIdx() + idx);
               }

               throw new NoWhenBranchMatchedException();
            }

            ptr = (ptr as SpellList.LPair).getCdr();
            idx--;
         }

         return ptr.getCar();
      }
   }

   public override fun toString(): String {
      return CollectionsKt.toList(this).toString();
   }

   public open operator fun iterator(): at.petrak.hexcasting.api.casting.SpellList.SpellListIterator {
      return new SpellList.SpellListIterator(this);
   }

   public fun size(): Int {
      var size: Int = 0;

      for (SpellList ptr = this; ptr.getNonEmpty(); size++) {
         ptr = ptr.getCdr();
      }

      return size;
   }

   public class LList(idx: Int, list: List<Iota>) : SpellList() {
      public final val idx: Int
      public final val list: List<Iota>

      public open val nonEmpty: Boolean
         public open get() {
            return this.idx < this.list.size();
         }


      public open val car: Iota
         public open get() {
            return this.list.get(this.idx);
         }


      public open val cdr: SpellList
         public open get() {
            return new SpellList.LList(this.idx + 1, this.list);
         }


      init {
         this.idx = idx;
         this.list = list;
      }

      public constructor(list: List<Iota>) : this(0, list)   }

   public class LPair(car: Iota, cdr: SpellList) : SpellList() {
      public open val car: Iota
      public open val cdr: SpellList
      public open val nonEmpty: Boolean

      init {
         this.car = car;
         this.cdr = cdr;
         this.nonEmpty = true;
      }
   }

   public class SpellListIterator(list: SpellList) : java.util.Iterator<Iota>, KMappedMarker {
      public final var list: SpellList
         internal set

      init {
         this.list = list;
      }

      public override operator fun hasNext(): Boolean {
         return this.list.getNonEmpty();
      }

      public open operator fun next(): Iota {
         this.list = this.list.getCdr();
         return this.list.getCar();
      }

      override fun remove() {
         throw new UnsupportedOperationException("Operation is not supported for read-only collection");
      }
   }
}

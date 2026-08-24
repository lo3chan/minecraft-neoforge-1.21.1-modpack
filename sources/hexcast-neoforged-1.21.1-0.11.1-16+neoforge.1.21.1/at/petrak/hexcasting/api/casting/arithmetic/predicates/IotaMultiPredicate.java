package at.petrak.hexcasting.api.casting.arithmetic.predicates;

import at.petrak.hexcasting.api.casting.iota.Iota;
import java.util.Iterator;

@FunctionalInterface
public interface IotaMultiPredicate {
   boolean test(Iterable<Iota> var1);

   static IotaMultiPredicate all(IotaPredicate child) {
      return new IotaMultiPredicate.All(child);
   }

   static IotaMultiPredicate pair(IotaPredicate first, IotaPredicate second) {
      return new IotaMultiPredicate.Pair(first, second);
   }

   static IotaMultiPredicate triple(IotaPredicate first, IotaPredicate second, IotaPredicate third) {
      return new IotaMultiPredicate.Triple(first, second, third);
   }

   static IotaMultiPredicate any(IotaPredicate needs, IotaPredicate fallback) {
      return new IotaMultiPredicate.Any(needs, fallback);
   }

   static IotaMultiPredicate either(IotaMultiPredicate first, IotaMultiPredicate second) {
      return new IotaMultiPredicate.Either(first, second);
   }

   public record All(IotaPredicate inner) implements IotaMultiPredicate {
      @Override
      public boolean test(Iterable<Iota> iotas) {
         for (Iota iota : iotas) {
            if (!this.inner.test(iota)) {
               return false;
            }
         }

         return true;
      }
   }

   public record Any(IotaPredicate needs, IotaPredicate fallback) implements IotaMultiPredicate {
      @Override
      public boolean test(Iterable<Iota> iotas) {
         boolean ok = false;

         for (Iota iota : iotas) {
            if (this.needs.test(iota)) {
               ok = true;
            } else if (!this.fallback.test(iota)) {
               return false;
            }
         }

         return ok;
      }
   }

   public record Either(IotaMultiPredicate first, IotaMultiPredicate second) implements IotaMultiPredicate {
      @Override
      public boolean test(Iterable<Iota> iotas) {
         return this.first.test(iotas) || this.second.test(iotas);
      }
   }

   public record Pair(IotaPredicate first, IotaPredicate second) implements IotaMultiPredicate {
      @Override
      public boolean test(Iterable<Iota> iotas) {
         Iterator<Iota> it = iotas.iterator();
         return it.hasNext() && this.first.test(it.next()) && it.hasNext() && this.second.test(it.next()) && !it.hasNext();
      }
   }

   public record Triple(IotaPredicate first, IotaPredicate second, IotaPredicate third) implements IotaMultiPredicate {
      @Override
      public boolean test(Iterable<Iota> iotas) {
         Iterator<Iota> it = iotas.iterator();
         return it.hasNext()
            && this.first.test(it.next())
            && it.hasNext()
            && this.second.test(it.next())
            && it.hasNext()
            && this.third.test(it.next())
            && !it.hasNext();
      }
   }
}

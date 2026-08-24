package at.petrak.hexcasting.api.casting.arithmetic.predicates;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import java.util.List;

@FunctionalInterface
public interface IotaPredicate {
   IotaPredicate TRUE = iota -> true;

   boolean test(Iota var1);

   static IotaPredicate or(IotaPredicate left, IotaPredicate right) {
      return new IotaPredicate.Or(left, right);
   }

   static IotaPredicate any(IotaPredicate... any) {
      return new IotaPredicate.Any(any);
   }

   static IotaPredicate any(List<IotaPredicate> any) {
      return new IotaPredicate.Any(any.toArray(IotaPredicate[]::new));
   }

   static IotaPredicate ofType(IotaType<?> type) {
      return new IotaPredicate.OfType(type);
   }

   public record Any(IotaPredicate[] any) implements IotaPredicate {
      @Override
      public boolean test(Iota iota) {
         for (IotaPredicate i : this.any) {
            if (i.test(iota)) {
               return true;
            }
         }

         return false;
      }
   }

   public record OfType(IotaType<?> type) implements IotaPredicate {
      @Override
      public boolean test(Iota iota) {
         return iota.getType().equals(this.type);
      }
   }

   public record Or(IotaPredicate left, IotaPredicate right) implements IotaPredicate {
      @Override
      public boolean test(Iota iota) {
         return this.left.test(iota) || this.right.test(iota);
      }
   }
}

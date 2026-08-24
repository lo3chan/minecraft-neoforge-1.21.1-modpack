package at.petrak.hexcasting.api.casting.arithmetic.operator;

import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import java.util.Iterator;
import java.util.List;
import java.util.function.BinaryOperator;
import org.jetbrains.annotations.NotNull;

public class OperatorBinary extends OperatorBasic {
   public BinaryOperator<Iota> inner;

   public OperatorBinary(IotaMultiPredicate accepts, BinaryOperator<Iota> inner) {
      super(2, accepts);
      this.inner = inner;
   }

   @NotNull
   @Override
   public Iterable<Iota> apply(Iterable<? extends Iota> iotas, @NotNull CastingEnvironment env) {
      Iterator<? extends Iota> it = iotas.iterator();
      return List.of(this.inner.apply(it.next(), it.next()));
   }
}

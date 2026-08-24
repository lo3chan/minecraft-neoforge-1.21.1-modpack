package at.petrak.hexcasting.api.casting.arithmetic.operator;

import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import java.util.List;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;

public class OperatorUnary extends OperatorBasic {
   public UnaryOperator<Iota> inner;

   public OperatorUnary(IotaMultiPredicate accepts, UnaryOperator<Iota> inner) {
      super(1, accepts);
      this.inner = inner;
   }

   @NotNull
   @Override
   public Iterable<Iota> apply(Iterable<? extends Iota> iotas, @NotNull CastingEnvironment env) {
      return List.of(this.inner.apply(iotas.iterator().next()));
   }
}

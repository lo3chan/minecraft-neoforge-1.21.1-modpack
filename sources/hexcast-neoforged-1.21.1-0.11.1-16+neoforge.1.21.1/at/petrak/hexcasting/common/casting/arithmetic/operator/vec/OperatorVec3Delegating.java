package at.petrak.hexcasting.common.casting.arithmetic.operator.vec;

import at.petrak.hexcasting.api.casting.arithmetic.IterPair;
import at.petrak.hexcasting.api.casting.arithmetic.TripleIterable;
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic;
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate;
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapDivideByZero;
import at.petrak.hexcasting.common.casting.arithmetic.DoubleArithmetic;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class OperatorVec3Delegating extends OperatorBasic {
   private final BiFunction<Vec3, Vec3, Iota> op;
   private final OperatorBasic fb;

   public OperatorVec3Delegating(BiFunction<Vec3, Vec3, Iota> core, HexPattern fallback) {
      super(2, IotaMultiPredicate.any(IotaPredicate.ofType(HexIotaTypes.VEC3), IotaPredicate.ofType(HexIotaTypes.DOUBLE)));
      this.op = core;
      this.fb = Objects.requireNonNull(DoubleArithmetic.INSTANCE.getOperator(fallback));
   }

   @NotNull
   @Override
   public Iterable<Iota> apply(Iterable<? extends Iota> iotas, @NotNull CastingEnvironment env) throws Mishap {
      Iterator<? extends Iota> it = iotas.iterator();
      Iota left = it.next();
      Iota right = it.next();

      try {
         if (this.op != null && left instanceof Vec3Iota lh && right instanceof Vec3Iota rh) {
            return List.of(this.op.apply(lh.getVec3(), rh.getVec3()));
         } else {
            Vec3 lh = left instanceof Vec3Iota l ? l.getVec3() : triplicate(downcast(left, HexIotaTypes.DOUBLE).getDouble());
            Vec3 rh = right instanceof Vec3Iota r ? r.getVec3() : triplicate(downcast(right, HexIotaTypes.DOUBLE).getDouble());
            return new TripleIterable<>(
               this.fb.apply(new IterPair<>(new DoubleIota(lh.x()), new DoubleIota(rh.x())), env),
               this.fb.apply(new IterPair<>(new DoubleIota(lh.y()), new DoubleIota(rh.y())), env),
               this.fb.apply(new IterPair<>(new DoubleIota(lh.z()), new DoubleIota(rh.z())), env),
               (x, y, z) -> new Vec3Iota(
                  new Vec3(
                     downcast(x, HexIotaTypes.DOUBLE).getDouble(), downcast(y, HexIotaTypes.DOUBLE).getDouble(), downcast(z, HexIotaTypes.DOUBLE).getDouble()
                  )
               )
            );
         }
      } catch (MishapDivideByZero var9) {
         throw MishapDivideByZero.of(left, right, var9.getSuffix());
      }
   }

   public static Vec3 triplicate(double in) {
      return new Vec3(in, in, in);
   }
}

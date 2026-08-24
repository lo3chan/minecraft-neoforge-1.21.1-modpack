package at.petrak.hexcasting.common.casting.arithmetic.operator

import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapDivideByZero
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt

@SourceDebugExtension(["SMAP\nOperatorLog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorLog.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/OperatorLog\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,23:1\n300#2:24\n*S KotlinDebug\n*F\n+ 1 OperatorLog.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/OperatorLog\n*L\n21#1:24\n*E\n"])
public object OperatorLog : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.DOUBLE));
      super(2, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val it: java.util.Iterator = CollectionsKt.withIndex(iotas.iterator());
      val value: Double = OperatorUtilsKt.nextDouble(it, this.arity);
      val base: Double = OperatorUtilsKt.nextDouble(it, this.arity);
      if (!(value <= 0.0) && !(base <= 0.0) && base != 1.0) {
         return CollectionsKt.listOf(new DoubleIota(MathKt.log(value, base)));
      } else {
         throw MishapDivideByZero.Companion.of(CollectionsKt.first(iotas) as Iota, CollectionsKt.last(iotas) as Iota, "logarithm");
      }
   }
}

package at.petrak.hexcasting.api.casting.arithmetic.operator

import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import kotlin.jvm.internal.SourceDebugExtension

public abstract class Operator {
   public final val arity: Int
   public final val accepts: IotaMultiPredicate

   open fun Operator(arity: Int, accepts: IotaMultiPredicate) {
      this.arity = arity;
      this.accepts = accepts;
   }

   @Throws(at/petrak/hexcasting/api/casting/mishaps/Mishap::class)
   public abstract fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
   }

   @SourceDebugExtension(["SMAP\nOperator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Operator.kt\nat/petrak/hexcasting/api/casting/arithmetic/operator/Operator$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,43:1\n1#2:44\n*E\n"])
   public companion object {
      public fun <T : Iota?> downcast(iota: Iota, iotaType: IotaType<T>): T {
         if (iota.getType() != iotaType) {
            throw new IllegalStateException(("Attempting to downcast $iota to type: $iotaType").toString());
         } else {
            return (T)iota;
         }
      }
   }
}

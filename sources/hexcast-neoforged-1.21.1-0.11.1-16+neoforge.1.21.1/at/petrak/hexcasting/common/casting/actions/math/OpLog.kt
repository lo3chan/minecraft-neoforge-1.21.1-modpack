package at.petrak.hexcasting.common.casting.actions.math

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapDivideByZero
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt

@SourceDebugExtension(["SMAP\nOpLog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpLog.kt\nat/petrak/hexcasting/common/casting/actions/math/OpLog\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,23:1\n300#2:24\n*S KotlinDebug\n*F\n+ 1 OpLog.kt\nat/petrak/hexcasting/common/casting/actions/math/OpLog\n*L\n20#1:24\n*E\n"])
public object OpLog : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val value: Double = OperatorUtils.getDouble(args, 0, this.getArgc());
      val base: Double = OperatorUtils.getDouble(args, 1, this.getArgc());
      if (!(value <= 0.0) && !(base <= 0.0) && base != 1.0) {
         return CollectionsKt.listOf(new DoubleIota(MathKt.log(value, base)));
      } else {
         throw MishapDivideByZero.Companion.of(args.get(0) as Iota, args.get(1) as Iota, "logarithm");
      }
   }

   override fun getMediaCost(): Long {
      return ConstMediaAction.DefaultImpls.getMediaCost(this);
   }

   override fun executeWithOpCount(args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
      return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
   }
}

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

@SourceDebugExtension(["SMAP\nOpModulo.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpModulo.kt\nat/petrak/hexcasting/common/casting/actions/math/OpModulo\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,23:1\n300#2:24\n*S KotlinDebug\n*F\n+ 1 OpModulo.kt\nat/petrak/hexcasting/common/casting/actions/math/OpModulo\n*L\n20#1:24\n*E\n"])
public object OpModulo : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val l: Double = OperatorUtils.getDouble(args, 0, this.getArgc());
      val r: Double = OperatorUtils.getDouble(args, 1, this.getArgc());
      if (r == 0.0) {
         throw MishapDivideByZero.Companion.of$default(MishapDivideByZero.Companion, args.get(0) as Iota, args.get(1) as Iota, null, 4, null);
      } else {
         return CollectionsKt.listOf(new DoubleIota(l % r));
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

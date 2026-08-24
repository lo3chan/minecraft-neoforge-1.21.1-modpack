package at.petrak.hexcasting.common.casting.actions.math.trig

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpArcTan2.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpArcTan2.kt\nat/petrak/hexcasting/common/casting/actions/math/trig/OpArcTan2\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,20:1\n300#2:21\n*S KotlinDebug\n*F\n+ 1 OpArcTan2.kt\nat/petrak/hexcasting/common/casting/actions/math/trig/OpArcTan2\n*L\n17#1:21\n*E\n"])
public object OpArcTan2 : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      return CollectionsKt.listOf(
         new DoubleIota(Math.atan2(OperatorUtils.getDouble(args, 0, this.getArgc()), OperatorUtils.getDouble(args, 1, this.getArgc())))
      );
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

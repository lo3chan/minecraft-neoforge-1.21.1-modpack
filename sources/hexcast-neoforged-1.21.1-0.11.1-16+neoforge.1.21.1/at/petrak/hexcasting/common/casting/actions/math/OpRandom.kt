package at.petrak.hexcasting.common.casting.actions.math

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpRandom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpRandom.kt\nat/petrak/hexcasting/common/casting/actions/math/OpRandom\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,16:1\n300#2:17\n*S KotlinDebug\n*F\n+ 1 OpRandom.kt\nat/petrak/hexcasting/common/casting/actions/math/OpRandom\n*L\n13#1:17\n*E\n"])
public object OpRandom : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 0;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      return CollectionsKt.listOf(new DoubleIota(env.getWorld().random.nextDouble()));
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

package at.petrak.hexcasting.common.casting.actions.lists

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpListSize.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpListSize.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpListSize\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,16:1\n301#2:17\n*S KotlinDebug\n*F\n+ 1 OpListSize.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpListSize\n*L\n13#1:17\n*E\n"])
public object OpListSize : ConstMediaAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      return CollectionsKt.listOf(new DoubleIota((double)CollectionsKt.toList(OperatorUtils.getList(args, 0, this.getArgc())).size()));
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

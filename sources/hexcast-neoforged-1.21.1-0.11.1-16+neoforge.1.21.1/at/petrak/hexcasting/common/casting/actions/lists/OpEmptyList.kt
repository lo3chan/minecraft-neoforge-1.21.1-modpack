package at.petrak.hexcasting.common.casting.actions.lists

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpEmptyList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpEmptyList.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpEmptyList\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,14:1\n304#2:15\n*S KotlinDebug\n*F\n+ 1 OpEmptyList.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpEmptyList\n*L\n11#1:15\n*E\n"])
public object OpEmptyList : ConstMediaAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      return CollectionsKt.listOf(new ListIota(CollectionsKt.emptyList()));
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

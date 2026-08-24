package at.petrak.hexcasting.common.casting.actions.lists

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpSlice.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpSlice.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpSlice\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,25:1\n304#2:26\n304#2:27\n*S KotlinDebug\n*F\n+ 1 OpSlice.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpSlice\n*L\n20#1:26\n22#1:27\n*E\n"])
public object OpSlice : ConstMediaAction {
   public open val argc: Int = 3

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val list: java.util.List = CollectionsKt.toList(OperatorUtils.getList(args, 0, this.getArgc()));
      val index1: Int = OperatorUtils.getPositiveIntUnderInclusive(args, 1, list.size(), this.getArgc());
      val index2: Int = OperatorUtils.getPositiveIntUnderInclusive(args, 2, list.size(), this.getArgc());
      return if (index1 == index2)
         CollectionsKt.listOf(new ListIota(CollectionsKt.emptyList()))
         else
         CollectionsKt.listOf(new ListIota(list.subList(Math.min(index1, index2), Math.max(index1, index2))));
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

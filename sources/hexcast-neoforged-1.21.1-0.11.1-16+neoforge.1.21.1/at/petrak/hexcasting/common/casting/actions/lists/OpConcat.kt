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

@SourceDebugExtension(["SMAP\nOpConcat.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpConcat.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpConcat\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,18:1\n304#2:19\n*S KotlinDebug\n*F\n+ 1 OpConcat.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpConcat\n*L\n15#1:19\n*E\n"])
public object OpConcat : ConstMediaAction {
   public open val argc: Int = 2

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val lhs: java.util.List = CollectionsKt.toMutableList(OperatorUtils.getList(args, 0, this.getArgc()));
      CollectionsKt.addAll(lhs, OperatorUtils.getList(args, 1, this.getArgc()));
      return CollectionsKt.listOf(new ListIota(lhs));
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

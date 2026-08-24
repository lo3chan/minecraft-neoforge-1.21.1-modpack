package at.petrak.hexcasting.common.casting.actions.lists

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpModifyInPlace.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpModifyInPlace.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpModifyInPlace\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,20:1\n303#2:21\n*S KotlinDebug\n*F\n+ 1 OpModifyInPlace.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpModifyInPlace\n*L\n17#1:21\n*E\n"])
public object OpModifyInPlace : ConstMediaAction {
   public open val argc: Int = 3

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val list: SpellList = OperatorUtils.getList(args, 0, this.getArgc());
      return CollectionsKt.listOf(
         new ListIota(list.modifyAt(OperatorUtils.getPositiveIntUnder(args, 1, list.size(), this.getArgc()), OpModifyInPlace::execute$lambda$0))
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

   @JvmStatic
   fun `execute$lambda$0`(`$iota`: Iota, it: SpellList): SpellList {
      return new SpellList.LPair(`$iota`, it.getCdr());
   }
}

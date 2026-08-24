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

@SourceDebugExtension(["SMAP\nOpRemove.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpRemove.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpRemove\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,23:1\n304#2:24\n304#2:25\n*S KotlinDebug\n*F\n+ 1 OpRemove.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpRemove\n*L\n18#1:24\n20#1:25\n*E\n"])
public object OpRemove : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val list: java.util.List = CollectionsKt.toMutableList(OperatorUtils.getList(args, 0, this.getArgc()));
      val index: Int = OperatorUtils.getInt(args, 1, this.getArgc());
      if (index >= 0 && index < list.size()) {
         list.remove(index);
         return CollectionsKt.listOf(new ListIota(list));
      } else {
         return CollectionsKt.listOf(new ListIota(list));
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

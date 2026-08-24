package at.petrak.hexcasting.common.casting.actions.lists

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt

@SourceDebugExtension(["SMAP\nOpIndex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpIndex.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpIndex\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,20:1\n1#2:21\n*E\n"])
public object OpIndex : ConstMediaAction {
   public open val argc: Int = 2

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val list: java.util.List = CollectionsKt.toMutableList(OperatorUtils.getList(args, 0, this.getArgc()));
      val var8: Int = MathKt.roundToInt(OperatorUtils.getDouble(args, 1, this.getArgc()));
      return CollectionsKt.listOf((if (0 <= var8 && var8 < list.size()) list.get(var8) else new NullIota()) as Iota);
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

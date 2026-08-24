package at.petrak.hexcasting.common.casting.actions.math.logic

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpEquality.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpEquality.kt\nat/petrak/hexcasting/common/casting/actions/math/logic/OpEquality\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,18:1\n299#2:19\n*S KotlinDebug\n*F\n+ 1 OpEquality.kt\nat/petrak/hexcasting/common/casting/actions/math/logic/OpEquality\n*L\n15#1:19\n*E\n"])
public class OpEquality(invert: Boolean) : ConstMediaAction {
   public final val invert: Boolean
   public open val argc: Int

   init {
      this.invert = invert;
      this.argc = 2;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      return CollectionsKt.listOf(new BooleanIota(Iota.tolerates(args.get(0) as Iota, args.get(1) as Iota) != this.invert));
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

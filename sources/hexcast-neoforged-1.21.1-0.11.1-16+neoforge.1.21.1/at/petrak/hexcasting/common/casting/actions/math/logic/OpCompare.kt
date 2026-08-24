package at.petrak.hexcasting.common.casting.actions.math.logic

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import java.util.function.BiPredicate
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpCompare.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpCompare.kt\nat/petrak/hexcasting/common/casting/actions/math/logic/OpCompare\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,23:1\n299#2:24\n299#2:25\n*S KotlinDebug\n*F\n+ 1 OpCompare.kt\nat/petrak/hexcasting/common/casting/actions/math/logic/OpCompare\n*L\n18#1:24\n20#1:25\n*E\n"])
public class OpCompare(acceptsEqual: Boolean, cmp: BiPredicate<Double, Double>) : ConstMediaAction {
   public final val acceptsEqual: Boolean
   public final val cmp: BiPredicate<Double, Double>
   public open val argc: Int

   init {
      this.acceptsEqual = acceptsEqual;
      this.cmp = cmp;
      this.argc = 2;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val lhs: Double = OperatorUtils.getDouble(args, 0, this.getArgc());
      val rhs: Double = OperatorUtils.getDouble(args, 1, this.getArgc());
      return if (DoubleIota.tolerates(lhs, rhs))
         CollectionsKt.listOf(new BooleanIota(this.acceptsEqual))
         else
         CollectionsKt.listOf(new BooleanIota(this.cmp.test(lhs, rhs)));
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

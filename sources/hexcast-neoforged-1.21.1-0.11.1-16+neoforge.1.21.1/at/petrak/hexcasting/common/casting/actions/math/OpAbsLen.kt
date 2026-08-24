package at.petrak.hexcasting.common.casting.actions.math

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpAbsLen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpAbsLen.kt\nat/petrak/hexcasting/common/casting/actions/math/OpAbsLen\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,20:1\n300#2:21\n*S KotlinDebug\n*F\n+ 1 OpAbsLen.kt\nat/petrak/hexcasting/common/casting/actions/math/OpAbsLen\n*L\n17#1:21\n*E\n"])
public object OpAbsLen : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 1;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val var10000: Any = OperatorUtils.getNumOrVec(args, 0, this.getArgc()).map(OpAbsLen::execute$lambda$1, OpAbsLen::execute$lambda$3);
      return CollectionsKt.listOf(new DoubleIota((var10000 as java.lang.Number).doubleValue()));
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
   fun `execute$lambda$0`(num: java.lang.Double): java.lang.Double {
      return Math.abs(num);
   }

   @JvmStatic
   fun `execute$lambda$1`(`$tmp0`: Function1, p0: Any): java.lang.Double {
      return `$tmp0`.invoke(p0) as java.lang.Double;
   }

   @JvmStatic
   fun `execute$lambda$2`(vec: Vec3): java.lang.Double {
      return vec.length();
   }

   @JvmStatic
   fun `execute$lambda$3`(`$tmp0`: Function1, p0: Any): java.lang.Double {
      return `$tmp0`.invoke(p0) as java.lang.Double;
   }
}

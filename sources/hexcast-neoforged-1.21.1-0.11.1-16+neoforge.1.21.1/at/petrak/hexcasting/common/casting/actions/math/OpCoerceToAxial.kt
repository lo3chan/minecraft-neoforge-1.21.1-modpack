package at.petrak.hexcasting.common.casting.actions.math

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpCoerceToAxial.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpCoerceToAxial.kt\nat/petrak/hexcasting/common/casting/actions/math/OpCoerceToAxial\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,28:1\n300#2:29\n308#2:30\n308#2:31\n*S KotlinDebug\n*F\n+ 1 OpCoerceToAxial.kt\nat/petrak/hexcasting/common/casting/actions/math/OpCoerceToAxial\n*L\n19#1:29\n22#1:30\n24#1:31\n*E\n"])
public object OpCoerceToAxial : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 1;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val var4: Any = OperatorUtils.getNumOrVec(args, 0, this.getArgc()).map(OpCoerceToAxial::execute$lambda$1, OpCoerceToAxial::execute$lambda$3);
      return var4 as MutableList<Iota>;
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
   fun `execute$lambda$0`(num: java.lang.Double): java.util.List {
      return CollectionsKt.listOf(new DoubleIota(Math.signum(num)));
   }

   @JvmStatic
   fun `execute$lambda$1`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$2`(vec: Vec3): java.util.List {
      val var10000: java.util.List;
      if (vec == Vec3.ZERO) {
         var10000 = CollectionsKt.listOf(new Vec3Iota(vec));
      } else {
         val var4: Vec3 = Vec3.atLowerCornerOf(Direction.getNearest(vec.x, vec.y, vec.z).getNormal());
         var10000 = CollectionsKt.listOf(new Vec3Iota(var4));
      }

      return var10000;
   }

   @JvmStatic
   fun `execute$lambda$3`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }
}

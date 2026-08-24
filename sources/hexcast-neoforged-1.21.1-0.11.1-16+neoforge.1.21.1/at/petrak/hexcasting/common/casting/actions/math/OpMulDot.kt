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
import com.mojang.datafixers.util.Either
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpMulDot.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpMulDot.kt\nat/petrak/hexcasting/common/casting/actions/math/OpMulDot\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,30:1\n300#2,9:31\n308#2:40\n300#2:41\n*S KotlinDebug\n*F\n+ 1 OpMulDot.kt\nat/petrak/hexcasting/common/casting/actions/math/OpMulDot\n*L\n20#1:31,9\n24#1:40\n24#1:41\n*E\n"])
public object OpMulDot : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val lhs: Either = OperatorUtils.getNumOrVec(args, 0, OpAdd.INSTANCE.getArgc());
      val rhs: Either = OperatorUtils.getNumOrVec(args, 1, OpAdd.INSTANCE.getArgc());
      val var5: Any = lhs.map(OpMulDot::execute$lambda$5, OpMulDot::execute$lambda$11);
      return var5 as MutableList<Iota>;
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
   fun `execute$lambda$4$lambda$0`(`$lnum`: java.lang.Double, rnum: java.lang.Double): java.util.List {
      val var10000: Double = `$lnum`;
      return CollectionsKt.listOf(new DoubleIota(var10000 * rnum));
   }

   @JvmStatic
   fun `execute$lambda$4$lambda$1`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$4$lambda$2`(`$lnum`: java.lang.Double, rvec: Vec3): java.util.List {
      val var10000: Vec3 = rvec.scale(`$lnum`);
      return CollectionsKt.listOf(new Vec3Iota(var10000));
   }

   @JvmStatic
   fun `execute$lambda$4$lambda$3`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$4`(`$rhs`: Either, lnum: java.lang.Double): java.util.List {
      return `$rhs`.map(OpMulDot::execute$lambda$4$lambda$1, OpMulDot::execute$lambda$4$lambda$3) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$5`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$6`(`$lvec`: Vec3, rnum: java.lang.Double): java.util.List {
      val var10000: Vec3 = `$lvec`.scale(rnum);
      return CollectionsKt.listOf(new Vec3Iota(var10000));
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$7`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$8`(`$lvec`: Vec3, rvec: Vec3): java.util.List {
      return CollectionsKt.listOf(new DoubleIota(`$lvec`.dot(rvec)));
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$9`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10`(`$rhs`: Either, lvec: Vec3): java.util.List {
      return `$rhs`.map(OpMulDot::execute$lambda$10$lambda$7, OpMulDot::execute$lambda$10$lambda$9) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$11`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }
}

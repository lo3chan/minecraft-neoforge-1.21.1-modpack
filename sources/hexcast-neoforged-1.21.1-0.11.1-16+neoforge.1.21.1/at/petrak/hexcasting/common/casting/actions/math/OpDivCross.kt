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
import at.petrak.hexcasting.api.casting.mishaps.MishapDivideByZero
import com.mojang.datafixers.util.Either
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpDivCross.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpDivCross.kt\nat/petrak/hexcasting/common/casting/actions/math/OpDivCross\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,43:1\n300#2:44\n308#2:45\n308#2:46\n308#2:47\n*S KotlinDebug\n*F\n+ 1 OpDivCross.kt\nat/petrak/hexcasting/common/casting/actions/math/OpDivCross\n*L\n25#1:44\n29#1:45\n36#1:46\n38#1:47\n*E\n"])
public object OpDivCross : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val lhs: Either = OperatorUtils.getNumOrVec(args, 0, this.getArgc());
      val rhs: Either = OperatorUtils.getNumOrVec(args, 1, this.getArgc());
      val theMishap: MishapDivideByZero = MishapDivideByZero.Companion.of$default(
         MishapDivideByZero.Companion, args.get(0) as Iota, args.get(1) as Iota, null, 4, null
      );
      val var6: Any = lhs.map(OpDivCross::execute$lambda$5, OpDivCross::execute$lambda$11);
      return var6 as MutableList<Iota>;
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
   fun `execute$lambda$4$lambda$0`(`$theMishap`: MishapDivideByZero, `$lnum`: java.lang.Double, rnum: java.lang.Double): java.util.List {
      if (rnum == 0.0) {
         throw `$theMishap`;
      } else {
         val var10000: Double = `$lnum`;
         return CollectionsKt.listOf(new DoubleIota(var10000 / rnum));
      }
   }

   @JvmStatic
   fun `execute$lambda$4$lambda$1`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$4$lambda$2`(`$theMishap`: MishapDivideByZero, `$lnum`: java.lang.Double, rvec: Vec3): java.util.List {
      if (rvec.x != 0.0 && rvec.y != 0.0 && rvec.z != 0.0) {
         return CollectionsKt.listOf(new Vec3Iota(new Vec3(`$lnum` / rvec.x, `$lnum` / rvec.y, `$lnum` / rvec.z)));
      } else {
         throw `$theMishap`;
      }
   }

   @JvmStatic
   fun `execute$lambda$4$lambda$3`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$4`(`$rhs`: Either, `$theMishap`: MishapDivideByZero, lnum: java.lang.Double): java.util.List {
      return `$rhs`.map(OpDivCross::execute$lambda$4$lambda$1, OpDivCross::execute$lambda$4$lambda$3) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$5`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$6`(`$lvec`: Vec3, `$theMishap`: MishapDivideByZero, rnum: java.lang.Double): java.util.List {
      if (`$lvec` == Vec3.ZERO) {
         throw `$theMishap`;
      } else {
         val var10000: Vec3 = `$lvec`.scale(1.0 / rnum);
         return CollectionsKt.listOf(new Vec3Iota(var10000));
      }
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$7`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$8`(`$lvec`: Vec3, rvec: Vec3): java.util.List {
      val var10000: Vec3 = `$lvec`.cross(rvec);
      return CollectionsKt.listOf(new Vec3Iota(var10000));
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$9`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10`(`$rhs`: Either, `$theMishap`: MishapDivideByZero, lvec: Vec3): java.util.List {
      return `$rhs`.map(OpDivCross::execute$lambda$10$lambda$7, OpDivCross::execute$lambda$10$lambda$9) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$11`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }
}

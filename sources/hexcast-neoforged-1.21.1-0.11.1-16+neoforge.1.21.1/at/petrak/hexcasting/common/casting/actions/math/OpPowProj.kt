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

@SourceDebugExtension(["SMAP\nOpPowProj.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpPowProj.kt\nat/petrak/hexcasting/common/casting/actions/math/OpPowProj\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,50:1\n300#2:51\n308#2:52\n308#2:53\n308#2:54\n*S KotlinDebug\n*F\n+ 1 OpPowProj.kt\nat/petrak/hexcasting/common/casting/actions/math/OpPowProj\n*L\n27#1:51\n31#1:52\n39#1:53\n44#1:54\n*E\n"])
public object OpPowProj : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val lhs: Either = OperatorUtils.getNumOrVec(args, 0, OpAdd.INSTANCE.getArgc());
      val rhs: Either = OperatorUtils.getNumOrVec(args, 1, OpAdd.INSTANCE.getArgc());
      val theMishap: MishapDivideByZero = MishapDivideByZero.Companion.of(args.get(0) as Iota, args.get(1) as Iota, "exponent");
      val var6: Any = lhs.map(OpPowProj::execute$lambda$5, OpPowProj::execute$lambda$11);
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
   fun `execute$lambda$4$lambda$0`(`$lnum`: java.lang.Double, `$theMishap`: MishapDivideByZero, rnum: java.lang.Double): java.util.List {
      if (rnum == 0.0 && `$lnum` == 0.0) {
         throw `$theMishap`;
      } else {
         val var10000: Double = `$lnum`;
         return CollectionsKt.listOf(new DoubleIota(Math.pow(var10000, rnum)));
      }
   }

   @JvmStatic
   fun `execute$lambda$4$lambda$1`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$4$lambda$2`(`$lnum`: java.lang.Double, `$theMishap`: MishapDivideByZero, rvec: Vec3): java.util.List {
      if (!(`$lnum` == 0.0) || rvec.x != 0.0 && rvec.y != 0.0 && rvec.z != 0.0) {
         return CollectionsKt.listOf(new Vec3Iota(new Vec3(Math.pow(`$lnum`, rvec.x), Math.pow(`$lnum`, rvec.y), Math.pow(`$lnum`, rvec.z))));
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
      return `$rhs`.map(OpPowProj::execute$lambda$4$lambda$1, OpPowProj::execute$lambda$4$lambda$3) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$5`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$6`(`$lvec`: Vec3, `$theMishap`: MishapDivideByZero, rnum: java.lang.Double): java.util.List {
      if (!(rnum == 0.0) || `$lvec`.x != 0.0 && `$lvec`.y != 0.0 && `$lvec`.z != 0.0) {
         val var10002: Double = `$lvec`.x;
         return CollectionsKt.listOf(new Vec3Iota(new Vec3(Math.pow(var10002, rnum), Math.pow(`$lvec`.y, rnum), Math.pow(`$lvec`.z, rnum))));
      } else {
         throw `$theMishap`;
      }
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$7`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$8`(`$lvec`: Vec3, `$args`: java.util.List, rvec: Vec3): java.util.List {
      if (`$lvec` == Vec3.ZERO) {
         throw MishapDivideByZero.Companion.of(`$args`.get(0) as Iota, `$args`.get(1) as Iota, "project");
      } else {
         val var10000: Vec3 = `$lvec`.scale(rvec.dot(`$lvec`) / `$lvec`.dot(`$lvec`));
         return CollectionsKt.listOf(new Vec3Iota(var10000));
      }
   }

   @JvmStatic
   fun `execute$lambda$10$lambda$9`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$10`(`$rhs`: Either, `$theMishap`: MishapDivideByZero, `$args`: java.util.List, lvec: Vec3): java.util.List {
      return `$rhs`.map(OpPowProj::execute$lambda$10$lambda$7, OpPowProj::execute$lambda$10$lambda$9) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$11`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }
}

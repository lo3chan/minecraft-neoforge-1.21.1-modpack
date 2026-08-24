package at.petrak.hexcasting.api.casting.arithmetic.operator

import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOperatorBasic.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorBasic.kt\nat/petrak/hexcasting/api/casting/arithmetic/operator/OperatorBasic\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,39:1\n1#2:40\n*E\n"])
public abstract class OperatorBasic : Operator {
   open fun OperatorBasic(arity: Int, accepts: IotaMultiPredicate) {
      super(arity, accepts);
   }

   @Throws(at/petrak/hexcasting/api/casting/mishaps/Mishap::class)
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      val args: java.util.List = CollectionsKt.takeLast(stack, this.arity);
      val ret: Int = this.arity;

      for (int image2 = 0; image2 < ret; image2++) {
         stack.removeLast();
      }

      this.apply(args, env).forEach(OperatorBasic::operate$lambda$1);
      val var11: CastingImage = CastingImage.copy$default(image, stack, 0, null, false, image.getOpsConsumed() + 1L, null, 46, null);
      val var10003: java.util.List = CollectionsKt.emptyList();
      val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
      return new OperationResult(var11, var10003, continuation, var10005);
   }

   @Throws(at/petrak/hexcasting/api/casting/mishaps/Mishap::class)
   public abstract fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
   }

   @JvmStatic
   fun `operate$lambda$1`(`$stack`: java.util.List, e: Iota) {
      `$stack`.add(e);
   }
}

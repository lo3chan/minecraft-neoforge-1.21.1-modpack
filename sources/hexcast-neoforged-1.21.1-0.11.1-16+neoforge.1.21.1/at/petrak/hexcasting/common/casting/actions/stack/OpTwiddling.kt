package at.petrak.hexcasting.common.casting.actions.stack

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpTwiddling.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpTwiddling.kt\nat/petrak/hexcasting/common/casting/actions/stack/OpTwiddling\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,13:1\n11258#2:14\n11593#2,3:15\n*S KotlinDebug\n*F\n+ 1 OpTwiddling.kt\nat/petrak/hexcasting/common/casting/actions/stack/OpTwiddling\n*L\n12#1:14\n12#1:15,3\n*E\n"])
public class OpTwiddling(argumentCount: Int, lookup: IntArray) : ConstMediaAction {
   public final val argumentCount: Int
   public final val lookup: IntArray

   public open val argc: Int
      public open get() {
         return this.argumentCount;
      }


   init {
      this.argumentCount = argumentCount;
      this.lookup = lookup;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val `$this$map$iv`: IntArray = this.lookup;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(this.lookup.length);

      for (int item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(args.get(`item$iv$iv`) as Iota);
      }

      return `destination$iv$iv` as MutableList<Iota>;
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

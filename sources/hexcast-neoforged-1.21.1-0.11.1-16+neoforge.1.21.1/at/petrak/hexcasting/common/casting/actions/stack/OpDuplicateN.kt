package at.petrak.hexcasting.common.casting.actions.stack

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapShameOnYou
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpDuplicateN.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpDuplicateN.kt\nat/petrak/hexcasting/common/casting/actions/stack/OpDuplicateN\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,23:1\n1#2:24\n*E\n"])
public object OpDuplicateN : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val count: Int = OperatorUtils.getPositiveInt(args, 1, this.getArgc());
      if (count > 1000) {
         throw new MishapShameOnYou();
      } else {
         val var4: ArrayList = new ArrayList(count);

         for (int var5 = 0; var5 < count; var5++) {
            var4.add(args.get(0) as Iota);
         }

         return var4;
      }
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

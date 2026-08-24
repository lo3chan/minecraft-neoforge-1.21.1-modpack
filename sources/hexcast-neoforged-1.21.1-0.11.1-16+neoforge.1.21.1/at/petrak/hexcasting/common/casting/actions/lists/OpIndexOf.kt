package at.petrak.hexcasting.common.casting.actions.lists

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpIndexOf.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpIndexOf.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpIndexOf\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,19:1\n360#2,7:20\n301#3:27\n*S KotlinDebug\n*F\n+ 1 OpIndexOf.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpIndexOf\n*L\n16#1:20,7\n16#1:27\n*E\n"])
public object OpIndexOf : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val list: java.util.List = CollectionsKt.toMutableList(OperatorUtils.getList(args, 0, this.getArgc()));
      val value: Iota = args.get(1) as Iota;
      var `index$iv`: Int = 0;
      val var8: java.util.Iterator = list.iterator();

      var var10000: Int;
      while (true) {
         if (!var8.hasNext()) {
            var10000 = -1;
            break;
         }

         if (Iota.tolerates(value, var8.next() as Iota)) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return CollectionsKt.listOf(new DoubleIota((double)var10000));
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

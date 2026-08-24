package at.petrak.hexcasting.common.casting.actions.math.bit

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import java.util.ArrayList
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpOr.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpOr.kt\nat/petrak/hexcasting/common/casting/actions/math/bit/OpOr\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,29:1\n301#2:30\n304#2:37\n774#3:31\n865#3:32\n2746#3,3:33\n866#3:36\n*S KotlinDebug\n*F\n+ 1 OpOr.kt\nat/petrak/hexcasting/common/casting/actions/math/bit/OpOr\n*L\n20#1:30\n24#1:37\n24#1:31\n24#1:32\n24#1:33,3\n24#1:36\n*E\n"])
public object OpOr : ConstMediaAction {
   public open val argc: Int = 2

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val var4: Any = OperatorUtils.getLongOrList(args, 0, this.getArgc()).map(OpOr::execute$lambda$1, OpOr::execute$lambda$5);
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
   fun `execute$lambda$0`(`$args`: java.util.List, num1: java.lang.Long): java.util.List {
      return CollectionsKt.listOf(new DoubleIota((double)(num1 or OperatorUtils.getLong(`$args`, 1, INSTANCE.getArgc()))));
   }

   @JvmStatic
   fun `execute$lambda$1`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$4`(`$args`: java.util.List, list1: SpellList): java.util.List {
      val list2: SpellList = OperatorUtils.getList(`$args`, 1, INSTANCE.getArgc());
      val var10000: java.lang.Iterable = list1;
      val `$this$asActionResult$iv`: java.lang.Iterable = list2;
      val `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$asActionResult$iv) {
         val x: Iota = `element$iv$iv` as Iota;
         val `$this$none$iv`: java.lang.Iterable = list1;
         var var21: Boolean;
         if (list1 is java.util.Collection && (list1 as java.util.Collection).isEmpty()) {
            var21 = true;
         } else {
            val var14: java.util.Iterator = `$this$none$iv`.iterator();

            while (true) {
               if (!var14.hasNext()) {
                  var21 = true;
                  break;
               }

               if (Iota.tolerates(x, var14.next() as Iota)) {
                  var21 = false;
                  break;
               }
            }
         }

         if (var21) {
            `destination$iv$iv`.add(`element$iv$iv`);
         }
      }

      return CollectionsKt.listOf(new ListIota(CollectionsKt.plus(var10000, `destination$iv$iv` as java.util.List)));
   }

   @JvmStatic
   fun `execute$lambda$5`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }
}

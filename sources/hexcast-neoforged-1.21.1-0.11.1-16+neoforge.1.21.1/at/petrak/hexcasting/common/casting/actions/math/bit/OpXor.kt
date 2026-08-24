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

@SourceDebugExtension(["SMAP\nOpXor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpXor.kt\nat/petrak/hexcasting/common/casting/actions/math/bit/OpXor\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,38:1\n301#2:39\n304#2:52\n774#3:40\n865#3:41\n2746#3,3:42\n866#3:45\n774#3:46\n865#3:47\n2746#3,3:48\n866#3:51\n*S KotlinDebug\n*F\n+ 1 OpXor.kt\nat/petrak/hexcasting/common/casting/actions/math/bit/OpXor\n*L\n20#1:39\n33#1:52\n25#1:40\n25#1:41\n26#1:42,3\n25#1:45\n32#1:46\n32#1:47\n32#1:48,3\n32#1:51\n*E\n"])
public object OpXor : ConstMediaAction {
   public open val argc: Int = 2

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val var4: Any = OperatorUtils.getLongOrList(args, 0, this.getArgc()).map(OpXor::execute$lambda$1, OpXor::execute$lambda$7);
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
      return CollectionsKt.listOf(new DoubleIota((double)(num1 xor OperatorUtils.getLong(`$args`, 1, INSTANCE.getArgc()))));
   }

   @JvmStatic
   fun `execute$lambda$1`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }

   @JvmStatic
   fun `execute$lambda$6`(`$args`: java.util.List, list1: SpellList): java.util.List {
      val list2: SpellList = OperatorUtils.getList(`$args`, 1, INSTANCE.getArgc());
      var `$this$asActionResult$iv`: java.lang.Iterable = list1;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         val x2: Iota = `element$iv$iv` as Iota;
         val `$this$none$iv`: java.lang.Iterable = list2;
         var var10000: Boolean;
         if (list2 is java.util.Collection && (list2 as java.util.Collection).isEmpty()) {
            var10000 = true;
         } else {
            val var15: java.util.Iterator = `$this$none$iv`.iterator();

            while (true) {
               if (!var15.hasNext()) {
                  var10000 = true;
                  break;
               }

               if (Iota.tolerates(x2, var15.next() as Iota)) {
                  var10000 = false;
                  break;
               }
            }
         }

         if (var10000) {
            `destination$iv$iv`.add(`element$iv$iv`);
         }
      }

      val var35: java.util.Collection = `destination$iv$iv` as java.util.List;
      `$this$asActionResult$iv` = list2;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         val var27: Iota = var26 as Iota;
         val `$this$none$ivx`: java.lang.Iterable = list1;
         var var36: Boolean;
         if (list1 is java.util.Collection && (list1 as java.util.Collection).isEmpty()) {
            var36 = true;
         } else {
            val var31: java.util.Iterator = `$this$none$ivx`.iterator();

            while (true) {
               if (!var31.hasNext()) {
                  var36 = true;
                  break;
               }

               if (Iota.tolerates(var27, var31.next() as Iota)) {
                  var36 = false;
                  break;
               }
            }
         }

         if (var36) {
            `destination$iv$iv`.add(var26);
         }
      }

      return CollectionsKt.listOf(new ListIota(CollectionsKt.plus(var35, `destination$iv$iv` as java.util.List)));
   }

   @JvmStatic
   fun `execute$lambda$7`(`$tmp0`: Function1, p0: Any): java.util.List {
      return `$tmp0`.invoke(p0) as java.util.List;
   }
}

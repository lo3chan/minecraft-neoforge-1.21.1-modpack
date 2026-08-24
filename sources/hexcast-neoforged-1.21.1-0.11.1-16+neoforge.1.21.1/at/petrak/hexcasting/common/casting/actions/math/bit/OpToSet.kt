package at.petrak.hexcasting.common.casting.actions.math.bit

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpToSet.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpToSet.kt\nat/petrak/hexcasting/common/casting/actions/math/bit/OpToSet\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,25:1\n2746#2,3:26\n304#3:29\n*S KotlinDebug\n*F\n+ 1 OpToSet.kt\nat/petrak/hexcasting/common/casting/actions/math/bit/OpToSet\n*L\n17#1:26,3\n22#1:29\n*E\n"])
public object OpToSet : ConstMediaAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val list: SpellList = OperatorUtils.getList(args, 0, this.getArgc());
      val out: java.util.List = new ArrayList();
      val `$this$asActionResult$iv`: SpellList.SpellListIterator = list.iterator();

      while ($this$asActionResult$iv.hasNext()) {
         val `$i$f$getAsActionResult`: Iota = `$this$asActionResult$iv`.next();
         val `$this$none$iv`: java.lang.Iterable = out;
         var var10000: Boolean;
         if (out is java.util.Collection && (out as java.util.Collection).isEmpty()) {
            var10000 = true;
         } else {
            val var9: java.util.Iterator = `$this$none$iv`.iterator();

            while (true) {
               if (!var9.hasNext()) {
                  var10000 = true;
                  break;
               }

               if (Iota.tolerates(var9.next() as Iota, `$i$f$getAsActionResult`)) {
                  var10000 = false;
                  break;
               }
            }
         }

         if (var10000) {
            out.add(`$i$f$getAsActionResult`);
         }
      }

      return CollectionsKt.listOf(new ListIota(out));
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

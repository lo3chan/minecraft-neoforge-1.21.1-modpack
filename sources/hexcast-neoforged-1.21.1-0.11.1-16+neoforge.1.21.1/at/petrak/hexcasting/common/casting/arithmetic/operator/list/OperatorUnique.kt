package at.petrak.hexcasting.common.casting.arithmetic.operator.list

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.casting.actions.math.bit.OpToSet
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOperatorUnique.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorUnique.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorUnique\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,28:1\n2746#2,3:29\n304#3:32\n*S KotlinDebug\n*F\n+ 1 OperatorUnique.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorUnique\n*L\n21#1:29,3\n26#1:32\n*E\n"])
public object OperatorUnique : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.LIST));
      super(1, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val list: SpellList = OperatorUtilsKt.nextList(CollectionsKt.withIndex(iotas.iterator()), OpToSet.INSTANCE.getArgc());
      val out: java.util.List = new ArrayList();
      val `$this$asActionResult$iv`: SpellList.SpellListIterator = list.iterator();

      while ($this$asActionResult$iv.hasNext()) {
         val `$i$f$getAsActionResult`: Iota = `$this$asActionResult$iv`.next();
         val `$this$none$iv`: java.lang.Iterable = out;
         var var10000: Boolean;
         if (out is java.util.Collection && (out as java.util.Collection).isEmpty()) {
            var10000 = true;
         } else {
            val var10: java.util.Iterator = `$this$none$iv`.iterator();

            while (true) {
               if (!var10.hasNext()) {
                  var10000 = true;
                  break;
               }

               if (Iota.tolerates(var10.next() as Iota, `$i$f$getAsActionResult`)) {
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
}

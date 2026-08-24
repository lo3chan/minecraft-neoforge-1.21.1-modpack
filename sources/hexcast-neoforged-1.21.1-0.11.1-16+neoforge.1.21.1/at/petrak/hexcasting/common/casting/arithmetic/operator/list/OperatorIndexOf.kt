package at.petrak.hexcasting.common.casting.arithmetic.operator.list

import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOperatorIndexOf.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorIndexOf.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorIndexOf\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,20:1\n360#2,7:21\n301#3:28\n*S KotlinDebug\n*F\n+ 1 OperatorIndexOf.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorIndexOf\n*L\n18#1:21,7\n18#1:28\n*E\n"])
public object OperatorIndexOf : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.pair(IotaPredicate.ofType(HexIotaTypes.LIST), IotaPredicate.TRUE);
      super(2, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val it: java.util.Iterator = CollectionsKt.withIndex(iotas.iterator());
      val list: java.util.List = CollectionsKt.toList(OperatorUtilsKt.nextList(it, this.arity));
      val toFind: Iota = (it.next() as IndexedValue).getValue() as Iota;
      var `index$iv`: Int = 0;
      val var9: java.util.Iterator = list.iterator();

      var var10000: Int;
      while (true) {
         if (!var9.hasNext()) {
            var10000 = -1;
            break;
         }

         if (Iota.tolerates(toFind, var9.next() as Iota)) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return CollectionsKt.listOf(new DoubleIota((double)var10000));
   }
}

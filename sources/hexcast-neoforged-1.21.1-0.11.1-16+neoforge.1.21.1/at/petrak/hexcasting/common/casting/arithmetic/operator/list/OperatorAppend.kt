package at.petrak.hexcasting.common.casting.arithmetic.operator.list

import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOperatorAppend.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorAppend.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorAppend\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,20:1\n304#2:21\n*S KotlinDebug\n*F\n+ 1 OperatorAppend.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorAppend\n*L\n18#1:21\n*E\n"])
public object OperatorAppend : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.pair(IotaPredicate.ofType(HexIotaTypes.LIST), IotaPredicate.TRUE);
      super(2, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val it: java.util.Iterator = CollectionsKt.withIndex(iotas.iterator());
      val list: java.util.List = CollectionsKt.toMutableList(OperatorUtilsKt.nextList(it, this.arity));
      list.add((it.next() as IndexedValue).getValue());
      return CollectionsKt.listOf(new ListIota(list));
   }
}

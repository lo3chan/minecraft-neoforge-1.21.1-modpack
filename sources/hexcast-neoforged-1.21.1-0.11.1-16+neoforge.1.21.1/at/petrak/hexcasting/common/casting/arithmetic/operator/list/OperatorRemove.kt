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

@SourceDebugExtension(["SMAP\nOperatorRemove.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorRemove.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorRemove\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,24:1\n304#2:25\n304#2:26\n*S KotlinDebug\n*F\n+ 1 OperatorRemove.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorRemove\n*L\n20#1:25\n22#1:26\n*E\n"])
public object OperatorRemove : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.pair(IotaPredicate.ofType(HexIotaTypes.LIST), IotaPredicate.ofType(HexIotaTypes.DOUBLE));
      super(2, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val it: java.util.Iterator = CollectionsKt.withIndex(iotas.iterator());
      val list: java.util.List = CollectionsKt.toMutableList(OperatorUtilsKt.nextList(it, this.arity));
      val index: Int = OperatorUtilsKt.nextInt(it, this.arity);
      if (index >= 0 && index < list.size()) {
         list.remove(index);
         return CollectionsKt.listOf(new ListIota(list));
      } else {
         return CollectionsKt.listOf(new ListIota(list));
      }
   }
}

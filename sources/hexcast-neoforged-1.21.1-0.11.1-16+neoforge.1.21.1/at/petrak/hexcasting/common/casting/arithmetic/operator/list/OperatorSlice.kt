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

@SourceDebugExtension(["SMAP\nOperatorSlice.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorSlice.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorSlice\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,27:1\n304#2:28\n304#2:29\n*S KotlinDebug\n*F\n+ 1 OperatorSlice.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorSlice\n*L\n24#1:28\n25#1:29\n*E\n"])
public object OperatorSlice : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.triple(
         IotaPredicate.ofType(HexIotaTypes.LIST), IotaPredicate.ofType(HexIotaTypes.DOUBLE), IotaPredicate.ofType(HexIotaTypes.DOUBLE)
      );
      super(3, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val it: java.util.Iterator = CollectionsKt.withIndex(iotas.iterator());
      val list: java.util.List = CollectionsKt.toList(OperatorUtilsKt.nextList(it, this.arity));
      val index0: Int = OperatorUtilsKt.nextPositiveIntUnderInclusive(it, list.size(), this.arity);
      val index1: Int = OperatorUtilsKt.nextPositiveIntUnderInclusive(it, list.size(), this.arity);
      return if (index0 == index1)
         CollectionsKt.listOf(new ListIota(CollectionsKt.emptyList()))
         else
         CollectionsKt.listOf(new ListIota(list.subList(Math.min(index0, index1), Math.max(index0, index1))));
   }
}

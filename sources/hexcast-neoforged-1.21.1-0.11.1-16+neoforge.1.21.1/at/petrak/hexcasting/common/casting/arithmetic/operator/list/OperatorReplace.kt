package at.petrak.hexcasting.common.casting.arithmetic.operator.list

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOperatorReplace.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorReplace.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorReplace\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,23:1\n303#2:24\n*S KotlinDebug\n*F\n+ 1 OperatorReplace.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorReplace\n*L\n21#1:24\n*E\n"])
public object OperatorReplace : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.triple(
         IotaPredicate.ofType(HexIotaTypes.LIST), IotaPredicate.ofType(HexIotaTypes.DOUBLE), IotaPredicate.TRUE
      );
      super(3, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val it: java.util.Iterator = CollectionsKt.withIndex(iotas.iterator());
      val list: SpellList = OperatorUtilsKt.nextList(it, this.arity);
      return CollectionsKt.listOf(
         new ListIota(list.modifyAt(OperatorUtilsKt.nextPositiveIntUnder(it, list.size(), this.arity), OperatorReplace::apply$lambda$0))
      );
   }

   @JvmStatic
   fun `apply$lambda$0`(`$iota`: Iota, it: SpellList): SpellList {
      return new SpellList.LPair(`$iota`, it.getCdr());
   }
}

package at.petrak.hexcasting.common.casting.arithmetic.operator.list

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt

@SourceDebugExtension(["SMAP\nOperatorIndex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorIndex.kt\nat/petrak/hexcasting/common/casting/arithmetic/operator/list/OperatorIndex\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,22:1\n1#2:23\n*E\n"])
public object OperatorIndex : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.pair(IotaPredicate.ofType(HexIotaTypes.LIST), IotaPredicate.ofType(HexIotaTypes.DOUBLE));
      super(2, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val it: java.util.Iterator = iotas.iterator();
      var var10000: Operator.Companion = Operator.Companion;
      var var10001: Iota = it.next() as Iota;
      var var10002: IotaType = HexIotaTypes.LIST;
      val var12: SpellList = var10000.<ListIota>downcast(var10001, var10002).getList();
      val list: java.util.List = CollectionsKt.toMutableList(var12);
      var10000 = Operator.Companion;
      var10001 = it.next() as Iota;
      var10002 = HexIotaTypes.DOUBLE;
      val var9: Int = MathKt.roundToInt(var10000.<DoubleIota>downcast(var10001, var10002).getDouble());
      return CollectionsKt.listOf((if (0 <= var9 && var9 < list.size()) list.get(var9) else new NullIota()) as Iota);
   }
}

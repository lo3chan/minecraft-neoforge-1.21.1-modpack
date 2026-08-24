package at.petrak.hexcasting.common.casting.arithmetic.operator.list

import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes

public object OperatorUnappend : OperatorBasic {
   init {
      val var10002: IotaMultiPredicate = IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.LIST));
      super(1, var10002);
   }

   public override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
      val list: java.util.List = CollectionsKt.toMutableList(OperatorUtilsKt.nextList(CollectionsKt.withIndex(iotas.iterator()), this.arity));
      var var10000: Iota = CollectionsKt.removeLastOrNull(list) as Iota;
      if (var10000 == null) {
         var10000 = new NullIota();
      }

      return CollectionsKt.listOf(new Iota[]{new ListIota(list), var10000});
   }
}

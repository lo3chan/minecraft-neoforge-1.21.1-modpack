package at.petrak.hexcasting.common.casting.arithmetic

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.arithmetic.engine.InvalidOperatorException
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBinary
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorUnary
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorAppend
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorIndex
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorIndexOf
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorRemove
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorReplace
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorSlice
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorUnCons
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorUnappend
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import java.util.function.BinaryOperator

public object ListArithmetic : Arithmetic {
   private final val OPS: List<HexPattern> =
      CollectionsKt.listOf(
         new HexPattern[]{
            Arithmetic.INDEX,
            Arithmetic.SLICE,
            Arithmetic.APPEND,
            Arithmetic.UNAPPEND,
            Arithmetic.ADD,
            Arithmetic.ABS,
            Arithmetic.REV,
            Arithmetic.INDEX_OF,
            Arithmetic.REMOVE,
            Arithmetic.REPLACE,
            Arithmetic.CONS,
            Arithmetic.UNCONS
         }
      )

   public override fun arithName(): String {
      return "list_ops";
   }

   public override fun opTypes(): Iterable<HexPattern> {
      return OPS;
   }

   public override fun getOperator(pattern: HexPattern): Operator {
      val var10000: Operator;
      if (pattern == Arithmetic.INDEX) {
         var10000 = OperatorIndex.INSTANCE;
      } else if (pattern == Arithmetic.SLICE) {
         var10000 = OperatorSlice.INSTANCE;
      } else if (pattern == Arithmetic.APPEND) {
         var10000 = OperatorAppend.INSTANCE;
      } else if (pattern == Arithmetic.UNAPPEND) {
         var10000 = OperatorUnappend.INSTANCE;
      } else if (pattern == Arithmetic.ADD) {
         var10000 = this.make2(ListArithmetic::getOperator$lambda$0);
      } else if (pattern == Arithmetic.ABS) {
         var10000 = new OperatorUnary(IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.LIST)), ListArithmetic::getOperator$lambda$1);
      } else if (pattern == Arithmetic.REV) {
         var10000 = new OperatorUnary(IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.LIST)), ListArithmetic::getOperator$lambda$2);
      } else if (pattern == Arithmetic.INDEX_OF) {
         var10000 = OperatorIndexOf.INSTANCE;
      } else if (pattern == Arithmetic.REMOVE) {
         var10000 = OperatorRemove.INSTANCE;
      } else if (pattern == Arithmetic.REPLACE) {
         var10000 = OperatorReplace.INSTANCE;
      } else if (pattern == Arithmetic.CONS) {
         var10000 = new OperatorBinary(
            IotaMultiPredicate.pair(IotaPredicate.ofType(HexIotaTypes.LIST), IotaPredicate.TRUE), ListArithmetic::getOperator$lambda$3
         );
      } else {
         if (!(pattern == Arithmetic.UNCONS)) {
            throw new InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.");
         }

         var10000 = OperatorUnCons.INSTANCE;
      }

      return var10000;
   }

   private fun make2(op: BinaryOperator<List<Iota>>): OperatorBinary {
      return new OperatorBinary(IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.LIST)), ListArithmetic::make2$lambda$4);
   }

   @JvmStatic
   fun `getOperator$lambda$0`(list0: java.util.List, list1: java.util.List): java.util.List {
      return CollectionsKt.plus(list0, list1);
   }

   @JvmStatic
   fun `getOperator$lambda$1`(iota: Iota): Iota {
      val var10002: Operator.Companion = Operator.Companion;
      val var10004: IotaType = HexIotaTypes.LIST;
      return new DoubleIota(var10002.<ListIota>downcast(iota, var10004).getList().size());
   }

   @JvmStatic
   fun `getOperator$lambda$2`(iota: Iota): Iota {
      val var10002: Operator.Companion = Operator.Companion;
      val var10004: IotaType = HexIotaTypes.LIST;
      val var1: SpellList = var10002.<ListIota>downcast(iota, var10004).getList();
      return new ListIota(CollectionsKt.asReversed(CollectionsKt.toList(var1)));
   }

   @JvmStatic
   fun `getOperator$lambda$3`(list: Iota, iota: Iota): Iota {
      val var10005: Operator.Companion = Operator.Companion;
      val var10007: IotaType = HexIotaTypes.LIST;
      val var2: SpellList = var10005.<ListIota>downcast(list, var10007).getList();
      return new ListIota(new SpellList.LPair(iota, var2));
   }

   @JvmStatic
   fun `make2$lambda$4`(`$op`: BinaryOperator, i: Iota, j: Iota): Iota {
      val var10003: Operator.Companion = Operator.Companion;
      val var10005: IotaType = HexIotaTypes.LIST;
      val var3: SpellList = var10003.<ListIota>downcast(i, var10005).getList();
      val var4: java.util.List = CollectionsKt.toList(var3);
      val var10004: Operator.Companion = Operator.Companion;
      val var10006: IotaType = HexIotaTypes.LIST;
      val var5: SpellList = var10004.<ListIota>downcast(j, var10006).getList();
      return new ListIota(`$op`.apply(var4, CollectionsKt.toList(var5)));
   }
}

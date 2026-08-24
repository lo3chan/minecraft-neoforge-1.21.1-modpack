package at.petrak.hexcasting.common.casting.arithmetic

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.arithmetic.engine.InvalidOperatorException
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBinary
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.casting.arithmetic.operator.list.OperatorUnique
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import java.util.ArrayList
import java.util.function.BinaryOperator
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nListSetArithmetic.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListSetArithmetic.kt\nat/petrak/hexcasting/common/casting/arithmetic/ListSetArithmetic\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,43:1\n774#2:44\n865#2:45\n1761#2,3:46\n866#2:49\n774#2:50\n865#2:51\n2746#2,3:52\n866#2:55\n774#2:56\n865#2:57\n2746#2,3:58\n866#2:61\n865#2:62\n2746#2,3:63\n866#2:66\n*S KotlinDebug\n*F\n+ 1 ListSetArithmetic.kt\nat/petrak/hexcasting/common/casting/arithmetic/ListSetArithmetic\n*L\n31#1:44\n31#1:45\n31#1:46,3\n31#1:49\n32#1:50\n32#1:51\n32#1:52,3\n32#1:55\n33#1:56\n33#1:57\n33#1:58,3\n33#1:61\n33#1:62\n33#1:63,3\n33#1:66\n*E\n"])
public object ListSetArithmetic : Arithmetic {
   private final val OPS: List<HexPattern> = CollectionsKt.listOf(new HexPattern[]{Arithmetic.AND, Arithmetic.OR, Arithmetic.XOR, Arithmetic.UNIQUE})

   public override fun arithName(): String {
      return "list_set_ops";
   }

   public open fun opTypes(): List<HexPattern> {
      return OPS;
   }

   public override fun getOperator(pattern: HexPattern): Operator {
      val var10000: Operator;
      if (pattern == Arithmetic.AND) {
         var10000 = this.make2(ListSetArithmetic::getOperator$lambda$2);
      } else if (pattern == Arithmetic.OR) {
         var10000 = this.make2(ListSetArithmetic::getOperator$lambda$5);
      } else if (pattern == Arithmetic.XOR) {
         var10000 = this.make2(ListSetArithmetic::getOperator$lambda$10);
      } else {
         if (!(pattern == Arithmetic.UNIQUE)) {
            throw new InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.");
         }

         var10000 = OperatorUnique.INSTANCE;
      }

      return var10000;
   }

   private fun make2(op: BinaryOperator<List<Iota>>): OperatorBinary {
      return new OperatorBinary(IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.LIST)), ListSetArithmetic::make2$lambda$11);
   }

   @JvmStatic
   fun `getOperator$lambda$2`(list0: java.util.List, list1: java.util.List): java.util.List {
      val `$this$filter$iv`: java.lang.Iterable = list0;
      val `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         val x: Iota = `element$iv$iv` as Iota;
         val `$this$any$iv`: java.lang.Iterable = list1;
         var var10000: Boolean;
         if (list1 is java.util.Collection && (list1 as java.util.Collection).isEmpty()) {
            var10000 = false;
         } else {
            val var13: java.util.Iterator = `$this$any$iv`.iterator();

            while (true) {
               if (!var13.hasNext()) {
                  var10000 = false;
                  break;
               }

               if (Iota.tolerates(x, var13.next() as Iota)) {
                  var10000 = true;
                  break;
               }
            }
         }

         if (var10000) {
            `destination$iv$iv`.add(`element$iv$iv`);
         }
      }

      return `destination$iv$iv` as java.util.List;
   }

   @JvmStatic
   fun `getOperator$lambda$5`(list0: java.util.List, list1: java.util.List): java.util.List {
      val var10000: java.util.Collection = list0;
      val `$this$filter$iv`: java.lang.Iterable = list1;
      val `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         val x: Iota = `element$iv$iv` as Iota;
         val `$this$none$iv`: java.lang.Iterable = list0;
         var var18: Boolean;
         if (list0 is java.util.Collection && (list0 as java.util.Collection).isEmpty()) {
            var18 = true;
         } else {
            val var13: java.util.Iterator = `$this$none$iv`.iterator();

            while (true) {
               if (!var13.hasNext()) {
                  var18 = true;
                  break;
               }

               if (Iota.tolerates(x, var13.next() as Iota)) {
                  var18 = false;
                  break;
               }
            }
         }

         if (var18) {
            `destination$iv$iv`.add(`element$iv$iv`);
         }
      }

      return CollectionsKt.plus(var10000, `destination$iv$iv` as java.util.List);
   }

   @JvmStatic
   fun `getOperator$lambda$10`(list0: java.util.List, list1: java.util.List): java.util.List {
      var `$this$filter$iv`: java.lang.Iterable = list0;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         val x1: Iota = `element$iv$iv` as Iota;
         val `$this$none$iv`: java.lang.Iterable = list1;
         var var10000: Boolean;
         if (list1 is java.util.Collection && (list1 as java.util.Collection).isEmpty()) {
            var10000 = true;
         } else {
            val var13: java.util.Iterator = `$this$none$iv`.iterator();

            while (true) {
               if (!var13.hasNext()) {
                  var10000 = true;
                  break;
               }

               if (Iota.tolerates(x1, var13.next() as Iota)) {
                  var10000 = false;
                  break;
               }
            }
         }

         if (var10000) {
            `destination$iv$iv`.add(`element$iv$iv`);
         }
      }

      val var32: java.util.Collection = `destination$iv$iv` as java.util.List;
      `$this$filter$iv` = list1;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         val var24: Iota = var23 as Iota;
         val `$this$none$ivx`: java.lang.Iterable = list0;
         var var33: Boolean;
         if (list0 is java.util.Collection && (list0 as java.util.Collection).isEmpty()) {
            var33 = true;
         } else {
            val var28: java.util.Iterator = `$this$none$ivx`.iterator();

            while (true) {
               if (!var28.hasNext()) {
                  var33 = true;
                  break;
               }

               if (Iota.tolerates(var24, var28.next() as Iota)) {
                  var33 = false;
                  break;
               }
            }
         }

         if (var33) {
            `destination$iv$iv`.add(var23);
         }
      }

      return CollectionsKt.plus(var32, `destination$iv$iv` as java.util.List);
   }

   @JvmStatic
   fun `make2$lambda$11`(`$op`: BinaryOperator, i: Iota, j: Iota): Iota {
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

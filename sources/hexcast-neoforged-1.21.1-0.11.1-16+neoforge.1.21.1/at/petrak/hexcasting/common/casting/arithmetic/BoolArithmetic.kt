package at.petrak.hexcasting.common.casting.arithmetic

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.arithmetic.engine.InvalidOperatorException
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBinary
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorUnary
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import it.unimi.dsi.fastutil.booleans.BooleanBinaryOperator
import it.unimi.dsi.fastutil.booleans.BooleanUnaryOperator
import java.util.function.BiFunction

public object BoolArithmetic : Arithmetic {
   private final val OPS: List<HexPattern> =
      CollectionsKt.listOf(
         new HexPattern[]{
            Arithmetic.AND,
            Arithmetic.OR,
            Arithmetic.XOR,
            Arithmetic.GREATER,
            Arithmetic.LESS,
            Arithmetic.GREATER_EQ,
            Arithmetic.LESS_EQ,
            Arithmetic.NOT,
            Arithmetic.ABS
         }
      )
      public final val ALL_BOOLS: IotaMultiPredicate

   public override fun arithName(): String {
      return "bool_math";
   }

   public open fun opTypes(): List<HexPattern> {
      return OPS;
   }

   public override fun getOperator(pattern: HexPattern): Operator {
      val var10000: Operator;
      if (pattern == Arithmetic.AND) {
         var10000 = this.make2(BoolArithmetic::getOperator$lambda$0);
      } else if (pattern == Arithmetic.OR) {
         var10000 = this.make2(BoolArithmetic::getOperator$lambda$1);
      } else if (pattern == Arithmetic.XOR) {
         var10000 = this.make2(BoolArithmetic::getOperator$lambda$2);
      } else if (pattern == Arithmetic.GREATER) {
         var10000 = this.makeComp(BoolArithmetic::getOperator$lambda$3);
      } else if (pattern == Arithmetic.LESS) {
         var10000 = this.makeComp(BoolArithmetic::getOperator$lambda$4);
      } else if (pattern == Arithmetic.GREATER_EQ) {
         var10000 = this.makeComp(BoolArithmetic::getOperator$lambda$5);
      } else if (pattern == Arithmetic.LESS_EQ) {
         var10000 = this.makeComp(BoolArithmetic::getOperator$lambda$6);
      } else if (pattern == Arithmetic.NOT) {
         var10000 = this.make1(BoolArithmetic::getOperator$lambda$7);
      } else {
         if (!(pattern == Arithmetic.ABS)) {
            throw new InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.");
         }

         var10000 = new OperatorUnary(ALL_BOOLS, BoolArithmetic::getOperator$lambda$8);
      }

      return var10000;
   }

   private fun make1(op: BooleanUnaryOperator): OperatorUnary {
      return new OperatorUnary(ALL_BOOLS, BoolArithmetic::make1$lambda$9);
   }

   private fun make2(op: BooleanBinaryOperator): OperatorBinary {
      return new OperatorBinary(ALL_BOOLS, BoolArithmetic::make2$lambda$10);
   }

   private fun makeComp(op: BiFunction<Double, Double, Boolean>): OperatorBinary {
      return new OperatorBinary(DoubleArithmetic.INSTANCE.getACCEPTS(), BoolArithmetic::makeComp$lambda$11);
   }

   @JvmStatic
   fun `getOperator$lambda$0`(a: Boolean, b: Boolean): Boolean {
      return a and b;
   }

   @JvmStatic
   fun `getOperator$lambda$1`(a: Boolean, b: Boolean): Boolean {
      return a or b;
   }

   @JvmStatic
   fun `getOperator$lambda$2`(a: Boolean, b: Boolean): Boolean {
      return a xor b;
   }

   @JvmStatic
   fun `getOperator$lambda$3`(x: java.lang.Double, y: java.lang.Double): java.lang.Boolean {
      return x > y;
   }

   @JvmStatic
   fun `getOperator$lambda$4`(x: java.lang.Double, y: java.lang.Double): java.lang.Boolean {
      return x < y;
   }

   @JvmStatic
   fun `getOperator$lambda$5`(x: java.lang.Double, y: java.lang.Double): java.lang.Boolean {
      return DoubleIota.tolerates(x, y) || x >= y;
   }

   @JvmStatic
   fun `getOperator$lambda$6`(x: java.lang.Double, y: java.lang.Double): java.lang.Boolean {
      return DoubleIota.tolerates(x, y) || x <= y;
   }

   @JvmStatic
   fun `getOperator$lambda$7`(a: Boolean): Boolean {
      return !a;
   }

   @JvmStatic
   fun `getOperator$lambda$8`(i: Iota): Iota {
      val var10002: Operator.Companion = Operator.Companion;
      val var10004: IotaType = HexIotaTypes.BOOLEAN;
      return new DoubleIota(if (var10002.<BooleanIota>downcast(i, var10004).getBool()) 1.0 else 0.0);
   }

   @JvmStatic
   fun `make1$lambda$9`(`$op`: BooleanUnaryOperator, i: Iota): Iota {
      val var10003: Operator.Companion = Operator.Companion;
      val var10005: IotaType = HexIotaTypes.BOOLEAN;
      return new BooleanIota(`$op`.apply(var10003.<BooleanIota>downcast(i, var10005).getBool()));
   }

   @JvmStatic
   fun `make2$lambda$10`(`$op`: BooleanBinaryOperator, i: Iota, j: Iota): Iota {
      val var10003: Operator.Companion = Operator.Companion;
      val var10005: IotaType = HexIotaTypes.BOOLEAN;
      val var3: Boolean = var10003.<BooleanIota>downcast(i, var10005).getBool();
      val var10004: Operator.Companion = Operator.Companion;
      val var10006: IotaType = HexIotaTypes.BOOLEAN;
      return new BooleanIota(`$op`.apply(var3, var10004.<BooleanIota>downcast(j, var10006).getBool()));
   }

   @JvmStatic
   fun `makeComp$lambda$11`(`$op`: BiFunction, i: Iota, j: Iota): Iota {
      val var10003: Operator.Companion = Operator.Companion;
      val var10005: IotaType = HexIotaTypes.DOUBLE;
      val var3: java.lang.Double = var10003.<DoubleIota>downcast(i, var10005).getDouble();
      val var10004: Operator.Companion = Operator.Companion;
      val var10006: IotaType = HexIotaTypes.DOUBLE;
      val var10002: Any = `$op`.apply(var3, var10004.<DoubleIota>downcast(j, var10006).getDouble());
      return new BooleanIota(var10002 as java.lang.Boolean);
   }

   @JvmStatic
   fun {
      val var10000: IotaMultiPredicate = IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.BOOLEAN));
      ALL_BOOLS = var10000;
   }
}

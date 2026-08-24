package at.petrak.hexcasting.common.casting.arithmetic

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
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapDivideByZero
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorLog
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import java.util.function.DoubleBinaryOperator
import java.util.function.DoubleUnaryOperator

public object DoubleArithmetic : Arithmetic {
   public final val OPS: List<HexPattern> =
      CollectionsKt.listOf(
         new HexPattern[]{
            Arithmetic.ADD,
            Arithmetic.SUB,
            Arithmetic.MUL,
            Arithmetic.DIV,
            Arithmetic.ABS,
            Arithmetic.POW,
            Arithmetic.FLOOR,
            Arithmetic.CEIL,
            Arithmetic.SIN,
            Arithmetic.COS,
            Arithmetic.TAN,
            Arithmetic.ARCSIN,
            Arithmetic.ARCCOS,
            Arithmetic.ARCTAN,
            Arithmetic.ARCTAN2,
            Arithmetic.LOG,
            Arithmetic.MOD
         }
      )
      public final val ACCEPTS: IotaMultiPredicate

   public override fun arithName(): String {
      return "double_math";
   }

   public open fun opTypes(): List<HexPattern> {
      return OPS;
   }

   public override fun getOperator(pattern: HexPattern): Operator {
      val var10000: Operator;
      if (pattern == Arithmetic.ADD) {
         var10000 = this.make2(DoubleArithmetic::getOperator$lambda$0);
      } else if (pattern == Arithmetic.SUB) {
         var10000 = this.make2(DoubleArithmetic::getOperator$lambda$1);
      } else if (pattern == Arithmetic.MUL) {
         var10000 = this.make2(DoubleArithmetic::getOperator$lambda$2);
      } else if (pattern == Arithmetic.DIV) {
         var10000 = this.make2(DoubleArithmetic::getOperator$lambda$3);
      } else if (pattern == Arithmetic.ABS) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$4);
      } else if (pattern == Arithmetic.POW) {
         var10000 = this.make2(DoubleArithmetic::getOperator$lambda$5);
      } else if (pattern == Arithmetic.FLOOR) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$6);
      } else if (pattern == Arithmetic.CEIL) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$7);
      } else if (pattern == Arithmetic.SIN) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$8);
      } else if (pattern == Arithmetic.COS) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$9);
      } else if (pattern == Arithmetic.TAN) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$10);
      } else if (pattern == Arithmetic.ARCSIN) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$11);
      } else if (pattern == Arithmetic.ARCCOS) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$12);
      } else if (pattern == Arithmetic.ARCTAN) {
         var10000 = this.make1(DoubleArithmetic::getOperator$lambda$13);
      } else if (pattern == Arithmetic.ARCTAN2) {
         var10000 = this.make2(DoubleArithmetic::getOperator$lambda$14);
      } else if (pattern == Arithmetic.LOG) {
         var10000 = OperatorLog.INSTANCE;
      } else {
         if (!(pattern == Arithmetic.MOD)) {
            throw new InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.");
         }

         var10000 = this.make2(DoubleArithmetic::getOperator$lambda$15);
      }

      return var10000;
   }

   public fun make1(op: DoubleUnaryOperator): OperatorUnary {
      return new OperatorUnary(ACCEPTS, DoubleArithmetic::make1$lambda$16);
   }

   public fun make2(op: DoubleBinaryOperator): OperatorBinary {
      return new OperatorBinary(ACCEPTS, DoubleArithmetic::make2$lambda$17);
   }

   @JvmStatic
   fun `getOperator$lambda$0`(a: Double, b: Double): Double {
      return a + b;
   }

   @JvmStatic
   fun `getOperator$lambda$1`(a: Double, b: Double): Double {
      return a - b;
   }

   @JvmStatic
   fun `getOperator$lambda$2`(a: Double, b: Double): Double {
      return a * b;
   }

   @JvmStatic
   fun `getOperator$lambda$3`(a: Double, b: Double): Double {
      if (b == 0.0) {
         throw MishapDivideByZero.Companion.of$default(MishapDivideByZero.Companion, a, b, null, 4, null);
      } else {
         return a / b;
      }
   }

   @JvmStatic
   fun `getOperator$lambda$4`(a: Double): Double {
      return Math.abs(a);
   }

   @JvmStatic
   fun `getOperator$lambda$5`(a: Double, b: Double): Double {
      return Math.pow(a, b);
   }

   @JvmStatic
   fun `getOperator$lambda$6`(a: Double): Double {
      return Math.floor(a);
   }

   @JvmStatic
   fun `getOperator$lambda$7`(a: Double): Double {
      return Math.ceil(a);
   }

   @JvmStatic
   fun `getOperator$lambda$8`(a: Double): Double {
      return Math.sin(a);
   }

   @JvmStatic
   fun `getOperator$lambda$9`(a: Double): Double {
      return Math.cos(a);
   }

   @JvmStatic
   fun `getOperator$lambda$10`(a: Double): Double {
      if (Math.cos(a) == 0.0) {
         throw MishapDivideByZero.Companion.tan(a);
      } else {
         return Math.tan(a);
      }
   }

   @JvmStatic
   fun `getOperator$lambda$11`(a: Double): Double {
      return Math.asin(OperatorUtilsKt.asDoubleBetween(a, -1.0, 1.0, 0));
   }

   @JvmStatic
   fun `getOperator$lambda$12`(a: Double): Double {
      return Math.acos(OperatorUtilsKt.asDoubleBetween(a, -1.0, 1.0, 0));
   }

   @JvmStatic
   fun `getOperator$lambda$13`(a: Double): Double {
      return Math.atan(a);
   }

   @JvmStatic
   fun `getOperator$lambda$14`(a: Double, b: Double): Double {
      return Math.atan2(a, b);
   }

   @JvmStatic
   fun `getOperator$lambda$15`(a: Double, b: Double): Double {
      if (b == 0.0) {
         throw MishapDivideByZero.Companion.of$default(MishapDivideByZero.Companion, a, b, null, 4, null);
      } else {
         return a % b;
      }
   }

   @JvmStatic
   fun `make1$lambda$16`(`$op`: DoubleUnaryOperator, i: Iota): Iota {
      val var10003: Operator.Companion = Operator.Companion;
      val var10005: IotaType = HexIotaTypes.DOUBLE;
      return new DoubleIota(`$op`.applyAsDouble(var10003.<DoubleIota>downcast(i, var10005).getDouble()));
   }

   @JvmStatic
   fun `make2$lambda$17`(`$op`: DoubleBinaryOperator, i: Iota, j: Iota): Iota {
      val var10003: Operator.Companion = Operator.Companion;
      val var10005: IotaType = HexIotaTypes.DOUBLE;
      val var3: Double = var10003.<DoubleIota>downcast(i, var10005).getDouble();
      val var10004: Operator.Companion = Operator.Companion;
      val var10006: IotaType = HexIotaTypes.DOUBLE;
      return new DoubleIota(`$op`.applyAsDouble(var3, var10004.<DoubleIota>downcast(j, var10006).getDouble()));
   }

   @JvmStatic
   fun {
      val var10000: IotaMultiPredicate = IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.DOUBLE));
      ACCEPTS = var10000;
   }
}

package at.petrak.hexcasting.common.casting.arithmetic

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.arithmetic.engine.InvalidOperatorException
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBinary
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorUnary
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import java.util.function.LongBinaryOperator
import java.util.function.LongUnaryOperator
import kotlin.math.MathKt

public object BitwiseSetArithmetic : Arithmetic {
   private final val OPS: List<HexPattern> = CollectionsKt.listOf(new HexPattern[]{Arithmetic.AND, Arithmetic.OR, Arithmetic.XOR, Arithmetic.NOT})

   public override fun arithName(): String {
      return "bitwise_set_ops";
   }

   public open fun opTypes(): List<HexPattern> {
      return OPS;
   }

   public override fun getOperator(pattern: HexPattern): Operator {
      val var10000: Operator;
      if (pattern == Arithmetic.AND) {
         var10000 = this.make2(BitwiseSetArithmetic::getOperator$lambda$0);
      } else if (pattern == Arithmetic.OR) {
         var10000 = this.make2(BitwiseSetArithmetic::getOperator$lambda$1);
      } else if (pattern == Arithmetic.XOR) {
         var10000 = this.make2(BitwiseSetArithmetic::getOperator$lambda$2);
      } else {
         if (!(pattern == Arithmetic.NOT)) {
            throw new InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.");
         }

         var10000 = this.make1(BitwiseSetArithmetic::getOperator$lambda$3);
      }

      return var10000;
   }

   private fun make1(op: LongUnaryOperator): OperatorUnary {
      return new OperatorUnary(DoubleArithmetic.INSTANCE.getACCEPTS(), BitwiseSetArithmetic::make1$lambda$4);
   }

   private fun make2(op: LongBinaryOperator): OperatorBinary {
      return new OperatorBinary(DoubleArithmetic.INSTANCE.getACCEPTS(), BitwiseSetArithmetic::make2$lambda$5);
   }

   @JvmStatic
   fun `getOperator$lambda$0`(x: Long, y: Long): Long {
      return x and y;
   }

   @JvmStatic
   fun `getOperator$lambda$1`(x: Long, y: Long): Long {
      return x or y;
   }

   @JvmStatic
   fun `getOperator$lambda$2`(x: Long, y: Long): Long {
      return x xor y;
   }

   @JvmStatic
   fun `getOperator$lambda$3`(x: Long): Long {
      return x.inv();
   }

   @JvmStatic
   fun `make1$lambda$4`(`$op`: LongUnaryOperator, i: Iota): Iota {
      val var10003: Operator.Companion = Operator.Companion;
      val var10005: IotaType = HexIotaTypes.DOUBLE;
      return new DoubleIota(`$op`.applyAsLong(MathKt.roundToLong(var10003.<DoubleIota>downcast(i, var10005).getDouble())));
   }

   @JvmStatic
   fun `make2$lambda$5`(`$op`: LongBinaryOperator, i: Iota, j: Iota): Iota {
      val var10003: Operator.Companion = Operator.Companion;
      val var10005: IotaType = HexIotaTypes.DOUBLE;
      val var3: Long = MathKt.roundToLong(var10003.<DoubleIota>downcast(i, var10005).getDouble());
      val var10004: Operator.Companion = Operator.Companion;
      val var10006: IotaType = HexIotaTypes.DOUBLE;
      return new DoubleIota(`$op`.applyAsLong(var3, MathKt.roundToLong(var10004.<DoubleIota>downcast(j, var10006).getDouble())));
   }
}

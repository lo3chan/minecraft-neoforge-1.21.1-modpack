package at.petrak.hexcasting.common.casting.arithmetic.operator

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import kotlin.math.MathKt

public fun Iterator<IndexedValue<Iota>>.nextList(argc: Int = 0): SpellList {
   val var2: IndexedValue = `$this$nextList`.next() as IndexedValue;
   val idx: Int = var2.component1();
   val x: Iota = var2.component2() as Iota;
   if (x is ListIota) {
      val var10000: SpellList = (x as ListIota).getList();
      return var10000;
   } else {
      throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "list");
   }
}

@JvmSynthetic
fun `nextList$default`(var0: java.util.Iterator, var1: Int, var2: Int, var3: Any): SpellList {
   if ((var2 and 1) != 0) {
      var1 = 0;
   }

   return nextList(var0, var1);
}

public fun Iterator<IndexedValue<Iota>>.nextDouble(argc: Int = 0): Double {
   val var2: IndexedValue = `$this$nextDouble`.next() as IndexedValue;
   val idx: Int = var2.component1();
   val x: Iota = var2.component2() as Iota;
   if (x is DoubleIota) {
      return (x as DoubleIota).getDouble();
   } else {
      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "double");
   }
}

@JvmSynthetic
fun `nextDouble$default`(var0: java.util.Iterator, var1: Int, var2: Int, var3: Any): Double {
   if ((var2 and 1) != 0) {
      var1 = 0;
   }

   return nextDouble(var0, var1);
}

public fun Iterator<IndexedValue<Iota>>.nextInt(argc: Int = 0): Int {
   val var2: IndexedValue = `$this$nextInt`.next() as IndexedValue;
   val idx: Int = var2.component1();
   val x: Iota = var2.component2() as Iota;
   if (x is DoubleIota) {
      val var5: Double = (x as DoubleIota).getDouble();
      val rounded: Int = MathKt.roundToInt(var5);
      if (Math.abs(var5 - (double)rounded) <= 1.0E-4) {
         return rounded;
      }
   }

   throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int");
}

@JvmSynthetic
fun `nextInt$default`(var0: java.util.Iterator, var1: Int, var2: Int, var3: Any): Int {
   if ((var2 and 1) != 0) {
      var1 = 0;
   }

   return nextInt(var0, var1);
}

public fun Iterator<IndexedValue<Iota>>.nextPositiveIntUnder(max: Int, argc: Int = 0): Int {
   val var3: IndexedValue = `$this$nextPositiveIntUnder`.next() as IndexedValue;
   val idx: Int = var3.component1();
   val x: Iota = var3.component2() as Iota;
   if (x is DoubleIota) {
      val var6: Double = (x as DoubleIota).getDouble();
      val rounded: Int = MathKt.roundToInt(var6);
      if (Math.abs(var6 - (double)rounded) <= 1.0E-4 && 0 <= rounded && rounded < max) {
         return rounded;
      }
   }

   throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int.positive.less.equal", max);
}

@JvmSynthetic
fun `nextPositiveIntUnder$default`(var0: java.util.Iterator, var1: Int, var2: Int, var3: Int, var4: Any): Int {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return nextPositiveIntUnder(var0, var1, var2);
}

public fun Iterator<IndexedValue<Iota>>.nextPositiveIntUnderInclusive(max: Int, argc: Int = 0): Int {
   val var3: IndexedValue = `$this$nextPositiveIntUnderInclusive`.next() as IndexedValue;
   val idx: Int = var3.component1();
   val x: Iota = var3.component2() as Iota;
   if (x is DoubleIota) {
      val var6: Double = (x as DoubleIota).getDouble();
      val rounded: Int = MathKt.roundToInt(var6);
      if (Math.abs(var6 - (double)rounded) <= 1.0E-4 && 0 <= rounded && rounded <= max) {
         return rounded;
      }
   }

   throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int.positive.less.equal", max);
}

@JvmSynthetic
fun `nextPositiveIntUnderInclusive$default`(var0: java.util.Iterator, var1: Int, var2: Int, var3: Int, var4: Any): Int {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return nextPositiveIntUnderInclusive(var0, var1, var2);
}

public fun Double.asDoubleBetween(min: Double, max: Double, idx: Int): Double {
   if (min <= `$this$asDoubleBetween` && `$this$asDoubleBetween` <= max) {
      return `$this$asDoubleBetween`;
   } else {
      throw MishapInvalidIota.Companion.of(new DoubleIota(`$this$asDoubleBetween`), idx, "double.between", min, max);
   }
}

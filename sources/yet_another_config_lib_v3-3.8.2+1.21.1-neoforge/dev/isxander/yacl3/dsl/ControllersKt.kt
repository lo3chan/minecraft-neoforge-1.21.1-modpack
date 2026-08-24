@file:SourceDebugExtension(["SMAP\nControllers.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Controllers.kt\ndev/isxander/yacl3/dsl/ControllersKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,120:1\n1#2:121\n*E\n"])

package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.ItemControllerBuilder
import dev.isxander.yacl3.api.controller.LongFieldControllerBuilder
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import java.awt.Color
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.item.Item

public fun tickBox(): (Option<Boolean>) -> ControllerBuilder<Boolean> {
   return ControllersKt::tickBox$lambda$0;
}

public fun textSwitch(formatter: ValueFormatter<Boolean>? = null): (Option<Boolean>) -> ControllerBuilder<Boolean> {
   return ControllersKt::textSwitch$lambda$0;
}

@JvmSynthetic
fun `textSwitch$default`(var0: ValueFormatter, var1: Int, var2: Any): Function1 {
   if ((var1 and 1) != 0) {
      var0 = null;
   }

   return textSwitch(var0);
}

public fun slider(range: IntRange, step: Int = 1, formatter: ValueFormatter<Int>? = null): (Option<Int>) -> ControllerBuilder<Int> {
   return ControllersKt::slider$lambda$0;
}

@JvmSynthetic
fun `slider$default`(var0: IntRange, var1: Int, var2: ValueFormatter, var3: Int, var4: Any): Function1 {
   if ((var3 and 2) != 0) {
      var1 = 1;
   }

   if ((var3 and 4) != 0) {
      var2 = null;
   }

   return slider(var0, var1, var2);
}

public fun slider(range: LongRange, step: Long = 1L, formatter: ValueFormatter<Long>? = null): (Option<Long>) -> ControllerBuilder<Long> {
   return ControllersKt::slider$lambda$1;
}

@JvmSynthetic
fun `slider$default`(var0: LongRange, var1: Long, var3: ValueFormatter, var4: Int, var5: Any): Function1 {
   if ((var4 and 2) != 0) {
      var1 = 1L;
   }

   if ((var4 and 4) != 0) {
      var3 = null;
   }

   return slider(var0, var1, var3);
}

public fun slider(range: ClosedRange<Float>, step: Float = 1.0F, formatter: ValueFormatter<Float>? = null): (Option<Float>) -> ControllerBuilder<Float> {
   return ControllersKt::slider$lambda$2;
}

@JvmSynthetic
fun `slider$default`(var0: ClosedRange, var1: Float, var2: ValueFormatter, var3: Int, var4: Any): Function1 {
   if ((var3 and 2) != 0) {
      var1 = 1.0F;
   }

   if ((var3 and 4) != 0) {
      var2 = null;
   }

   return slider(var0, var1, var2);
}

public fun slider(range: ClosedRange<Double>, step: Double = 1.0, formatter: ValueFormatter<Double>? = null): (Option<Double>) -> ControllerBuilder<Double> {
   return ControllersKt::slider$lambda$3;
}

@JvmSynthetic
fun `slider$default`(var0: ClosedRange, var1: Double, var3: ValueFormatter, var4: Int, var5: Any): Function1 {
   if ((var4 and 2) != 0) {
      var1 = 1.0;
   }

   if ((var4 and 4) != 0) {
      var3 = null;
   }

   return slider(var0, var1, var3);
}

public fun stringField(): (Option<String>) -> ControllerBuilder<String> {
   return ControllersKt::stringField$lambda$0;
}

public fun numberField(min: Int? = null, max: Int? = null, formatter: ValueFormatter<Int>? = null): (Option<Int>) -> ControllerBuilder<Int> {
   return ControllersKt::numberField$lambda$0;
}

@JvmSynthetic
fun `numberField$default`(var0: Int, var1: Int, var2: ValueFormatter, var3: Int, var4: Any): Function1 {
   if ((var3 and 1) != 0) {
      var0 = null;
   }

   if ((var3 and 2) != 0) {
      var1 = null;
   }

   if ((var3 and 4) != 0) {
      var2 = null;
   }

   return numberField(var0, var1, var2);
}

public fun numberField(min: Long? = null, max: Long? = null, formatter: ValueFormatter<Long>? = null): (Option<Long>) -> ControllerBuilder<Long> {
   return ControllersKt::numberField$lambda$1;
}

@JvmSynthetic
fun `numberField$default`(var0: java.lang.Long, var1: java.lang.Long, var2: ValueFormatter, var3: Int, var4: Any): Function1 {
   if ((var3 and 1) != 0) {
      var0 = null;
   }

   if ((var3 and 2) != 0) {
      var1 = null;
   }

   if ((var3 and 4) != 0) {
      var2 = null;
   }

   return numberField(var0, var1, var2);
}

public fun numberField(min: Float? = null, max: Float? = null, formatter: ValueFormatter<Float>? = null): (Option<Float>) -> ControllerBuilder<Float> {
   return ControllersKt::numberField$lambda$2;
}

@JvmSynthetic
fun `numberField$default`(var0: java.lang.Float, var1: java.lang.Float, var2: ValueFormatter, var3: Int, var4: Any): Function1 {
   if ((var3 and 1) != 0) {
      var0 = null;
   }

   if ((var3 and 2) != 0) {
      var1 = null;
   }

   if ((var3 and 4) != 0) {
      var2 = null;
   }

   return numberField(var0, var1, var2);
}

public fun numberField(min: Double? = null, max: Double? = null, formatter: ValueFormatter<Double>? = null): (Option<Double>) -> ControllerBuilder<Double> {
   return ControllersKt::numberField$lambda$3;
}

@JvmSynthetic
fun `numberField$default`(var0: java.lang.Double, var1: java.lang.Double, var2: ValueFormatter, var3: Int, var4: Any): Function1 {
   if ((var3 and 1) != 0) {
      var0 = null;
   }

   if ((var3 and 2) != 0) {
      var1 = null;
   }

   if ((var3 and 4) != 0) {
      var2 = null;
   }

   return numberField(var0, var1, var2);
}

public fun colorPicker(allowAlpha: Boolean = false): (Option<Color>) -> ControllerBuilder<Color> {
   return ControllersKt::colorPicker$lambda$0;
}

@JvmSynthetic
fun `colorPicker$default`(var0: Boolean, var1: Int, var2: Any): Function1 {
   if ((var1 and 1) != 0) {
      var0 = false;
   }

   return colorPicker(var0);
}

public fun <T> cyclingList(values: Iterable<T>, formatter: ValueFormatter<T>? = null): (Option<T>) -> ControllerBuilder<T> {
   return ControllersKt::cyclingList$lambda$0;
}

@JvmSynthetic
fun `cyclingList$default`(var0: java.lang.Iterable, var1: ValueFormatter, var2: Int, var3: Any): Function1 {
   if ((var2 and 2) != 0) {
      var1 = null;
   }

   return cyclingList(var0, var1);
}

public fun <T : Enum<T>> enumSwitch(enumClass: Class<T>, formatter: ValueFormatter<T>? = null): (Option<T>) -> ControllerBuilder<T> {
   return ControllersKt::enumSwitch$lambda$0;
}

@JvmSynthetic
fun `enumSwitch$default`(var0: Class, var1: ValueFormatter, var2: Int, var3: Any): Function1 {
   if ((var2 and 2) != 0) {
      var1 = null;
   }

   return enumSwitch(var0, var1);
}

@JvmSynthetic
public inline fun <reified T : Enum<T>> enumSwitch(formatter: ValueFormatter<T>? = null): (Option<T>) -> ControllerBuilder<T> {
   Intrinsics.reifiedOperationMarker(4, "T");
   return enumSwitch(java.lang.Enum::class.java, formatter);
}

@JvmSynthetic
fun `enumSwitch$default`(formatter: ValueFormatter, `$i$f$enumSwitch`: Int, var2: Any): Function1 {
   if ((`$i$f$enumSwitch` and 1) != 0) {
      formatter = null;
   }

   Intrinsics.reifiedOperationMarker(4, "T");
   return enumSwitch(java.lang.Enum::class.java, formatter);
}

public fun <T : Enum<T>> enumDropdown(formatter: ValueFormatter<T>? = null): (Option<T>) -> ControllerBuilder<T> {
   return ControllersKt::enumDropdown$lambda$0;
}

@JvmSynthetic
fun `enumDropdown$default`(var0: ValueFormatter, var1: Int, var2: Any): Function1 {
   if ((var1 and 1) != 0) {
      var0 = null;
   }

   return enumDropdown(var0);
}

public fun minecraftItem(): (Option<Item>) -> ControllerBuilder<Item> {
   return ControllersKt::minecraftItem$lambda$0;
}

fun `tickBox$lambda$0`(option: Option): ControllerBuilder {
   val var10000: TickBoxControllerBuilder = TickBoxControllerBuilder.create(option);
   return var10000;
}

fun `textSwitch$lambda$0`(`$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var2: BooleanControllerBuilder = BooleanControllerBuilder.create(option);
   if (`$formatter` != null) {
      var2.formatValue(`$formatter`);
   }

   return var2;
}

fun `slider$lambda$0`(`$range`: IntRange, `$step`: Int, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var4: IntegerSliderControllerBuilder = IntegerSliderControllerBuilder.create(option);
   var4.range(`$range`.getFirst(), `$range`.getLast());
   var4.step(`$step`);
   if (`$formatter` != null) {
      var4.formatValue(`$formatter`);
   }

   return var4;
}

fun `slider$lambda$1`(`$range`: LongRange, `$step`: Long, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var5: LongSliderControllerBuilder = LongSliderControllerBuilder.create(option);
   var5.range(`$range`.getFirst(), `$range`.getLast());
   var5.step(`$step`);
   if (`$formatter` != null) {
      var5.formatValue(`$formatter`);
   }

   return var5;
}

fun `slider$lambda$2`(`$range`: ClosedRange, `$step`: Float, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var4: FloatSliderControllerBuilder = FloatSliderControllerBuilder.create(option);
   var4.range(`$range`.getStart() as java.lang.Number, `$range`.getEndInclusive() as java.lang.Number);
   var4.step(`$step`);
   if (`$formatter` != null) {
      var4.formatValue(`$formatter`);
   }

   return var4;
}

fun `slider$lambda$3`(`$range`: ClosedRange, `$step`: Double, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var5: DoubleSliderControllerBuilder = DoubleSliderControllerBuilder.create(option);
   var5.range(`$range`.getStart() as java.lang.Number, `$range`.getEndInclusive() as java.lang.Number);
   var5.step(`$step`);
   if (`$formatter` != null) {
      var5.formatValue(`$formatter`);
   }

   return var5;
}

fun `stringField$lambda$0`(option: Option): ControllerBuilder {
   val var10000: StringControllerBuilder = StringControllerBuilder.create(option);
   return var10000;
}

fun `numberField$lambda$0`(`$min`: Int, `$max`: Int, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var4: IntegerFieldControllerBuilder = IntegerFieldControllerBuilder.create(option);
   if (`$min` != null) {
      val var10000: IntegerFieldControllerBuilder = var4.min(`$min`.intValue());
   }

   if (`$max` != null) {
      val var12: IntegerFieldControllerBuilder = var4.max(`$max`.intValue());
   }

   if (`$formatter` != null) {
      var4.formatValue(`$formatter`);
   }

   return var4;
}

fun `numberField$lambda$1`(`$min`: java.lang.Long, `$max`: java.lang.Long, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var4: LongFieldControllerBuilder = LongFieldControllerBuilder.create(option);
   if (`$min` != null) {
      val var10000: LongFieldControllerBuilder = var4.min(`$min`.longValue());
   }

   if (`$max` != null) {
      val var14: LongFieldControllerBuilder = var4.max(`$max`.longValue());
   }

   if (`$formatter` != null) {
      var4.formatValue(`$formatter`);
   }

   return var4;
}

fun `numberField$lambda$2`(`$min`: java.lang.Float, `$max`: java.lang.Float, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var4: FloatFieldControllerBuilder = FloatFieldControllerBuilder.create(option);
   if (`$min` != null) {
      val var10000: FloatFieldControllerBuilder = var4.min(`$min`.floatValue());
   }

   if (`$max` != null) {
      val var12: FloatFieldControllerBuilder = var4.max(`$max`.floatValue());
   }

   if (`$formatter` != null) {
      var4.formatValue(`$formatter`);
   }

   return var4;
}

fun `numberField$lambda$3`(`$min`: java.lang.Double, `$max`: java.lang.Double, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var4: DoubleFieldControllerBuilder = DoubleFieldControllerBuilder.create(option);
   if (`$min` != null) {
      val var10000: DoubleFieldControllerBuilder = var4.min(`$min`.doubleValue());
   }

   if (`$max` != null) {
      val var14: DoubleFieldControllerBuilder = var4.max(`$max`.doubleValue());
   }

   if (`$formatter` != null) {
      var4.formatValue(`$formatter`);
   }

   return var4;
}

fun `colorPicker$lambda$0`(`$allowAlpha`: Boolean, option: Option): ControllerBuilder {
   val var2: ColorControllerBuilder = ColorControllerBuilder.create(option);
   var2.allowAlpha(`$allowAlpha`);
   return var2;
}

fun `cyclingList$lambda$0`(`$values`: java.lang.Iterable, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var3: CyclingListControllerBuilder = CyclingListControllerBuilder.create(option);
   var3.values(`$values`);
   if (`$formatter` != null) {
      var3.formatValue(`$formatter`);
   }

   return var3;
}

fun `enumSwitch$lambda$0`(`$enumClass`: Class, `$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var3: EnumControllerBuilder = EnumControllerBuilder.create(option);
   var3.enumClass(`$enumClass`);
   if (`$formatter` != null) {
      var3.formatValue(`$formatter`);
   }

   return var3;
}

fun `enumDropdown$lambda$0`(`$formatter`: ValueFormatter, option: Option): ControllerBuilder {
   val var2: EnumDropdownControllerBuilder = EnumDropdownControllerBuilder.create(option);
   if (`$formatter` != null) {
      var2.formatValue(`$formatter`);
   }

   return var2;
}

fun `minecraftItem$lambda$0`(option: Option): ControllerBuilder {
   val var10000: ItemControllerBuilder = ItemControllerBuilder.create(option);
   return var10000;
}

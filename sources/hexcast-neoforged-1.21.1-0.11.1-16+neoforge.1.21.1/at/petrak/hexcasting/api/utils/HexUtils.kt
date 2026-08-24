@file:JvmName(name = "HexUtils")

@file:SourceDebugExtension(["SMAP\nHexUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HexUtils.kt\nat/petrak/hexcasting/api/utils/HexUtils\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,321:1\n1310#2,2:322\n1#3:324\n*S KotlinDebug\n*F\n+ 1 HexUtils.kt\nat/petrak/hexcasting/api/utils/HexUtils\n*L\n112#1:322,2\n*E\n"])

package at.petrak.hexcasting.api.utils

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.math.HexCoord
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Arrays
import java.util.Locale
import java.util.Optional
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import kotlin.reflect.KProperty
import net.minecraft.ChatFormatting
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.Holder.Reference
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

public const val TAU: Double = 6.283185307179586
public const val SQRT_3: Float = 1.7320508F

public final val black: MutableComponent
   public final get() {
      return styledWith(`$this$black`, ChatFormatting.BLACK);
   }


public final val black: MutableComponent
   public final get() {
      return styledWith(`$this$black`, ChatFormatting.BLACK);
   }


public final val darkBlue: MutableComponent
   public final get() {
      return styledWith(`$this$darkBlue`, ChatFormatting.DARK_BLUE);
   }


public final val darkBlue: MutableComponent
   public final get() {
      return styledWith(`$this$darkBlue`, ChatFormatting.DARK_BLUE);
   }


public final val darkGreen: MutableComponent
   public final get() {
      return styledWith(`$this$darkGreen`, ChatFormatting.DARK_GREEN);
   }


public final val darkGreen: MutableComponent
   public final get() {
      return styledWith(`$this$darkGreen`, ChatFormatting.DARK_GREEN);
   }


public final val darkAqua: MutableComponent
   public final get() {
      return styledWith(`$this$darkAqua`, ChatFormatting.DARK_AQUA);
   }


public final val darkAqua: MutableComponent
   public final get() {
      return styledWith(`$this$darkAqua`, ChatFormatting.DARK_AQUA);
   }


public final val darkRed: MutableComponent
   public final get() {
      return styledWith(`$this$darkRed`, ChatFormatting.DARK_RED);
   }


public final val darkRed: MutableComponent
   public final get() {
      return styledWith(`$this$darkRed`, ChatFormatting.DARK_RED);
   }


public final val darkPurple: MutableComponent
   public final get() {
      return styledWith(`$this$darkPurple`, ChatFormatting.DARK_PURPLE);
   }


public final val darkPurple: MutableComponent
   public final get() {
      return styledWith(`$this$darkPurple`, ChatFormatting.DARK_PURPLE);
   }


public final val gold: MutableComponent
   public final get() {
      return styledWith(`$this$gold`, ChatFormatting.GOLD);
   }


public final val gold: MutableComponent
   public final get() {
      return styledWith(`$this$gold`, ChatFormatting.GOLD);
   }


public final val gray: MutableComponent
   public final get() {
      return styledWith(`$this$gray`, ChatFormatting.GRAY);
   }


public final val gray: MutableComponent
   public final get() {
      return styledWith(`$this$gray`, ChatFormatting.GRAY);
   }


public final val darkGray: MutableComponent
   public final get() {
      return styledWith(`$this$darkGray`, ChatFormatting.DARK_GRAY);
   }


public final val darkGray: MutableComponent
   public final get() {
      return styledWith(`$this$darkGray`, ChatFormatting.DARK_GRAY);
   }


public final val blue: MutableComponent
   public final get() {
      return styledWith(`$this$blue`, ChatFormatting.BLUE);
   }


public final val blue: MutableComponent
   public final get() {
      return styledWith(`$this$blue`, ChatFormatting.BLUE);
   }


public final val green: MutableComponent
   public final get() {
      return styledWith(`$this$green`, ChatFormatting.GREEN);
   }


public final val green: MutableComponent
   public final get() {
      return styledWith(`$this$green`, ChatFormatting.GREEN);
   }


public final val aqua: MutableComponent
   public final get() {
      return styledWith(`$this$aqua`, ChatFormatting.AQUA);
   }


public final val aqua: MutableComponent
   public final get() {
      return styledWith(`$this$aqua`, ChatFormatting.AQUA);
   }


public final val red: MutableComponent
   public final get() {
      return styledWith(`$this$red`, ChatFormatting.RED);
   }


public final val red: MutableComponent
   public final get() {
      return styledWith(`$this$red`, ChatFormatting.RED);
   }


public final val lightPurple: MutableComponent
   public final get() {
      return styledWith(`$this$lightPurple`, ChatFormatting.LIGHT_PURPLE);
   }


public final val lightPurple: MutableComponent
   public final get() {
      return styledWith(`$this$lightPurple`, ChatFormatting.LIGHT_PURPLE);
   }


public final val yellow: MutableComponent
   public final get() {
      return styledWith(`$this$yellow`, ChatFormatting.YELLOW);
   }


public final val yellow: MutableComponent
   public final get() {
      return styledWith(`$this$yellow`, ChatFormatting.YELLOW);
   }


public final val white: MutableComponent
   public final get() {
      return styledWith(`$this$white`, ChatFormatting.WHITE);
   }


public final val white: MutableComponent
   public final get() {
      return styledWith(`$this$white`, ChatFormatting.WHITE);
   }


public final val obfuscated: MutableComponent
   public final get() {
      return styledWith(`$this$obfuscated`, ChatFormatting.OBFUSCATED);
   }


public final val obfuscated: MutableComponent
   public final get() {
      return styledWith(`$this$obfuscated`, ChatFormatting.OBFUSCATED);
   }


public final val bold: MutableComponent
   public final get() {
      return styledWith(`$this$bold`, ChatFormatting.BOLD);
   }


public final val bold: MutableComponent
   public final get() {
      return styledWith(`$this$bold`, ChatFormatting.BOLD);
   }


public final val strikethrough: MutableComponent
   public final get() {
      return styledWith(`$this$strikethrough`, ChatFormatting.STRIKETHROUGH);
   }


public final val strikethrough: MutableComponent
   public final get() {
      return styledWith(`$this$strikethrough`, ChatFormatting.STRIKETHROUGH);
   }


public final val underline: MutableComponent
   public final get() {
      return styledWith(`$this$underline`, ChatFormatting.UNDERLINE);
   }


public final val underline: MutableComponent
   public final get() {
      return styledWith(`$this$underline`, ChatFormatting.UNDERLINE);
   }


public final val italic: MutableComponent
   public final get() {
      return styledWith(`$this$italic`, ChatFormatting.ITALIC);
   }


public final val italic: MutableComponent
   public final get() {
      return styledWith(`$this$italic`, ChatFormatting.ITALIC);
   }


public final val asTextComponent: MutableComponent
   public final get() {
      val var10000: MutableComponent = Component.literal(`$this$asTextComponent`);
      return var10000;
   }


public final val asTranslatedComponent: MutableComponent
   public final get() {
      val var10000: MutableComponent = Component.translatable(`$this$asTranslatedComponent`);
      return var10000;
   }


public const val ERROR_COLOR: Int = -524040

public fun Vec3.serializeToNBT(): CompoundTag {
   val tag: CompoundTag = new CompoundTag();
   tag.putDouble("x", `$this$serializeToNBT`.x);
   tag.putDouble("y", `$this$serializeToNBT`.y);
   tag.putDouble("z", `$this$serializeToNBT`.z);
   return tag;
}

public fun vecFromNBT(tag: LongArray): Vec3 {
   val var10000: Vec3;
   if (tag.length != 3) {
      var10000 = Vec3.ZERO;
   } else {
      var10000 = new Vec3(java.lang.Double.longBitsToDouble(tag[0]), java.lang.Double.longBitsToDouble(tag[1]), java.lang.Double.longBitsToDouble(tag[2]));
   }

   return var10000;
}

public fun vecFromNBT(tag: CompoundTag): Vec3 {
   val var10000: Vec3;
   if (tag.contains("x") && tag.contains("y") && tag.contains("z")) {
      var10000 = new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
   } else {
      var10000 = Vec3.ZERO;
   }

   return var10000;
}

public fun Vec2.serializeToNBT(): LongArrayTag {
   return new LongArrayTag(
      new long[]{
         java.lang.Double.doubleToRawLongBits((double)`$this$serializeToNBT`.x), java.lang.Double.doubleToRawLongBits((double)`$this$serializeToNBT`.y)
      }
   );
}

public fun vec2FromNBT(tag: LongArray): Vec2 {
   val var10000: Vec2;
   if (tag.length != 2) {
      var10000 = Vec2.ZERO;
   } else {
      var10000 = new Vec2((float)java.lang.Double.longBitsToDouble(tag[0]), (float)java.lang.Double.longBitsToDouble(tag[1]));
   }

   return var10000;
}

public fun otherHand(hand: InteractionHand): InteractionHand {
   return if (hand === InteractionHand.MAIN_HAND) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND;
}

public fun fixNAN(n: Double): Double {
   return if (Math.abs(n) <= 1.7976931348623157E308) n else 0.0;
}

public fun findCenter(points: List<Vec2>): Vec2 {
   var minX: Float = 1.0F / 0.0F;
   var minY: Float = 1.0F / 0.0F;
   var maxX: Float = -1.0F / 0.0F;
   var maxY: Float = -1.0F / 0.0F;

   for (Vec2 pos : points) {
      minX = Math.min(minX, pos.x);
      minY = Math.min(minY, pos.y);
      maxX = Math.max(maxX, pos.x);
      maxY = Math.max(maxY, pos.y);
   }

   return new Vec2((minX + maxX) / 2.0F, (minY + maxY) / 2.0F);
}

public fun coordToPx(coord: HexCoord, size: Float, offset: Vec2): Vec2 {
   val var10000: Vec2 = new Vec2(1.7320508F * (float)coord.getQ() + 0.8660254F * (float)coord.getR(), 1.5F * (float)coord.getR()).scale(size).add(offset);
   return var10000;
}

public fun pxToCoord(px: Vec2, size: Float, offset: Vec2): HexCoord {
   val offsetted: Vec2 = px.add(offset.negated());
   val qf: Float = (0.57735026F * offsetted.x - 0.33333F * offsetted.y) / size;
   val rf: Float = 0.66666F * offsetted.y / size;
   val q: Int = MathKt.roundToInt(qf);
   val r: Int = MathKt.roundToInt(rf);
   return if (Math.abs(q) >= Math.abs(r))
      new HexCoord(q + MathKt.roundToInt(qf - (float)q + 0.5F * (rf - (float)r)), r)
      else
      new HexCoord(q, r + MathKt.roundToInt((double)(rf - (float)r) + 0.5 * (double)(qf - (float)q)));
}

@JvmOverloads
public fun <T : Enum<T>> Array<T>.getSafe(key: String, default: T = ...): T {
   var var10000: Locale = Locale.ROOT;
   val var13: java.lang.String = key.toLowerCase(var10000);
   val lowercaseKey: java.lang.String = var13;
   val `$this$firstOrNull$iv`: Array<Any> = `$this$getSafe`;
   var var7: Int = 0;
   val var8: Int = `$this$getSafe`.length;

   while (true) {
      if (var7 >= var8) {
         var16 = null;
         break;
      }

      val `element$iv`: Any = `$this$firstOrNull$iv`[var7];
      val var12: java.lang.String = `$this$firstOrNull$iv`[var7].name();
      var10000 = Locale.ROOT;
      val var15: java.lang.String = var12.toLowerCase(var10000);
      if (var15 == lowercaseKey) {
         var16 = `element$iv`;
         break;
      }

      var7++;
   }

   if (var16 == null) {
      var16 = var2;
   }

   return (T)var16;
}

@JvmSynthetic
fun `getSafe$default`(var0: Array<java.lang.Enum>, var1: java.lang.String, var2: java.lang.Enum, var3: Int, var4: Any): java.lang.Enum {
   if ((var3 and 2) != 0) {
      var2 = var0[0];
   }

   return getSafe(var0, var1, var2);
}

@JvmOverloads
public fun <T : Enum<T>> Array<T>.getSafe(index: Byte, default: T = ...): T {
   return (T)getSafe(`$this$getSafe`, (int)index, var2);
}

@JvmSynthetic
fun `getSafe$default`(var0: Array<java.lang.Enum>, var1: Byte, var2: java.lang.Enum, var3: Int, var4: Any): java.lang.Enum {
   if ((var3 and 2) != 0) {
      var2 = var0[0];
   }

   return getSafe(var0, var1, var2);
}

@JvmOverloads
public fun <T : Enum<T>> Array<T>.getSafe(index: Int, default: T = ...): T {
   return (T)(if (0 <= index && index < `$this$getSafe`.length) `$this$getSafe`[index] else var2);
}

@JvmSynthetic
fun `getSafe$default`(var0: Array<java.lang.Enum>, var1: Int, var2: java.lang.Enum, var3: Int, var4: Any): java.lang.Enum {
   if ((var3 and 2) != 0) {
      var2 = var0[0];
   }

   return getSafe(var0, var1, var2);
}

public fun String.withStyle(op: (Style) -> Style): MutableComponent {
   val var10000: MutableComponent = getAsTextComponent(`$this$withStyle`).withStyle(HexUtils::withStyle$lambda$1);
   return var10000;
}

public fun String.withStyle(style: Style): MutableComponent {
   val var10000: MutableComponent = getAsTextComponent(`$this$withStyle`).withStyle(style);
   return var10000;
}

public fun String.withStyle(formatting: ChatFormatting): MutableComponent {
   val var10000: MutableComponent = getAsTextComponent(`$this$withStyle`).withStyle(formatting);
   return var10000;
}

public fun String.withStyle(vararg formatting: ChatFormatting): MutableComponent {
   val var10000: MutableComponent = getAsTextComponent(`$this$withStyle`).withStyle(Arrays.copyOf(formatting, formatting.length));
   return var10000;
}

public infix fun String.styledWith(op: (Style) -> Style): MutableComponent {
   return withStyle(`$this$styledWith`, op);
}

public infix fun String.styledWith(style: Style): MutableComponent {
   return withStyle(`$this$styledWith`, style);
}

public infix fun String.styledWith(formatting: ChatFormatting): MutableComponent {
   return withStyle(`$this$styledWith`, formatting);
}

public infix fun MutableComponent.styledWith(op: (Style) -> Style): MutableComponent {
   val var10000: MutableComponent = `$this$styledWith`.withStyle(HexUtils::styledWith$lambda$2);
   return var10000;
}

public infix fun MutableComponent.styledWith(style: Style): MutableComponent {
   val var10000: MutableComponent = `$this$styledWith`.withStyle(style);
   return var10000;
}

public infix fun MutableComponent.styledWith(formatting: ChatFormatting): MutableComponent {
   val var10000: MutableComponent = `$this$styledWith`.withStyle(formatting);
   return var10000;
}

public operator fun MutableComponent.plusAssign(component: Component) {
   `$this$plusAssign`.append(component);
}

public fun String.asTranslatedComponent(vararg args: Any): MutableComponent {
   val var10000: MutableComponent = Component.translatable(`$this$asTranslatedComponent`, Arrays.copyOf(args, args.length));
   return var10000;
}

public fun <T> weakReference(value: T? = null): WeakValue<T> {
   return (WeakValue<T>)(new WeakReferencedValue<>(if (value != null) new WeakReference<>(value) else null));
}

@JvmSynthetic
fun `weakReference$default`(var0: Any, var1: Int, var2: Any): WeakValue {
   if ((var1 and 1) != 0) {
      var0 = null;
   }

   return weakReference(var0);
}

public fun <T, K> weakMapped(keyGen: (T) -> K): WeakValue<T> {
   return new WeakMappedValue(keyGen);
}

public inline operator fun <T> WeakValue<T>.getValue(thisRef: Any?, property: KProperty<*>): T? {
   return (T)`$this$getValue`.getValue();
}

public inline operator fun <T> WeakValue<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
   `$this$setValue`.setValue(value);
}

public fun Iterable<Iota>.serializeToNBT(): Tag {
   val var1: Tag = if (IotaType.isTooLargeToSerialize(`$this$serializeToNBT`))
      (new ListTag()) as Tag
      else
      new ListIota(CollectionsKt.toList(`$this$serializeToNBT`)).serialize();
   return var1;
}

public fun Iterable<Boolean>.serializeToNBT(): ByteArrayTag {
   val out: ByteArray = new byte[if (`$this$serializeToNBT` is java.util.Collection) (`$this$serializeToNBT` as java.util.Collection).size() else 10];
   val var2: java.util.Iterator = `$this$serializeToNBT`.iterator();
   var var3: Int = 0;

   while (var2.hasNext()) {
      out[var3++] = (byte)(if (var2.next()) 1 else 0);
   }

   return new ByteArrayTag(out);
}

public fun <A> List<A>.zipWithDefault(array: ByteArray, default: (Int) -> Byte): List<Pair<A, Byte>> {
   val list: ArrayList = new ArrayList(`$this$zipWithDefault`.size());
   var i: Int = 0;

   for (Object element : $this$zipWithDefault) {
      val var8: Int = i++;
      list.add(TuplesKt.to(element, if (0 <= var8 && var8 < array.length) array[var8] else (var2.invoke(var8) as java.lang.Number).byteValue()));
   }

   return list;
}

public fun ItemStack.serializeToNBT(): CompoundTag {
   if (`$this$serializeToNBT`.isEmpty()) {
      return new CompoundTag();
   } else {
      val out: CompoundTag = new CompoundTag();
      `$this$serializeToNBT`.save(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY) as Provider, out as Tag);
      return out;
   }
}

@Throws(java/lang/IllegalArgumentException::class)
public fun <T : Tag> Tag.downcast(type: TagType<T>): T {
   if (`$this$downcast`.getType() == type) {
      return (T)`$this$downcast`;
   } else {
      throw new IllegalArgumentException("Expected this tag to be of type ${type.getName()}, but found ${`$this$downcast`.getType().getName()}.");
   }
}

public fun <T> isOfTag(registry: Registry<T>, key: ResourceKey<T>, tag: TagKey<T>): Boolean {
   val maybeHolder: Optional = registry.getHolder(key);
   if (maybeHolder.isPresent()) {
      val var5: Reference = maybeHolder.get() as Reference;
      return var5.is(tag);
   } else {
      return false;
   }
}

public fun <T> isOfTag(registry: Registry<T>, loc: ResourceLocation, tag: TagKey<T>): Boolean {
   val key: ResourceKey = ResourceKey.create(registry.key(), loc);
   return isOfTag(registry, key, tag);
}

@JvmOverloads
fun <T extends java.lang.Enum<T>> Array<T>.getSafe(key: java.lang.String): T {
   return (T)getSafe$default(`$this$getSafe`, key, null, 2, null);
}

@JvmOverloads
fun <T extends java.lang.Enum<T>> Array<T>.getSafe(index: Byte): T {
   return (T)getSafe$default(`$this$getSafe`, index, null, 2, null);
}

@JvmOverloads
fun <T extends java.lang.Enum<T>> Array<T>.getSafe(index: Int): T {
   return (T)getSafe$default(`$this$getSafe`, index, null, 2, null);
}

fun `withStyle$lambda$1`(`$tmp0`: Function1, p0: Style): Style {
   return `$tmp0`.invoke(p0) as Style;
}

fun `styledWith$lambda$2`(`$tmp0`: Function1, p0: Style): Style {
   return `$tmp0`.invoke(p0) as Style;
}

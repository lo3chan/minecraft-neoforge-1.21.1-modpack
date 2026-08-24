package at.petrak.hexcasting.api.utils

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

@JvmInline
@NBTDslMarker
@SourceDebugExtension(["SMAP\nNBTDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtListBuilder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,216:1\n157#1,2:217\n1#2:219\n1563#3:220\n1634#3,3:221\n1563#3:224\n1634#3,3:225\n1563#3:228\n1634#3,3:229\n1563#3:232\n1634#3,3:233\n11258#4:236\n11593#4,3:237\n11288#4:240\n11623#4,3:241\n11258#4:244\n11593#4,3:245\n11278#4:248\n11613#4,3:249\n11258#4:252\n11593#4,3:253\n11268#4:256\n11603#4,3:257\n11258#4:260\n11593#4,3:261\n11258#4:264\n11593#4,3:265\n11248#4:268\n11583#4,3:269\n11258#4:272\n11593#4,3:273\n11238#4:276\n11573#4,3:277\n11228#4:280\n11563#4,3:281\n*S KotlinDebug\n*F\n+ 1 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtListBuilder\n*L\n174#1:217,2\n180#1:220\n180#1:221,3\n191#1:224\n191#1:225,3\n195#1:228\n195#1:229,3\n199#1:232\n199#1:233,3\n202#1:236\n202#1:237,3\n203#1:240\n203#1:241,3\n204#1:244\n204#1:245,3\n205#1:248\n205#1:249,3\n206#1:252\n206#1:253,3\n207#1:256\n207#1:257,3\n208#1:260\n208#1:261,3\n209#1:264\n209#1:265,3\n210#1:268\n210#1:269,3\n211#1:272\n211#1:273,3\n212#1:276\n212#1:277,3\n214#1:280\n214#1:281,3\n*E\n"])
public inline class NbtListBuilder {
   public final val tag: ListTag

   @JvmStatic
   public inline operator fun Tag.unaryPlus() {
      arg0.add(`$this$unaryPlus`);
   }

   @JvmStatic
   public inline operator fun Collection<Tag>.unaryPlus() {
      arg0.addAll(`$this$unaryPlus`);
   }

   @JvmStatic
   public inline operator fun ListTag.unaryPlus() {
      arg0.add(`$this$unaryPlus`);
   }

   @JvmStatic
   public inline fun addAll(nbt: Collection<Tag>) {
      arg0.addAll(nbt);
   }

   @JvmStatic
   public inline fun add(nbt: Tag) {
      arg0.add(nbt);
   }

   @JvmStatic
   public inline fun compound(block: (NbtCompoundBuilder) -> Unit): CompoundTag {
      val var3: NbtCompoundBuilder = NbtCompoundBuilder.box-impl(NbtCompoundBuilder.constructor-impl(new CompoundTag()));
      block.invoke(var3);
      return var3.unbox-impl();
   }

   @JvmStatic
   public inline fun list(block: (NbtListBuilder) -> Unit): ListTag {
      val var3: NbtListBuilder = box-impl(constructor-impl(new ListTag()));
      block.invoke(var3);
      return var3.unbox-impl();
   }

   @JvmStatic
   public inline fun list(vararg elements: Tag, block: (NbtListBuilder) -> Unit): ListTag {
      val var4: ListTag = constructor-impl(new ListTag());
      var4.addAll(ArraysKt.toList(elements));
      block.invoke(box-impl(var4));
      return var4;
   }

   @JvmStatic
   public inline fun list(vararg elements: Tag): ListTag {
      val var3: ListTag = new ListTag();
      CollectionsKt.addAll(var3 as java.util.Collection, elements);
      return var3;
   }

   @JvmStatic
   public inline fun list(elements: Collection<Tag>): ListTag {
      val var3: ListTag = new ListTag();
      var3.addAll(elements);
      return var3;
   }

   @JvmStatic
   public inline fun <T> list(elements: Collection<T>, mapper: (T) -> Tag): ListTag {
      val var4: ListTag = new ListTag();
      val `$this$map$iv`: java.lang.Iterable = elements;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(elements, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(mapper.invoke(`item$iv$iv`));
      }

      var4.addAll(`destination$iv$iv` as java.util.List);
      return var4;
   }

   @JvmStatic
   public inline fun double(value: Number): DoubleTag {
      val var10000: DoubleTag = DoubleTag.valueOf(value.doubleValue());
      return var10000;
   }

   @JvmStatic
   public inline fun float(value: Number): FloatTag {
      val var10000: FloatTag = FloatTag.valueOf(value.floatValue());
      return var10000;
   }

   @JvmStatic
   public inline fun long(value: Number): LongTag {
      val var10000: LongTag = LongTag.valueOf(value.longValue());
      return var10000;
   }

   @JvmStatic
   public inline fun int(value: Number): IntTag {
      val var10000: IntTag = IntTag.valueOf(value.intValue());
      return var10000;
   }

   @JvmStatic
   public inline fun short(value: Number): ShortTag {
      val var10000: ShortTag = ShortTag.valueOf(value.shortValue());
      return var10000;
   }

   @JvmStatic
   public inline fun byte(value: Number): ByteTag {
      val var10000: ByteTag = ByteTag.valueOf(value.byteValue());
      return var10000;
   }

   @JvmStatic
   public inline fun string(value: String): StringTag {
      val var10000: StringTag = StringTag.valueOf(value);
      return var10000;
   }

   @JvmStatic
   public inline fun byteArray(value: Collection<Number>): ByteArrayTag {
      val `$this$map$iv`: java.lang.Iterable = value;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(value, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).byteValue());
      }

      return new ByteArrayTag(`destination$iv$iv` as java.util.List);
   }

   @JvmStatic
   public inline fun byteArray(value: IntArray): ByteArrayTag {
      var var3: Int = 0;
      val var4: Int = value.length;

      val var5: ByteArray;
      for (var5 = new byte[value.length]; var3 < var4; var3++) {
         var5[var3] = (byte)value[var3];
      }

      return new ByteArrayTag(var5);
   }

   @JvmStatic
   public inline fun byteArray(value: ByteArray): ByteArrayTag {
      return new ByteArrayTag(value);
   }

   @JvmStatic
   public inline fun byteArray(): ByteArrayTag {
      return new ByteArrayTag(new byte[0]);
   }

   @JvmStatic
   public inline fun longArray(value: Collection<Number>): LongArrayTag {
      val `$this$map$iv`: java.lang.Iterable = value;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(value, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).longValue());
      }

      return new LongArrayTag(`destination$iv$iv` as java.util.List);
   }

   @JvmStatic
   public inline fun longArray(value: IntArray): LongArrayTag {
      var var3: Int = 0;
      val var4: Int = value.length;

      val var5: LongArray;
      for (var5 = new long[value.length]; var3 < var4; var3++) {
         var5[var3] = value[var3];
      }

      return new LongArrayTag(var5);
   }

   @JvmStatic
   public inline fun longArray(value: LongArray): LongArrayTag {
      return new LongArrayTag(value);
   }

   @JvmStatic
   public inline fun longArray(): LongArrayTag {
      return new LongArrayTag(new long[0]);
   }

   @JvmStatic
   public inline fun intArray(value: Collection<Number>): IntArrayTag {
      val `$this$map$iv`: java.lang.Iterable = value;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(value, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).intValue());
      }

      return new IntArrayTag(`destination$iv$iv` as java.util.List);
   }

   @JvmStatic
   public inline fun intArray(value: IntArray): IntArrayTag {
      return new IntArrayTag(value);
   }

   @JvmStatic
   public inline fun doubles(value: IntArray): List<DoubleTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (int item$iv$iv : value) {
         `destination$iv$iv`.add(DoubleTag.valueOf((double)`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<DoubleTag>;
   }

   @JvmStatic
   public inline fun doubles(value: DoubleArray): List<DoubleTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (double item$iv$iv : value) {
         `destination$iv$iv`.add(DoubleTag.valueOf(`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<DoubleTag>;
   }

   @JvmStatic
   public inline fun floats(value: IntArray): List<FloatTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (int item$iv$iv : value) {
         `destination$iv$iv`.add(FloatTag.valueOf((float)`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<FloatTag>;
   }

   @JvmStatic
   public inline fun floats(value: FloatArray): List<FloatTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (float item$iv$iv : value) {
         `destination$iv$iv`.add(FloatTag.valueOf(`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<FloatTag>;
   }

   @JvmStatic
   public inline fun longs(value: IntArray): List<LongTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (int item$iv$iv : value) {
         `destination$iv$iv`.add(LongTag.valueOf((long)`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<LongTag>;
   }

   @JvmStatic
   public inline fun longs(value: LongArray): List<LongTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (long item$iv$iv : value) {
         `destination$iv$iv`.add(LongTag.valueOf(`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<LongTag>;
   }

   @JvmStatic
   public inline fun ints(value: IntArray): List<IntTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (int item$iv$iv : value) {
         `destination$iv$iv`.add(IntTag.valueOf(`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<IntTag>;
   }

   @JvmStatic
   public inline fun shorts(value: IntArray): List<ShortTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (int item$iv$iv : value) {
         `destination$iv$iv`.add(ShortTag.valueOf((short)`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<ShortTag>;
   }

   @JvmStatic
   public inline fun shorts(value: ShortArray): List<ShortTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (short item$iv$iv : value) {
         `destination$iv$iv`.add(ShortTag.valueOf(`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<ShortTag>;
   }

   @JvmStatic
   public inline fun bytes(value: IntArray): List<ByteTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (int item$iv$iv : value) {
         `destination$iv$iv`.add(ByteTag.valueOf((byte)`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<ByteTag>;
   }

   @JvmStatic
   public inline fun bytes(value: ByteArray): List<ByteTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (byte item$iv$iv : value) {
         `destination$iv$iv`.add(ByteTag.valueOf(`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<ByteTag>;
   }

   @JvmStatic
   public fun strings(vararg value: String): List<StringTag> {
      val `destination$iv$iv`: java.util.Collection = new ArrayList(value.length);

      for (Object item$iv$iv : value) {
         `destination$iv$iv`.add(StringTag.valueOf((java.lang.String)`item$iv$iv`));
      }

      return `destination$iv$iv` as MutableList<StringTag>;
   }

   @JvmStatic
   public open fun toString(): String {
      return "NbtListBuilder(tag=$arg0)";
   }

   override fun toString(): java.lang.String {
      return toString-impl(this.tag);
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return arg0.hashCode();
   }

   override fun hashCode(): Int {
      return hashCode-impl(this.tag);
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      if (other !is NbtListBuilder) {
         return false;
      } else {
         return arg0 == (other as NbtListBuilder).unbox-impl();
      }
   }

   override fun equals(other: Any): Boolean {
      return equals-impl(this.tag, other);
   }

   @JvmStatic
   fun `constructor-impl`(tag: ListTag): ListTag {
      return tag;
   }

   @JvmStatic
   fun `equals-impl0`(p1: ListTag, p2: ListTag): Boolean {
      return p1 == p2;
   }
}

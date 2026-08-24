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
@SourceDebugExtension(["SMAP\nNBTDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n+ 2 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtListBuilder\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,216:1\n115#1:217\n111#1:218\n110#1:219\n108#1:220\n109#1:221\n113#1:222\n157#2,2:223\n1#3:225\n1563#4:226\n1634#4,3:227\n1563#4:230\n1634#4,3:231\n1563#4:234\n1634#4,3:235\n1563#4:238\n1634#4,3:239\n*S KotlinDebug\n*F\n+ 1 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n*L\n67#1:217\n71#1:218\n75#1:219\n79#1:220\n83#1:221\n87#1:222\n100#1:223,2\n106#1:226\n106#1:227,3\n117#1:230\n117#1:231,3\n121#1:234\n121#1:235,3\n125#1:238\n125#1:239,3\n*E\n"])
public inline class NbtCompoundBuilder {
   public final val tag: CompoundTag

   @JvmStatic
   public inline operator fun String.remAssign(nbt: Tag) {
      arg0.put(`$this$remAssign`, nbt);
   }

   @JvmStatic
   public inline operator fun String.remAssign(str: String) {
      val var10002: StringTag = StringTag.valueOf(str);
      arg0.put(`$this$remAssign`, var10002 as Tag);
   }

   @JvmStatic
   public inline operator fun String.remAssign(num: Int) {
      val var10002: IntTag = IntTag.valueOf(num);
      arg0.put(`$this$remAssign`, var10002 as Tag);
   }

   @JvmStatic
   public inline operator fun String.remAssign(num: Long) {
      val var10002: LongTag = LongTag.valueOf(num);
      arg0.put(`$this$remAssign`, var10002 as Tag);
   }

   @JvmStatic
   public inline operator fun String.remAssign(num: Double) {
      val var10002: DoubleTag = DoubleTag.valueOf(num);
      arg0.put(`$this$remAssign`, var10002 as Tag);
   }

   @JvmStatic
   public inline operator fun String.remAssign(num: Float) {
      val var10002: FloatTag = FloatTag.valueOf(num);
      arg0.put(`$this$remAssign`, var10002 as Tag);
   }

   @JvmStatic
   public inline operator fun String.remAssign(bool: Boolean) {
      val var10002: ByteTag = ByteTag.valueOf((byte)(if (bool) 1 else 0));
      arg0.put(`$this$remAssign`, var10002 as Tag);
   }

   @JvmStatic
   public inline fun compound(block: (NbtCompoundBuilder) -> Unit): CompoundTag {
      val var3: NbtCompoundBuilder = box-impl(constructor-impl(new CompoundTag()));
      block.invoke(var3);
      return var3.unbox-impl();
   }

   @JvmStatic
   public inline fun list(block: (NbtListBuilder) -> Unit): ListTag {
      val var3: NbtListBuilder = NbtListBuilder.box-impl(NbtListBuilder.constructor-impl(new ListTag()));
      block.invoke(var3);
      return var3.unbox-impl();
   }

   @JvmStatic
   public inline fun list(vararg elements: Tag, block: (NbtListBuilder) -> Unit): ListTag {
      val var4: ListTag = NbtListBuilder.constructor-impl(new ListTag());
      var4.addAll(ArraysKt.toList(elements));
      block.invoke(NbtListBuilder.box-impl(var4));
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
   public open fun toString(): String {
      return "NbtCompoundBuilder(tag=$arg0)";
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
      if (other !is NbtCompoundBuilder) {
         return false;
      } else {
         return arg0 == (other as NbtCompoundBuilder).unbox-impl();
      }
   }

   override fun equals(other: Any): Boolean {
      return equals-impl(this.tag, other);
   }

   @JvmStatic
   fun `constructor-impl`(tag: CompoundTag): CompoundTag {
      return tag;
   }

   @JvmStatic
   fun `equals-impl0`(p1: CompoundTag, p2: CompoundTag): Boolean {
      return p1 == p2;
   }
}

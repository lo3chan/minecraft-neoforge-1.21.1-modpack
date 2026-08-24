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

@NBTDslMarker
@SourceDebugExtension(["SMAP\nNBTDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n+ 2 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtListBuilder\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,216:1\n21#1:217\n18#1:218\n157#2,2:219\n1#3:221\n1563#4:222\n1634#4,3:223\n1563#4:226\n1634#4,3:227\n1563#4:230\n1634#4,3:231\n1563#4:234\n1634#4,3:235\n*S KotlinDebug\n*F\n+ 1 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n*L\n14#1:217\n15#1:218\n28#1:219,2\n34#1:222\n34#1:223,3\n45#1:226\n45#1:227,3\n49#1:230\n49#1:231,3\n53#1:234\n53#1:235,3\n*E\n"])
public object NBTBuilder {
   public inline operator fun invoke(block: (NbtCompoundBuilder) -> Unit): CompoundTag {
      val var5: NbtCompoundBuilder = NbtCompoundBuilder.box-impl(NbtCompoundBuilder.constructor-impl(new CompoundTag()));
      block.invoke(var5);
      return var5.unbox-impl();
   }

   public inline operator fun invoke(tag: CompoundTag, block: (NbtCompoundBuilder) -> Unit): CompoundTag {
      val var6: NbtCompoundBuilder = NbtCompoundBuilder.box-impl(NbtCompoundBuilder.constructor-impl(tag));
      block.invoke(var6);
      return var6.unbox-impl();
   }

   public inline fun use(tag: CompoundTag, block: (NbtCompoundBuilder) -> Unit): CompoundTag {
      val var4: NbtCompoundBuilder = NbtCompoundBuilder.box-impl(NbtCompoundBuilder.constructor-impl(tag));
      block.invoke(var4);
      return var4.unbox-impl();
   }

   public inline fun compound(block: (NbtCompoundBuilder) -> Unit): CompoundTag {
      val var3: NbtCompoundBuilder = NbtCompoundBuilder.box-impl(NbtCompoundBuilder.constructor-impl(new CompoundTag()));
      block.invoke(var3);
      return var3.unbox-impl();
   }

   public inline fun list(block: (NbtListBuilder) -> Unit): ListTag {
      val var3: NbtListBuilder = NbtListBuilder.box-impl(NbtListBuilder.constructor-impl(new ListTag()));
      block.invoke(var3);
      return var3.unbox-impl();
   }

   public inline fun list(vararg elements: Tag, block: (NbtListBuilder) -> Unit): ListTag {
      val var4: ListTag = NbtListBuilder.constructor-impl(new ListTag());
      var4.addAll(ArraysKt.toList(elements));
      block.invoke(NbtListBuilder.box-impl(var4));
      return var4;
   }

   public inline fun list(vararg elements: Tag): ListTag {
      val var3: ListTag = new ListTag();
      CollectionsKt.addAll(var3 as java.util.Collection, elements);
      return var3;
   }

   public inline fun list(elements: Collection<Tag>): ListTag {
      val var3: ListTag = new ListTag();
      var3.addAll(elements);
      return var3;
   }

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

   public inline fun double(value: Number): DoubleTag {
      val var10000: DoubleTag = DoubleTag.valueOf(value.doubleValue());
      return var10000;
   }

   public inline fun float(value: Number): FloatTag {
      val var10000: FloatTag = FloatTag.valueOf(value.floatValue());
      return var10000;
   }

   public inline fun long(value: Number): LongTag {
      val var10000: LongTag = LongTag.valueOf(value.longValue());
      return var10000;
   }

   public inline fun int(value: Number): IntTag {
      val var10000: IntTag = IntTag.valueOf(value.intValue());
      return var10000;
   }

   public inline fun short(value: Number): ShortTag {
      val var10000: ShortTag = ShortTag.valueOf(value.shortValue());
      return var10000;
   }

   public inline fun byte(value: Number): ByteTag {
      val var10000: ByteTag = ByteTag.valueOf(value.byteValue());
      return var10000;
   }

   public inline fun string(value: String): StringTag {
      val var10000: StringTag = StringTag.valueOf(value);
      return var10000;
   }

   public inline fun byteArray(value: Collection<Number>): ByteArrayTag {
      val `$this$map$iv`: java.lang.Iterable = value;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(value, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).byteValue());
      }

      return new ByteArrayTag(`destination$iv$iv` as java.util.List);
   }

   public inline fun byteArray(value: IntArray): ByteArrayTag {
      var var3: Int = 0;
      val var4: Int = value.length;

      val var5: ByteArray;
      for (var5 = new byte[value.length]; var3 < var4; var3++) {
         var5[var3] = (byte)value[var3];
      }

      return new ByteArrayTag(var5);
   }

   public inline fun byteArray(value: ByteArray): ByteArrayTag {
      return new ByteArrayTag(value);
   }

   public inline fun byteArray(): ByteArrayTag {
      return new ByteArrayTag(new byte[0]);
   }

   public inline fun longArray(value: Collection<Number>): LongArrayTag {
      val `$this$map$iv`: java.lang.Iterable = value;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(value, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).longValue());
      }

      return new LongArrayTag(`destination$iv$iv` as java.util.List);
   }

   public inline fun longArray(value: IntArray): LongArrayTag {
      var var3: Int = 0;
      val var4: Int = value.length;

      val var5: LongArray;
      for (var5 = new long[value.length]; var3 < var4; var3++) {
         var5[var3] = value[var3];
      }

      return new LongArrayTag(var5);
   }

   public inline fun longArray(value: LongArray): LongArrayTag {
      return new LongArrayTag(value);
   }

   public inline fun longArray(): LongArrayTag {
      return new LongArrayTag(new long[0]);
   }

   public inline fun intArray(value: Collection<Number>): IntArrayTag {
      val `$this$map$iv`: java.lang.Iterable = value;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(value, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.Number).intValue());
      }

      return new IntArrayTag(`destination$iv$iv` as java.util.List);
   }

   public inline fun intArray(value: IntArray): IntArrayTag {
      return new IntArrayTag(value);
   }
}

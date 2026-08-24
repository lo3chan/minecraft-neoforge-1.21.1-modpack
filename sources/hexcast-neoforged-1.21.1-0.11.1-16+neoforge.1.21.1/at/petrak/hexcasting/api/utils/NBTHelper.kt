@file:JvmName(name = "NBTHelper")

@file:SourceDebugExtension(["SMAP\nNBTHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NBTHelper.kt\nat/petrak/hexcasting/api/utils/NBTHelper\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,290:1\n16#1,3:291\n16#1,3:294\n16#1,3:297\n16#1,3:300\n16#1,3:303\n16#1,3:306\n16#1,3:309\n16#1,3:312\n13#1,6:315\n13#1,6:321\n13#1,6:327\n13#1,6:333\n13#1,6:339\n13#1,6:345\n13#1,6:351\n13#1,6:357\n189#1,2:364\n189#1,2:366\n189#1,2:368\n189#1,2:370\n189#1,2:372\n189#1,2:374\n189#1,2:376\n189#1,2:378\n189#1,2:380\n189#1,2:382\n189#1,2:384\n189#1,2:386\n189#1,2:388\n189#1,2:390\n189#1,2:392\n1#2:363\n*S KotlinDebug\n*F\n+ 1 NBTHelper.kt\nat/petrak/hexcasting/api/utils/NBTHelper\n*L\n13#1:291,3\n77#1:294,3\n81#1:297,3\n85#1:300,3\n89#1:303,3\n93#1:306,3\n97#1:309,3\n101#1:312,3\n103#1:315,6\n104#1:321,6\n105#1:327,6\n107#1:333,6\n109#1:339,6\n111#1:345,6\n112#1:351,6\n113#1:357,6\n222#1:364,2\n223#1:366,2\n224#1:368,2\n225#1:370,2\n226#1:372,2\n227#1:374,2\n228#1:376,2\n230#1:378,2\n231#1:380,2\n232#1:382,2\n234#1:384,2\n236#1:386,2\n239#1:388,2\n243#1:390,2\n285#1:392,2\n*E\n"])

package at.petrak.hexcasting.api.utils

import java.util.UUID
import java.util.function.Consumer
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.NumericTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

public final val asBoolean: Boolean
   public final get() {
      return getAsByte(`$this$asBoolean`) == 0;
   }


public final val asByte: Byte
   public final get() {
      return if ((`$this$asByte` as? NumericTag) != null) (`$this$asByte` as? NumericTag).getAsByte() else 0;
   }


public final val asShort: Short
   public final get() {
      return if ((`$this$asShort` as? NumericTag) != null) (`$this$asShort` as? NumericTag).getAsShort() else 0;
   }


public final val asInt: Int
   public final get() {
      return if ((`$this$asInt` as? NumericTag) != null) (`$this$asInt` as? NumericTag).getAsInt() else 0;
   }


public final val asLong: Long
   public final get() {
      return if ((`$this$asLong` as? NumericTag) != null) (`$this$asLong` as? NumericTag).getAsLong() else 0L;
   }


public final val asFloat: Float
   public final get() {
      return if ((`$this$asFloat` as? NumericTag) != null) (`$this$asFloat` as? NumericTag).getAsFloat() else 0.0F;
   }


public final val asDouble: Double
   public final get() {
      return if ((`$this$asDouble` as? NumericTag) != null) (`$this$asDouble` as? NumericTag).getAsDouble() else 0.0;
   }


public final val asLongArray: LongArray
   public final get() {
      val var10000: LongArray;
      if (`$this$asLongArray` is LongArrayTag) {
         var10000 = (`$this$asLongArray` as LongArrayTag).getAsLongArray();
      } else if (`$this$asLongArray` is IntArrayTag) {
         val array: IntArray = (`$this$asLongArray` as IntArrayTag).getAsIntArray();
         var var3: Int = 0;
         val var4: Int = array.length;

         val var5: LongArray;
         for (var5 = new long[array.length]; var3 < var4; var3++) {
            var5[var3] = array[var3];
         }

         var10000 = var5;
      } else if (`$this$asLongArray` is ByteArrayTag) {
         val var7: ByteArray = (`$this$asLongArray` as ByteArrayTag).getAsByteArray();
         var var8: Int = 0;
         val var9: Int = var7.length;

         val var10: LongArray;
         for (var10 = new long[array.length]; var8 < var9; var8++) {
            var10[var8] = var7[var8];
         }

         var10000 = var10;
      } else {
         var10000 = new long[0];
      }

      return var10000;
   }


public final val asIntArray: IntArray
   public final get() {
      val var10000: IntArray;
      if (`$this$asIntArray` is IntArrayTag) {
         var10000 = (`$this$asIntArray` as IntArrayTag).getAsIntArray();
      } else if (`$this$asIntArray` is LongArrayTag) {
         val array: LongArray = (`$this$asIntArray` as LongArrayTag).getAsLongArray();
         var var3: Int = 0;
         val var4: Int = array.length;

         val var5: IntArray;
         for (var5 = new int[array.length]; var3 < var4; var3++) {
            var5[var3] = (int)array[var3];
         }

         var10000 = var5;
      } else if (`$this$asIntArray` is ByteArrayTag) {
         val var7: ByteArray = (`$this$asIntArray` as ByteArrayTag).getAsByteArray();
         var var8: Int = 0;
         val var9: Int = var7.length;

         val var10: IntArray;
         for (var10 = new int[array.length]; var8 < var9; var8++) {
            var10[var8] = var7[var8];
         }

         var10000 = var10;
      } else {
         var10000 = new int[0];
      }

      return var10000;
   }


public final val asByteArray: ByteArray
   public final get() {
      val var10000: ByteArray;
      if (`$this$asByteArray` is ByteArrayTag) {
         var10000 = (`$this$asByteArray` as ByteArrayTag).getAsByteArray();
      } else if (`$this$asByteArray` is LongArrayTag) {
         val array: LongArray = (`$this$asByteArray` as LongArrayTag).getAsLongArray();
         var var3: Int = 0;
         val var4: Int = array.length;

         val var5: ByteArray;
         for (var5 = new byte[array.length]; var3 < var4; var3++) {
            var5[var3] = (byte)array[var3];
         }

         var10000 = var5;
      } else if (`$this$asByteArray` is IntArrayTag) {
         val var7: IntArray = (`$this$asByteArray` as IntArrayTag).getAsIntArray();
         var var8: Int = 0;
         val var9: Int = var7.length;

         val var10: ByteArray;
         for (var10 = new byte[array.length]; var8 < var9; var8++) {
            var10[var8] = (byte)var7[var8];
         }

         var10000 = var10;
      } else {
         var10000 = new byte[0];
      }

      return var10000;
   }


public final val asCompound: CompoundTag
   public final get() {
      var var10000: CompoundTag = `$this$asCompound` as? CompoundTag;
      if ((`$this$asCompound` as? CompoundTag) == null) {
         var10000 = new CompoundTag();
      }

      return var10000;
   }


public final val asList: ListTag
   public final get() {
      var var10000: ListTag = `$this$asList` as? ListTag;
      if ((`$this$asList` as? ListTag) == null) {
         var10000 = new ListTag();
      }

      return var10000;
   }


public final val asUUID: UUID
   public final get() {
      val var10000: UUID;
      if (`$this$asUUID` is IntArrayTag && (`$this$asUUID` as IntArrayTag).size() == 4) {
         var10000 = NbtUtils.loadUUID(`$this$asUUID`);
      } else {
         var10000 = new UUID(0L, 0L);
      }

      return var10000;
   }


private inline fun <T : Any, K, E> T?.getIf(key: K, predicate: (T?, K) -> Boolean, get: (T, K) -> E): E? {
   return (E)(if (`$this$getIf` != null && predicate.invoke(`$this$getIf`, key)) get.invoke(`$this$getIf`, key) else null);
}

private inline fun <T : Any, K, E> T?.getIf(key: K, predicate: (T?, K) -> Boolean, get: (T, K) -> E, default: E): E {
   return (E)(if (`$this$getIf` != null && predicate.invoke(`$this$getIf`, key)) get.invoke(`$this$getIf`, key) else var4);
}

public fun CompoundTag?.hasNumber(key: String): Boolean {
   return contains(`$this$hasNumber`, key, (byte)99);
}

public fun CompoundTag?.hasByte(key: String): Boolean {
   return contains(`$this$hasByte`, key, (byte)1);
}

public fun CompoundTag?.hasShort(key: String): Boolean {
   return contains(`$this$hasShort`, key, (byte)2);
}

public fun CompoundTag?.hasInt(key: String): Boolean {
   return contains(`$this$hasInt`, key, (byte)3);
}

public fun CompoundTag?.hasLong(key: String): Boolean {
   return contains(`$this$hasLong`, key, (byte)4);
}

public fun CompoundTag?.hasFloat(key: String): Boolean {
   return contains(`$this$hasFloat`, key, (byte)5);
}

public fun CompoundTag?.hasDouble(key: String): Boolean {
   return contains(`$this$hasDouble`, key, (byte)6);
}

public fun CompoundTag?.hasLongArray(key: String): Boolean {
   return contains(`$this$hasLongArray`, key, (byte)12);
}

public fun CompoundTag?.hasIntArray(key: String): Boolean {
   return contains(`$this$hasIntArray`, key, (byte)11);
}

public fun CompoundTag?.hasByteArray(key: String): Boolean {
   return contains(`$this$hasByteArray`, key, (byte)7);
}

public fun CompoundTag?.hasCompound(key: String): Boolean {
   return contains(`$this$hasCompound`, key, (byte)10);
}

public fun CompoundTag?.hasString(key: String): Boolean {
   return contains(`$this$hasString`, key, (byte)8);
}

public fun CompoundTag?.hasList(key: String): Boolean {
   return contains(`$this$hasList`, key, (byte)9);
}

public fun CompoundTag?.hasList(key: String, objType: Int): Boolean {
   return hasList(`$this$hasList`, key, (byte)objType);
}

public fun CompoundTag?.hasList(key: String, objType: Byte): Boolean {
   if (!hasList(`$this$hasList`, key)) {
      return false;
   } else {
      val var10000: Tag = get(`$this$hasList`, key);
      return (var10000 as ListTag).getElementType() == objType || (var10000 as ListTag).getElementType() == 0;
   }
}

public fun CompoundTag?.hasUUID(key: String): Boolean {
   return `$this$hasUUID` != null && `$this$hasUUID`.hasUUID(key);
}

public fun CompoundTag?.contains(key: String, id: Byte): Boolean {
   return contains(`$this$contains`, key, (int)id);
}

public fun CompoundTag?.contains(key: String, id: Int): Boolean {
   return `$this$contains` != null && `$this$contains`.contains(key, id);
}

public fun CompoundTag?.contains(key: String): Boolean {
   return `$this$contains` != null && `$this$contains`.contains(key);
}

public fun CompoundTag?.putBoolean(key: String, value: Boolean): Unit? {
   val var10000: Unit;
   if (`$this$putBoolean` != null) {
      `$this$putBoolean`.putBoolean(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putByte(key: String, value: Byte): Unit? {
   val var10000: Unit;
   if (`$this$putByte` != null) {
      `$this$putByte`.putByte(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putShort(key: String, value: Short): Unit? {
   val var10000: Unit;
   if (`$this$putShort` != null) {
      `$this$putShort`.putShort(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putInt(key: String, value: Int): Unit? {
   val var10000: Unit;
   if (`$this$putInt` != null) {
      `$this$putInt`.putInt(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putLong(key: String, value: Long): Unit? {
   val var10000: Unit;
   if (`$this$putLong` != null) {
      `$this$putLong`.putLong(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putFloat(key: String, value: Float): Unit? {
   val var10000: Unit;
   if (`$this$putFloat` != null) {
      `$this$putFloat`.putFloat(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putDouble(key: String, value: Double): Unit? {
   val var10000: Unit;
   if (`$this$putDouble` != null) {
      `$this$putDouble`.putDouble(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putLongArray(key: String, value: LongArray): Unit? {
   val var10000: Unit;
   if (`$this$putLongArray` != null) {
      `$this$putLongArray`.putLongArray(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putIntArray(key: String, value: IntArray): Unit? {
   val var10000: Unit;
   if (`$this$putIntArray` != null) {
      `$this$putIntArray`.putIntArray(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putByteArray(key: String, value: ByteArray): Unit? {
   val var10000: Unit;
   if (`$this$putByteArray` != null) {
      `$this$putByteArray`.putByteArray(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putCompound(key: String, value: CompoundTag): Tag? {
   return put(`$this$putCompound`, key, value as Tag);
}

public fun CompoundTag?.putString(key: String, value: String): Unit? {
   val var10000: Unit;
   if (`$this$putString` != null) {
      `$this$putString`.putString(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.putList(key: String, value: ListTag): Tag? {
   return put(`$this$putList`, key, value as Tag);
}

public fun CompoundTag?.putUUID(key: String, value: UUID): Unit? {
   val var10000: Unit;
   if (`$this$putUUID` != null) {
      `$this$putUUID`.putUUID(key, value);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

public fun CompoundTag?.put(key: String, value: Tag): Tag? {
   return if (`$this$put` != null) `$this$put`.put(key, value) else null;
}

public fun CompoundTag?.remove(key: String): Unit? {
   val var10000: Unit;
   if (`$this$remove` != null) {
      `$this$remove`.remove(key);
      var10000 = Unit.INSTANCE;
   } else {
      var10000 = null;
   }

   return var10000;
}

@JvmOverloads
public fun CompoundTag?.getBoolean(key: String, defaultExpected: Boolean = false): Boolean {
   return if (`$this$getBoolean` != null && hasNumber(`$this$getBoolean`, key)) `$this$getBoolean`.getBoolean(key) else defaultExpected;
}

@JvmSynthetic
fun `getBoolean$default`(var0: CompoundTag, var1: java.lang.String, var2: Boolean, var3: Int, var4: Any): Boolean {
   if ((var3 and 2) != 0) {
      var2 = false;
   }

   return getBoolean(var0, var1, var2);
}

@JvmOverloads
public fun CompoundTag?.getByte(key: String, defaultExpected: Byte = 0): Byte {
   return if (`$this$getByte` != null && hasNumber(`$this$getByte`, key)) `$this$getByte`.getByte(key) else defaultExpected;
}

@JvmSynthetic
fun `getByte$default`(var0: CompoundTag, var1: java.lang.String, var2: Byte, var3: Int, var4: Any): Byte {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getByte(var0, var1, var2);
}

@JvmOverloads
public fun CompoundTag?.getShort(key: String, defaultExpected: Short = 0): Short {
   return if (`$this$getShort` != null && hasNumber(`$this$getShort`, key)) `$this$getShort`.getShort(key) else defaultExpected;
}

@JvmSynthetic
fun `getShort$default`(var0: CompoundTag, var1: java.lang.String, var2: Short, var3: Int, var4: Any): Short {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getShort(var0, var1, var2);
}

@JvmOverloads
public fun CompoundTag?.getInt(key: String, defaultExpected: Int = 0): Int {
   return if (`$this$getInt` != null && hasNumber(`$this$getInt`, key)) `$this$getInt`.getInt(key) else defaultExpected;
}

@JvmSynthetic
fun `getInt$default`(var0: CompoundTag, var1: java.lang.String, var2: Int, var3: Int, var4: Any): Int {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getInt(var0, var1, var2);
}

@JvmOverloads
public fun CompoundTag?.getLong(key: String, defaultExpected: Long = 0L): Long {
   return if (`$this$getLong` != null && hasNumber(`$this$getLong`, key)) `$this$getLong`.getLong(key) else defaultExpected;
}

@JvmSynthetic
fun `getLong$default`(var0: CompoundTag, var1: java.lang.String, var2: Long, var4: Int, var5: Any): Long {
   if ((var4 and 2) != 0) {
      var2 = 0L;
   }

   return getLong(var0, var1, var2);
}

@JvmOverloads
public fun CompoundTag?.getFloat(key: String, defaultExpected: Float = 0.0F): Float {
   return if (`$this$getFloat` != null && hasNumber(`$this$getFloat`, key)) `$this$getFloat`.getFloat(key) else defaultExpected;
}

@JvmSynthetic
fun `getFloat$default`(var0: CompoundTag, var1: java.lang.String, var2: Float, var3: Int, var4: Any): Float {
   if ((var3 and 2) != 0) {
      var2 = 0.0F;
   }

   return getFloat(var0, var1, var2);
}

@JvmOverloads
public fun CompoundTag?.getDouble(key: String, defaultExpected: Double = 0.0): Double {
   return if (`$this$getDouble` != null && hasNumber(`$this$getDouble`, key)) `$this$getDouble`.getDouble(key) else defaultExpected;
}

@JvmSynthetic
fun `getDouble$default`(var0: CompoundTag, var1: java.lang.String, var2: Double, var4: Int, var5: Any): Double {
   if ((var4 and 2) != 0) {
      var2 = 0.0;
   }

   return getDouble(var0, var1, var2);
}

public fun CompoundTag?.getLongArray(key: String): LongArray? {
   return if (`$this$getLongArray` != null && hasLongArray(`$this$getLongArray`, key)) `$this$getLongArray`.getLongArray(key) else null;
}

public fun CompoundTag?.getIntArray(key: String): IntArray? {
   return if (`$this$getIntArray` != null && hasIntArray(`$this$getIntArray`, key)) `$this$getIntArray`.getIntArray(key) else null;
}

public fun CompoundTag?.getByteArray(key: String): ByteArray? {
   return if (`$this$getByteArray` != null && hasByteArray(`$this$getByteArray`, key)) `$this$getByteArray`.getByteArray(key) else null;
}

public fun CompoundTag?.getCompound(key: String): CompoundTag? {
   return if (`$this$getCompound` != null && hasCompound(`$this$getCompound`, key)) `$this$getCompound`.getCompound(key) else null;
}

public fun CompoundTag?.getString(key: String): String? {
   return if (`$this$getString` != null && hasString(`$this$getString`, key)) `$this$getString`.getString(key) else null;
}

public fun CompoundTag?.getList(key: String, objType: Byte): ListTag? {
   return getList(`$this$getList`, key, (int)objType);
}

public fun CompoundTag?.getList(key: String, objType: Int): ListTag? {
   return if (`$this$getList` != null && hasList(`$this$getList`, key, objType)) `$this$getList`.getList(key, objType) else null;
}

public fun CompoundTag?.getUUID(key: String): UUID? {
   return if (`$this$getUUID` != null && hasUUID(`$this$getUUID`, key)) `$this$getUUID`.getUUID(key) else null;
}

public fun CompoundTag?.get(key: String): Tag? {
   return if (`$this$get` != null && contains(`$this$get`, key)) `$this$get`.get(key) else null;
}

@JvmName(name = "getListByByte")
@JvmSynthetic
public fun CompoundTag.getList(key: String, objType: Byte): ListTag {
   val var10000: ListTag = `$this$getList`.getList(key, objType);
   return var10000;
}

public fun CompoundTag.getOrCreateCompound(key: String): CompoundTag {
   var var10000: CompoundTag = `$this$getOrCreateCompound`.getCompound(key);
   if (var10000 == null) {
      val var2: CompoundTag = new CompoundTag();
      putCompound(`$this$getOrCreateCompound`, key, var2);
      var10000 = var2;
   }

   return var10000;
}

public fun CompoundTag.getOrCreateList(key: String, objType: Byte): ListTag {
   return getOrCreateList(`$this$getOrCreateList`, key, (int)objType);
}

public fun CompoundTag.getOrCreateList(key: String, objType: Int): ListTag {
   val var10000: ListTag;
   if (hasList(`$this$getOrCreateList`, key, objType)) {
      var10000 = `$this$getOrCreateList`.getList(key, objType);
   } else {
      val var3: ListTag = new ListTag();
      putList(`$this$getOrCreateList`, key, var3);
      var10000 = var3;
   }

   return var10000;
}

private fun ItemStack.customDataTag(): CompoundTag {
   val var10000: CompoundTag = (`$this$customDataTag`.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY) as CustomData).copyTag();
   return var10000;
}

private inline fun ItemStack.updateCustomData(crossinline update: (CompoundTag) -> Unit) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$updateCustomData`, new Consumer(update) {
      {
         this.$update = `$update`;
      }

      public final void accept(CompoundTag tag) {
         val var10000: Function1 = this.$update;
         var10000.invoke(tag);
      }
   });
}

public fun ItemStack.hasNumber(key: String): Boolean {
   return hasNumber(customDataTag(`$this$hasNumber`), key);
}

public fun ItemStack.hasByte(key: String): Boolean {
   return hasByte(customDataTag(`$this$hasByte`), key);
}

public fun ItemStack.hasShort(key: String): Boolean {
   return hasShort(customDataTag(`$this$hasShort`), key);
}

public fun ItemStack.hasInt(key: String): Boolean {
   return hasInt(customDataTag(`$this$hasInt`), key);
}

public fun ItemStack.hasLong(key: String): Boolean {
   return hasLong(customDataTag(`$this$hasLong`), key);
}

public fun ItemStack.hasFloat(key: String): Boolean {
   return hasFloat(customDataTag(`$this$hasFloat`), key);
}

public fun ItemStack.hasDouble(key: String): Boolean {
   return hasDouble(customDataTag(`$this$hasDouble`), key);
}

public fun ItemStack.hasLongArray(key: String): Boolean {
   return hasLongArray(customDataTag(`$this$hasLongArray`), key);
}

public fun ItemStack.hasIntArray(key: String): Boolean {
   return hasIntArray(customDataTag(`$this$hasIntArray`), key);
}

public fun ItemStack.hasByteArray(key: String): Boolean {
   return hasByteArray(customDataTag(`$this$hasByteArray`), key);
}

public fun ItemStack.hasCompound(key: String): Boolean {
   return hasCompound(customDataTag(`$this$hasCompound`), key);
}

public fun ItemStack.hasString(key: String): Boolean {
   return hasString(customDataTag(`$this$hasString`), key);
}

public fun ItemStack.hasList(key: String): Boolean {
   return hasList(customDataTag(`$this$hasList`), key);
}

public fun ItemStack.hasList(key: String, objType: Int): Boolean {
   return hasList(customDataTag(`$this$hasList`), key, objType);
}

public fun ItemStack.hasList(key: String, objType: Byte): Boolean {
   return hasList(customDataTag(`$this$hasList`), key, objType);
}

public fun ItemStack.hasUUID(key: String): Boolean {
   return customDataTag(`$this$hasUUID`).hasUUID(key);
}

@JvmName(name = "contains")
public fun ItemStack.containsTag(key: String): Boolean {
   return customDataTag(`$this$containsTag`).contains(key);
}

@JvmName(name = "contains")
public fun ItemStack.containsTag(key: String, id: Byte): Boolean {
   return contains(customDataTag(`$this$containsTag`), key, id);
}

@JvmName(name = "contains")
public fun ItemStack.containsTag(key: String, id: Int): Boolean {
   return customDataTag(`$this$containsTag`).contains(key, id);
}

public fun ItemStack.putBoolean(key: String, value: Boolean) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putBoolean`, new NBTHelper$putBoolean$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putByte(key: String, value: Byte) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putByte`, new NBTHelper$putByte$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putShort(key: String, value: Short) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putShort`, new NBTHelper$putShort$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putInt(key: String, value: Int) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putInt`, new NBTHelper$putInt$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putLong(key: String, value: Long) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putLong`, new NBTHelper$putLong$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putFloat(key: String, value: Float) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putFloat`, new NBTHelper$putFloat$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putDouble(key: String, value: Double) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putDouble`, new NBTHelper$putDouble$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putLongArray(key: String, value: LongArray) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putLongArray`, new NBTHelper$putLongArray$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putIntArray(key: String, value: IntArray) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putIntArray`, new NBTHelper$putIntArray$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putByteArray(key: String, value: ByteArray) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putByteArray`, new NBTHelper$putByteArray$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putCompound(key: String, value: CompoundTag) {
   put(`$this$putCompound`, key, value as Tag);
}

public fun ItemStack.putString(key: String, value: String) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putString`, new NBTHelper$putString$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.putList(key: String, value: ListTag) {
   put(`$this$putList`, key, value as Tag);
}

public fun ItemStack.putUUID(key: String, value: UUID) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putUUID`, new NBTHelper$putUUID$$inlined$updateCustomData$1(key, value));
}

@JvmName(name = "put")
public fun ItemStack.putTag(key: String, value: Tag) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$putTag`, new NBTHelper$putTag$$inlined$updateCustomData$1(key, value));
}

public fun ItemStack.remove(key: String) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$remove`, new NBTHelper$remove$$inlined$updateCustomData$1(key));
}

@JvmOverloads
public fun ItemStack.getBoolean(key: String, defaultExpected: Boolean = false): Boolean {
   return getBoolean(customDataTag(`$this$getBoolean`), key, defaultExpected);
}

@JvmSynthetic
fun `getBoolean$default`(var0: ItemStack, var1: java.lang.String, var2: Boolean, var3: Int, var4: Any): Boolean {
   if ((var3 and 2) != 0) {
      var2 = false;
   }

   return getBoolean(var0, var1, var2);
}

@JvmOverloads
public fun ItemStack.getByte(key: String, defaultExpected: Byte = 0): Byte {
   return getByte(customDataTag(`$this$getByte`), key, defaultExpected);
}

@JvmSynthetic
fun `getByte$default`(var0: ItemStack, var1: java.lang.String, var2: Byte, var3: Int, var4: Any): Byte {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getByte(var0, var1, var2);
}

@JvmOverloads
public fun ItemStack.getShort(key: String, defaultExpected: Short = 0): Short {
   return getShort(customDataTag(`$this$getShort`), key, defaultExpected);
}

@JvmSynthetic
fun `getShort$default`(var0: ItemStack, var1: java.lang.String, var2: Short, var3: Int, var4: Any): Short {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getShort(var0, var1, var2);
}

@JvmOverloads
public fun ItemStack.getInt(key: String, defaultExpected: Int = 0): Int {
   return getInt(customDataTag(`$this$getInt`), key, defaultExpected);
}

@JvmSynthetic
fun `getInt$default`(var0: ItemStack, var1: java.lang.String, var2: Int, var3: Int, var4: Any): Int {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getInt(var0, var1, var2);
}

@JvmOverloads
public fun ItemStack.getLong(key: String, defaultExpected: Long = 0L): Long {
   return getLong(customDataTag(`$this$getLong`), key, defaultExpected);
}

@JvmSynthetic
fun `getLong$default`(var0: ItemStack, var1: java.lang.String, var2: Long, var4: Int, var5: Any): Long {
   if ((var4 and 2) != 0) {
      var2 = 0L;
   }

   return getLong(var0, var1, var2);
}

@JvmOverloads
public fun ItemStack.getFloat(key: String, defaultExpected: Float = 0.0F): Float {
   return getFloat(customDataTag(`$this$getFloat`), key, defaultExpected);
}

@JvmSynthetic
fun `getFloat$default`(var0: ItemStack, var1: java.lang.String, var2: Float, var3: Int, var4: Any): Float {
   if ((var3 and 2) != 0) {
      var2 = 0.0F;
   }

   return getFloat(var0, var1, var2);
}

@JvmOverloads
public fun ItemStack.getDouble(key: String, defaultExpected: Double = 0.0): Double {
   return getDouble(customDataTag(`$this$getDouble`), key, defaultExpected);
}

@JvmSynthetic
fun `getDouble$default`(var0: ItemStack, var1: java.lang.String, var2: Double, var4: Int, var5: Any): Double {
   if ((var4 and 2) != 0) {
      var2 = 0.0;
   }

   return getDouble(var0, var1, var2);
}

public fun ItemStack.getLongArray(key: String): LongArray {
   return customDataTag(`$this$getLongArray`).getLongArray(key);
}

public fun ItemStack.getIntArray(key: String): IntArray {
   return customDataTag(`$this$getIntArray`).getIntArray(key);
}

public fun ItemStack.getByteArray(key: String): ByteArray {
   return customDataTag(`$this$getByteArray`).getByteArray(key);
}

public fun ItemStack.getCompound(key: String): CompoundTag {
   return customDataTag(`$this$getCompound`).getCompound(key);
}

public fun ItemStack.getString(key: String): String {
   return customDataTag(`$this$getString`).getString(key);
}

public fun ItemStack.getList(key: String, objType: Int): ListTag {
   return customDataTag(`$this$getList`).getList(key, objType);
}

public fun ItemStack.getUUID(key: String): UUID {
   return customDataTag(`$this$getUUID`).getUUID(key);
}

@JvmName(name = "get")
public fun ItemStack.getTag(key: String): Tag? {
   return customDataTag(`$this$getTag`).get(key);
}

public fun ItemStack.getOrCreateCompound(key: String): CompoundTag {
   return getOrCreateCompound(customDataTag(`$this$getOrCreateCompound`), key);
}

public fun ItemStack.getOrCreateList(key: String, objType: Byte): ListTag {
   return getOrCreateList(customDataTag(`$this$getOrCreateList`), key, objType);
}

public fun ItemStack.getOrCreateList(key: String, objType: Int): ListTag {
   return getOrCreateList(customDataTag(`$this$getOrCreateList`), key, objType);
}

public fun ItemStack.updateCompound(key: String, updater: Consumer<CompoundTag>) {
   CustomData.update(DataComponents.CUSTOM_DATA, `$this$updateCompound`, new NBTHelper$updateCompound$$inlined$updateCustomData$1(key, updater));
}

@JvmOverloads
fun CompoundTag?.getBoolean(key: java.lang.String): Boolean {
   return getBoolean$default(`$this$getBoolean`, key, false, 2, null);
}

@JvmOverloads
fun CompoundTag?.getByte(key: java.lang.String): Byte {
   return getByte$default(`$this$getByte`, key, (byte)0, 2, null);
}

@JvmOverloads
fun CompoundTag?.getShort(key: java.lang.String): Short {
   return getShort$default(`$this$getShort`, key, (short)0, 2, null);
}

@JvmOverloads
fun CompoundTag?.getInt(key: java.lang.String): Int {
   return getInt$default(`$this$getInt`, key, 0, 2, null);
}

@JvmOverloads
fun CompoundTag?.getLong(key: java.lang.String): Long {
   return getLong$default(`$this$getLong`, key, 0L, 2, null);
}

@JvmOverloads
fun CompoundTag?.getFloat(key: java.lang.String): Float {
   return getFloat$default(`$this$getFloat`, key, 0.0F, 2, null);
}

@JvmOverloads
fun CompoundTag?.getDouble(key: java.lang.String): Double {
   return getDouble$default(`$this$getDouble`, key, 0.0, 2, null);
}

@JvmOverloads
fun ItemStack.getBoolean(key: java.lang.String): Boolean {
   return getBoolean$default(`$this$getBoolean`, key, false, 2, null);
}

@JvmOverloads
fun ItemStack.getByte(key: java.lang.String): Byte {
   return getByte$default(`$this$getByte`, key, (byte)0, 2, null);
}

@JvmOverloads
fun ItemStack.getShort(key: java.lang.String): Short {
   return getShort$default(`$this$getShort`, key, (short)0, 2, null);
}

@JvmOverloads
fun ItemStack.getInt(key: java.lang.String): Int {
   return getInt$default(`$this$getInt`, key, 0, 2, null);
}

@JvmOverloads
fun ItemStack.getLong(key: java.lang.String): Long {
   return getLong$default(`$this$getLong`, key, 0L, 2, null);
}

@JvmOverloads
fun ItemStack.getFloat(key: java.lang.String): Float {
   return getFloat$default(`$this$getFloat`, key, 0.0F, 2, null);
}

@JvmOverloads
fun ItemStack.getDouble(key: java.lang.String): Double {
   return getDouble$default(`$this$getDouble`, key, 0.0, 2, null);
}

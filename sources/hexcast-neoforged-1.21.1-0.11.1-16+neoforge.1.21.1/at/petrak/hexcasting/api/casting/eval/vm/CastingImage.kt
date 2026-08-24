package at.petrak.hexcasting.api.casting.eval.vm

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.HexUtils
import at.petrak.hexcasting.api.utils.NBTBuilder
import at.petrak.hexcasting.api.utils.NBTHelper
import at.petrak.hexcasting.api.utils.NbtCompoundBuilder
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagType
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity

@SourceDebugExtension(["SMAP\nCastingImage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CastingImage.kt\nat/petrak/hexcasting/api/casting/eval/vm/CastingImage\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n+ 4 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n*L\n1#1,137:1\n1563#2:138\n1634#2,3:139\n1563#2:142\n1634#2,3:143\n1563#2:146\n1634#2,3:147\n14#3,8:150\n63#4,2:158\n71#4:160\n111#4:161\n72#4:162\n87#4:163\n113#4:164\n88#4:165\n63#4,2:166\n75#4:168\n110#4:169\n76#4:170\n63#4,2:171\n*S KotlinDebug\n*F\n+ 1 CastingImage.kt\nat/petrak/hexcasting/api/casting/eval/vm/CastingImage\n*L\n44#1:138\n44#1:139,3\n48#1:142\n48#1:143,3\n49#1:146\n49#1:147,3\n70#1:150,8\n71#1:158,2\n73#1:160\n73#1:161\n73#1:162\n74#1:163\n74#1:164\n74#1:165\n75#1:166,2\n76#1:168\n76#1:169\n76#1:170\n78#1:171,2\n*E\n"])
public data class CastingImage private constructor(stack: List<Iota>,
   parenCount: Int,
   parenthesized: List<at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota>,
   escapeNext: Boolean,
   opsConsumed: Long,
   userData: CompoundTag
) {
   public final val stack: List<Iota>
   public final val parenCount: Int
   public final val parenthesized: List<at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota>
   public final val escapeNext: Boolean
   public final val opsConsumed: Long
   public final val userData: CompoundTag

   init {
      this.stack = stack;
      this.parenCount = parenCount;
      this.parenthesized = parenthesized;
      this.escapeNext = escapeNext;
      this.opsConsumed = opsConsumed;
      this.userData = userData;
   }

   public constructor() : this(CollectionsKt.emptyList(), 0, CollectionsKt.emptyList(), false, 0L, new CompoundTag())
   private fun Iterable<at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota>.serializeToNBT(): CompoundTag {
      val tag: CompoundTag = new CompoundTag();
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$serializeToNBT`, 10));

      for (Object item$iv$iv : $this$serializeToNBT) {
         `destination$iv$iv`.add((`item$iv$iv` as CastingImage.ParenthesizedIota).getIota());
      }

      if (IotaType.isTooLargeToSerialize(`destination$iv$iv`)) {
         tag.put("iotas", (new ListTag()) as Tag);
         tag.put("escaped", (new ListTag()) as Tag);
      } else {
         `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$serializeToNBT`, 10));

         for (Object item$iv$iv : $this$serializeToNBT) {
            `destination$iv$iv`.add((var25 as CastingImage.ParenthesizedIota).getIota());
         }

         tag.put("iotas", new ListIota(`destination$iv$iv` as MutableList<Iota>).serialize());
         `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$serializeToNBT`, 10));

         for (Object item$iv$iv : $this$serializeToNBT) {
            `destination$iv$iv`.add((var26 as CastingImage.ParenthesizedIota).getEscaped());
         }

         tag.put("escaped", HexUtils.serializeToNBT(`destination$iv$iv`) as Tag);
      }

      return tag;
   }

   public fun withUsedOps(count: Long): CastingImage {
      return copy$default(this, null, 0, null, false, this.opsConsumed + count, null, 47, null);
   }

   public fun withUsedOp(): CastingImage {
      return this.withUsedOps(1L);
   }

   public fun withOverriddenUsedOps(count: Long): CastingImage {
      return copy$default(this, null, 0, null, false, count, null, 47, null);
   }

   public fun serializeToNbt(): CompoundTag {
      val `this_$iv`: NBTBuilder = NBTBuilder.INSTANCE;
      val var5: CompoundTag = NbtCompoundBuilder.constructor-impl(new CompoundTag());
      var5.put("stack", HexUtils.serializeToNBT(this.stack));
      val var10002: IntTag = IntTag.valueOf(this.parenCount);
      var5.put("open_parens", var10002 as Tag);
      val var32: ByteTag = ByteTag.valueOf((byte)(if (this.escapeNext) 1 else 0));
      var5.put("escape_next", var32 as Tag);
      var5.put("parenthesized", this.serializeToNBT(this.parenthesized) as Tag);
      val var33: LongTag = LongTag.valueOf(this.opsConsumed);
      var5.put("ops_consumed", var33 as Tag);
      var5.put("userdata", this.userData as Tag);
      return var5;
   }

   public operator fun component1(): List<Iota> {
      return this.stack;
   }

   public operator fun component2(): Int {
      return this.parenCount;
   }

   public operator fun component3(): List<at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota> {
      return this.parenthesized;
   }

   public operator fun component4(): Boolean {
      return this.escapeNext;
   }

   public operator fun component5(): Long {
      return this.opsConsumed;
   }

   public operator fun component6(): CompoundTag {
      return this.userData;
   }

   public fun copy(
      stack: List<Iota> = this.stack,
      parenCount: Int = this.parenCount,
      parenthesized: List<at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota> = this.parenthesized,
      escapeNext: Boolean = this.escapeNext,
      opsConsumed: Long = this.opsConsumed,
      userData: CompoundTag = this.userData
   ): CastingImage {
      return new CastingImage(stack, parenCount, parenthesized, escapeNext, opsConsumed, userData);
   }

   public override fun toString(): String {
      return "CastingImage(stack=${this.stack}, parenCount=${this.parenCount}, parenthesized=${this.parenthesized}, escapeNext=${this.escapeNext}, opsConsumed=${this.opsConsumed}, userData=${this.userData})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        ((this.stack.hashCode() * 31 + Integer.hashCode(this.parenCount)) * 31 + this.parenthesized.hashCode()) * 31
                           + java.lang.Boolean.hashCode(this.escapeNext)
                     )
                     * 31
                  + java.lang.Long.hashCode(this.opsConsumed)
            )
            * 31
         + this.userData.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CastingImage) {
         return false;
      } else {
         val var2: CastingImage = other as CastingImage;
         if (!(this.stack == (other as CastingImage).stack)) {
            return false;
         } else if (this.parenCount != var2.parenCount) {
            return false;
         } else if (!(this.parenthesized == var2.parenthesized)) {
            return false;
         } else if (this.escapeNext != var2.escapeNext) {
            return false;
         } else if (this.opsConsumed != var2.opsConsumed) {
            return false;
         } else {
            return this.userData == var2.userData;
         }
      }
   }

   public companion object {
      public const val TAG_STACK: String
      public const val TAG_PAREN_COUNT: String
      public const val TAG_PARENTHESIZED: String
      public const val TAG_ESCAPE_NEXT: String
      public const val TAG_OPS_CONSUMED: String
      public const val TAG_USERDATA: String

      public fun loadFromNbt(tag: CompoundTag, world: ServerLevel): CastingImage {
         var stack: CastingImage;
         try {
            val var15: java.util.List = new ArrayList();
            val var10000: java.util.Iterator = NBTHelper.getListByByte(tag, "stack", (byte)10).iterator();
            val userData: java.util.Iterator = var10000;

            while (userData.hasNext()) {
               val parenthesized: Tag = userData.next() as Tag;
               val parenTag: Iota = IotaType.deserialize(NBTHelper.getAsCompound(parenthesized), world);
               var15.add(parenTag);
            }

            val var16: CompoundTag = if (tag.contains("userdata")) tag.getCompound("userdata") else new CompoundTag();
            val var17: java.util.List = new ArrayList();
            val var18: CompoundTag = tag.getCompound("parenthesized");
            val parenIotasTag: ListTag = NBTHelper.getListByByte(var18, "iotas", (byte)10);
            val parenEscapedTag: ByteArray = var18.getByteArray("escaped");
            val var22: java.util.List = parenIotasTag as java.util.List;

            for (Pair escapeNext : HexUtils.zipWithDefault(var22, parenEscapedTag, CastingImage.Companion::loadFromNbt$lambda$0)) {
               val opsUsed: Tag = escapeNext.component1() as Tag;
               val isEscapedByte: Byte = (escapeNext.component2() as java.lang.Number).byteValue();
               val var10004: TagType = CompoundTag.TYPE;
               val var10003: Iota = IotaType.deserialize(HexUtils.downcast(opsUsed, var10004), world);
               var17.add(new CastingImage.ParenthesizedIota(var10003, isEscapedByte != 0));
            }

            val var19: Int = tag.getInt("open_parens");
            val var20: Boolean = tag.getBoolean("escape_next");
            val var21: Long = tag.getLong("ops_consumed");
            stack = new CastingImage(var15, var19, var17, var20, var21, var16, null);
         } catch (var14: Exception) {
            HexAPI.LOGGER.warn("error while loading a CastingImage", var14);
            stack = new CastingImage();
         }

         return stack;
      }

      public fun checkAndMarkGivenMotion(userData: CompoundTag, entity: Entity): Boolean {
         val var10001: java.lang.String = HexAPI.MARKED_MOVED_USERDATA;
         val marked: CompoundTag = NBTHelper.getOrCreateCompound(userData, var10001);
         val var10000: Boolean;
         if (marked.contains(entity.getStringUUID())) {
            var10000 = true;
         } else {
            marked.putBoolean(entity.getStringUUID(), true);
            var10000 = false;
         }

         return var10000;
      }

      @JvmStatic
      fun `loadFromNbt$lambda$0`(var0: Int): Byte {
         return 0;
      }
   }

   public data class ParenthesizedIota(iota: Iota, escaped: Boolean) {
      public final val iota: Iota
      public final val escaped: Boolean

      init {
         this.iota = iota;
         this.escaped = escaped;
      }

      public operator fun component1(): Iota {
         return this.iota;
      }

      public operator fun component2(): Boolean {
         return this.escaped;
      }

      public fun copy(iota: Iota = this.iota, escaped: Boolean = this.escaped): at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota {
         return new CastingImage.ParenthesizedIota(iota, escaped);
      }

      public override fun toString(): String {
         return "ParenthesizedIota(iota=${this.iota}, escaped=${this.escaped})";
      }

      public override fun hashCode(): Int {
         return this.iota.hashCode() * 31 + java.lang.Boolean.hashCode(this.escaped);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is CastingImage.ParenthesizedIota) {
            return false;
         } else {
            val var2: CastingImage.ParenthesizedIota = other as CastingImage.ParenthesizedIota;
            if (!(this.iota == (other as CastingImage.ParenthesizedIota).iota)) {
               return false;
            } else {
               return this.escaped == var2.escaped;
            }
         }
      }

      public companion object {
         public const val TAG_IOTAS: String
         public const val TAG_ESCAPED: String
      }
   }
}

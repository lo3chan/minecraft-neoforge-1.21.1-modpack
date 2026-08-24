package at.petrak.hexcasting.api.casting.math

import at.petrak.hexcasting.api.utils.HexUtils
import at.petrak.hexcasting.api.utils.NBTBuilder
import at.petrak.hexcasting.api.utils.NbtCompoundBuilder
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.phys.Vec2

@SourceDebugExtension(["SMAP\nHexPattern.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HexPattern.kt\nat/petrak/hexcasting/api/casting/math/HexPattern\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n+ 4 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n*L\n1#1,170:1\n1803#2,3:171\n1563#2:185\n1634#2,3:186\n1563#2:192\n1634#2,3:193\n14#3,8:174\n113#4:182\n63#4,2:183\n117#4:189\n63#4,2:190\n*S KotlinDebug\n*F\n+ 1 HexPattern.kt\nat/petrak/hexcasting/api/casting/math/HexPattern\n*L\n72#1:171,3\n77#1:185\n77#1:186,3\n114#1:192\n114#1:193,3\n75#1:174,8\n76#1:182\n76#1:183,2\n77#1:189\n77#1:190,2\n*E\n"])
public data class HexPattern(startDir: HexDir, angles: MutableList<HexAngle> = (new ArrayList()) as java.util.List) {
   public final val startDir: HexDir
   public final val angles: MutableList<HexAngle>

   init {
      this.startDir = startDir;
      this.angles = angles;
   }

   public fun tryAppendDir(newDir: HexDir): Boolean {
      val linesSeen: java.util.Set = new LinkedHashSet();
      var compass: HexDir = this.startDir;
      var cursor: HexCoord = HexCoord.Companion.getOrigin();

      for (HexAngle a : this.angles) {
         linesSeen.add(TuplesKt.to(cursor, compass));
         linesSeen.add(TuplesKt.to(cursor.plus(compass), compass.rotatedBy(HexAngle.BACK)));
         cursor = cursor.plus(compass);
         compass = compass.times(nextAngle);
      }

      if (linesSeen.contains(TuplesKt.to(cursor.plus(compass), newDir))) {
         return false;
      } else {
         val var9: HexAngle = newDir.minus(compass);
         if (var9 === HexAngle.BACK) {
            return false;
         } else {
            this.angles.add(var9);
            return true;
         }
      }
   }

   @JvmOverloads
   public fun positions(start: HexCoord = HexCoord.Companion.getOrigin()): List<HexCoord> {
      val out: ArrayList = new ArrayList(this.angles.size() + 2);
      out.add(start);
      var compass: HexDir = this.startDir;
      var cursor: HexCoord = start;

      for (HexAngle a : this.angles) {
         cursor = cursor.plus(compass);
         out.add(cursor);
         compass = compass.times(a);
      }

      out.add(cursor.plus(compass));
      return out;
   }

   public fun directions(): List<HexDir> {
      val out: ArrayList = new ArrayList(this.angles.size() + 1);
      out.add(this.startDir);
      var compass: HexDir = this.startDir;

      for (HexAngle a : this.angles) {
         compass = compass.times(a);
         out.add(compass);
      }

      return out;
   }

   public fun finalDir(): HexDir {
      val `$this$fold$iv`: java.lang.Iterable = this.angles;
      var `accumulator$iv`: Any = this.startDir;

      for (Object element$iv : $this$fold$iv) {
         `accumulator$iv` = ((HexDir)`accumulator$iv`).times(`element$iv` as HexAngle);
      }

      return (HexDir)`accumulator$iv`;
   }

   public fun serializeToNBT(): CompoundTag {
      val `this_$iv`: NBTBuilder = NBTBuilder.INSTANCE;
      val var5: CompoundTag = NbtCompoundBuilder.constructor-impl(new CompoundTag());
      val var10000: ByteTag = ByteTag.valueOf((byte)this.startDir.ordinal());
      var5.put("start_dir", var10000 as Tag);
      val var27: java.lang.Iterable = this.angles;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.angles, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`$this$mapTo$iv$iv$iv` as HexAngle).ordinal());
      }

      val `$this$map$iv$iv`: java.lang.Iterable = `destination$iv$iv` as java.util.List;
      val var35: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`destination$iv$iv` as java.util.List, 10));

      for (Object item$iv$iv$iv : $this$map$iv$iv) {
         var35.add((`item$iv$iv$iv` as java.lang.Number).byteValue());
      }

      var5.put("angles", (new ByteArrayTag(var35 as java.util.List)) as Tag);
      return var5;
   }

   public fun anglesSignature(): String {
      val var1: StringBuilder = new StringBuilder();
      val `$this$anglesSignature_u24lambda_u242`: StringBuilder = var1;

      for (HexAngle a : this.angles) {
         var var10001: java.lang.String;
         switch (HexPattern.WhenMappings.$EnumSwitchMapping$0[a.ordinal()]) {
            case 1:
               var10001 = "w";
               break;
            case 2:
               var10001 = "e";
               break;
            case 3:
               var10001 = "d";
               break;
            case 4:
               var10001 = "s";
               break;
            case 5:
               var10001 = "a";
               break;
            case 6:
               var10001 = "q";
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         `$this$anglesSignature_u24lambda_u242`.append(var10001);
      }

      return var1.toString();
   }

   @JvmOverloads
   public fun getCenter(hexRadius: Float, origin: HexCoord = HexCoord.Companion.getOrigin()): Vec2 {
      val var10002: Vec2 = Vec2.ZERO;
      return HexUtils.findCenter(this.toLines(hexRadius, HexUtils.coordToPx(origin, hexRadius, var10002)));
   }

   public fun toLines(hexSize: Float, origin: Vec2): List<Vec2> {
      val `$this$map$iv`: java.lang.Iterable = positions$default(this, null, 1, null);
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(HexUtils.coordToPx(`item$iv$iv` as HexCoord, hexSize, origin));
      }

      return `destination$iv$iv` as MutableList<Vec2>;
   }

   public fun sigsEqual(that: HexPattern): Boolean {
      return this.anglesSignature() == that.anglesSignature();
   }

   public override fun toString(): String {
      val var1: StringBuilder = new StringBuilder();
      var1.append("HexPattern[");
      var1.append(this.startDir);
      var1.append(", ");
      var1.append(this.anglesSignature());
      var1.append("]");
      return var1.toString();
   }

   public operator fun component1(): HexDir {
      return this.startDir;
   }

   public operator fun component2(): MutableList<HexAngle> {
      return this.angles;
   }

   public fun copy(startDir: HexDir = this.startDir, angles: MutableList<HexAngle> = this.angles): HexPattern {
      return new HexPattern(startDir, angles);
   }

   public override fun hashCode(): Int {
      return this.startDir.hashCode() * 31 + this.angles.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is HexPattern) {
         return false;
      } else {
         val var2: HexPattern = other as HexPattern;
         if (this.startDir != (other as HexPattern).startDir) {
            return false;
         } else {
            return this.angles == var2.angles;
         }
      }
   }

   @JvmOverloads
   fun positions(): MutableList<HexCoord> {
      return positions$default(this, null, 1, null);
   }

   @JvmOverloads
   fun getCenter(hexRadius: Float): Vec2 {
      return getCenter$default(this, hexRadius, null, 2, null);
   }

   @SourceDebugExtension(["SMAP\nHexPattern.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HexPattern.kt\nat/petrak/hexcasting/api/casting/math/HexPattern$Companion\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,170:1\n11238#2:171\n11573#2,3:172\n*S KotlinDebug\n*F\n+ 1 HexPattern.kt\nat/petrak/hexcasting/api/casting/math/HexPattern$Companion\n*L\n139#1:171\n139#1:172,3\n*E\n"])
   public companion object {
      public const val TAG_START_DIR: String
      public const val TAG_ANGLES: String

      public fun isPattern(tag: CompoundTag): Boolean {
         return tag.contains("start_dir", 99) && tag.contains("angles", 7);
      }

      public fun fromNBT(tag: CompoundTag): HexPattern {
         val startDir: HexDir = HexUtils.getSafe$default(HexDir.values(), tag.getByte("start_dir"), null, 2, null) as HexDir;
         val var10000: ByteArray = tag.getByteArray("angles");
         val var5: Array<HexAngle> = HexAngle.values();
         val `destination$iv$iv`: java.util.Collection = new ArrayList(var10000.length);

         val `$this$map$iv`: ByteArray;
         for (byte item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add(HexUtils.getSafe$default(var5, `item$iv$iv`, null, 2, null) as HexAngle);
         }

         return new HexPattern(startDir, CollectionsKt.toMutableList(`destination$iv$iv` as java.util.List));
      }

      public fun fromAngles(signature: String, startDir: HexDir): HexPattern {
         val out: HexPattern = new HexPattern(startDir, null, 2, null);
         var compass: HexDir = startDir;
         var var5: Int = 0;

         for (int var6 = signature.length(); var5 < var6; var5++) {
            val c: Char = signature.charAt(var5);
            var var10000: HexAngle;
            switch (c) {
               case 'a':
                  var10000 = HexAngle.LEFT_BACK;
                  break;
               case 'd':
                  var10000 = HexAngle.RIGHT_BACK;
                  break;
               case 'e':
                  var10000 = HexAngle.RIGHT;
                  break;
               case 'q':
                  var10000 = HexAngle.LEFT;
                  break;
               case 's':
                  var10000 = HexAngle.BACK;
                  break;
               case 'w':
                  var10000 = HexAngle.FORWARD;
                  break;
               default:
                  throw new IllegalArgumentException("Cannot match $c at idx $var5 to a direction");
            }

            compass = compass.times(var10000);
            if (!out.tryAppendDir(compass)) {
               throw new IllegalStateException("Adding the angle $c at index $var5 made the pattern invalid by looping back on itself");
            }
         }

         return out;
      }
   }
}

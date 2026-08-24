package at.petrak.hexcasting.api.casting.eval

import at.petrak.hexcasting.api.casting.math.HexCoord
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.NBTBuilder
import at.petrak.hexcasting.api.utils.NbtCompoundBuilder
import java.util.Locale
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

@SourceDebugExtension(["SMAP\nResolvedPattern.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResolvedPattern.kt\nat/petrak/hexcasting/api/casting/eval/ResolvedPattern\n+ 2 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n+ 3 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n*L\n1#1,28:1\n14#2,8:29\n63#3,2:37\n71#3:39\n111#3:40\n72#3:41\n71#3:42\n111#3:43\n72#3:44\n67#3:45\n115#3:46\n68#3:47\n*S KotlinDebug\n*F\n+ 1 ResolvedPattern.kt\nat/petrak/hexcasting/api/casting/eval/ResolvedPattern\n*L\n11#1:29,8\n12#1:37,2\n13#1:39\n13#1:40\n13#1:41\n14#1:42\n14#1:43\n14#1:44\n15#1:45\n15#1:46\n15#1:47\n*E\n"])
public data class ResolvedPattern(pattern: HexPattern, origin: HexCoord, type: ResolvedPatternType) {
   public final val pattern: HexPattern
   public final val origin: HexCoord

   public final var type: ResolvedPatternType
      internal set

   init {
      this.pattern = pattern;
      this.origin = origin;
      this.type = type;
   }

   public fun serializeToNBT(): CompoundTag {
      val `this_$iv`: NBTBuilder = NBTBuilder.INSTANCE;
      val var5: CompoundTag = NbtCompoundBuilder.constructor-impl(new CompoundTag());
      var5.put("Pattern", this.pattern.serializeToNBT() as Tag);
      var var10002: IntTag = IntTag.valueOf(this.origin.getQ());
      var5.put("OriginQ", var10002 as Tag);
      var10002 = IntTag.valueOf(this.origin.getR());
      var5.put("OriginR", var10002 as Tag);
      val var18: java.lang.String = this.type.name();
      val var10000: Locale = Locale.ROOT;
      val var24: java.lang.String = var18.toLowerCase(var10000);
      val var26: StringTag = StringTag.valueOf(var24);
      var5.put("Valid", var26 as Tag);
      return var5;
   }

   public operator fun component1(): HexPattern {
      return this.pattern;
   }

   public operator fun component2(): HexCoord {
      return this.origin;
   }

   public operator fun component3(): ResolvedPatternType {
      return this.type;
   }

   public fun copy(pattern: HexPattern = this.pattern, origin: HexCoord = this.origin, type: ResolvedPatternType = this.type): ResolvedPattern {
      return new ResolvedPattern(pattern, origin, type);
   }

   public override fun toString(): String {
      return "ResolvedPattern(pattern=${this.pattern}, origin=${this.origin}, type=${this.type})";
   }

   public override fun hashCode(): Int {
      return (this.pattern.hashCode() * 31 + this.origin.hashCode()) * 31 + this.type.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ResolvedPattern) {
         return false;
      } else {
         val var2: ResolvedPattern = other as ResolvedPattern;
         if (!(this.pattern == (other as ResolvedPattern).pattern)) {
            return false;
         } else if (!(this.origin == var2.origin)) {
            return false;
         } else {
            return this.type === var2.type;
         }
      }
   }

   public companion object {
      public fun fromNBT(tag: CompoundTag): ResolvedPattern {
         val var10000: HexPattern.Companion = HexPattern.Companion;
         val var10001: CompoundTag = tag.getCompound("Pattern");
         val pattern: HexPattern = var10000.fromNBT(var10001);
         val origin: HexCoord = new HexCoord(tag.getInt("OriginQ"), tag.getInt("OriginR"));
         val var5: ResolvedPatternType.Companion = ResolvedPatternType.Companion;
         val var6: java.lang.String = tag.getString("Valid");
         return new ResolvedPattern(pattern, origin, var5.fromString(var6));
      }
   }
}

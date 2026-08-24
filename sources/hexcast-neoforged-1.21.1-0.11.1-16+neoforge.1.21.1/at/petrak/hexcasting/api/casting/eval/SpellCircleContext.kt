package at.petrak.hexcasting.api.casting.eval

import at.petrak.hexcasting.api.utils.NBTBuilder
import at.petrak.hexcasting.api.utils.NbtCompoundBuilder
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.BlockPos
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.Tag
import net.minecraft.world.phys.AABB

@SourceDebugExtension(["SMAP\nSpellCircleContext.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpellCircleContext.kt\nat/petrak/hexcasting/api/casting/eval/SpellCircleContext\n+ 2 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n+ 3 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n*L\n1#1,57:1\n14#2,8:58\n71#3:66\n111#3:67\n72#3:68\n71#3:69\n111#3:70\n72#3:71\n71#3:72\n111#3:73\n72#3:74\n79#3:75\n108#3:76\n80#3:77\n79#3:78\n108#3:79\n80#3:80\n79#3:81\n108#3:82\n80#3:83\n79#3:84\n108#3:85\n80#3:86\n79#3:87\n108#3:88\n80#3:89\n79#3:90\n108#3:91\n80#3:92\n87#3:93\n113#3:94\n88#3:95\n*S KotlinDebug\n*F\n+ 1 SpellCircleContext.kt\nat/petrak/hexcasting/api/casting/eval/SpellCircleContext\n*L\n12#1:58,8\n13#1:66\n13#1:67\n13#1:68\n14#1:69\n14#1:70\n14#1:71\n15#1:72\n15#1:73\n15#1:74\n17#1:75\n17#1:76\n17#1:77\n18#1:78\n18#1:79\n18#1:80\n19#1:81\n19#1:82\n19#1:83\n20#1:84\n20#1:85\n20#1:86\n21#1:87\n21#1:88\n21#1:89\n22#1:90\n22#1:91\n22#1:92\n24#1:93\n24#1:94\n24#1:95\n*E\n"])
public data class SpellCircleContext(impetusPos: BlockPos, aabb: AABB, activatorAlwaysInRange: Boolean) {
   public final val impetusPos: BlockPos
   public final val aabb: AABB
   public final val activatorAlwaysInRange: Boolean

   init {
      this.impetusPos = impetusPos;
      this.aabb = aabb;
      this.activatorAlwaysInRange = activatorAlwaysInRange;
   }

   public fun serializeToNBT(): CompoundTag {
      val `this_$iv`: NBTBuilder = NBTBuilder.INSTANCE;
      val var5: CompoundTag = NbtCompoundBuilder.constructor-impl(new CompoundTag());
      var var10002: IntTag = IntTag.valueOf(this.impetusPos.getX());
      var5.put("impetus_x", var10002 as Tag);
      var10002 = IntTag.valueOf(this.impetusPos.getY());
      var5.put("impetus_y", var10002 as Tag);
      var10002 = IntTag.valueOf(this.impetusPos.getZ());
      var5.put("impetus_z", var10002 as Tag);
      val var53: DoubleTag = DoubleTag.valueOf(this.aabb.minX);
      var5.put("min_x", var53 as Tag);
      val var54: DoubleTag = DoubleTag.valueOf(this.aabb.minY);
      var5.put("min_y", var54 as Tag);
      val var55: DoubleTag = DoubleTag.valueOf(this.aabb.minZ);
      var5.put("min_z", var55 as Tag);
      val var56: DoubleTag = DoubleTag.valueOf(this.aabb.maxX);
      var5.put("max_x", var56 as Tag);
      val var57: DoubleTag = DoubleTag.valueOf(this.aabb.maxY);
      var5.put("max_y", var57 as Tag);
      val var58: DoubleTag = DoubleTag.valueOf(this.aabb.maxZ);
      var5.put("max_z", var58 as Tag);
      val var59: ByteTag = ByteTag.valueOf((byte)(if (this.activatorAlwaysInRange) 1 else 0));
      var5.put("player_always_in_range", var59 as Tag);
      return var5;
   }

   public operator fun component1(): BlockPos {
      return this.impetusPos;
   }

   public operator fun component2(): AABB {
      return this.aabb;
   }

   public operator fun component3(): Boolean {
      return this.activatorAlwaysInRange;
   }

   public fun copy(impetusPos: BlockPos = this.impetusPos, aabb: AABB = this.aabb, activatorAlwaysInRange: Boolean = this.activatorAlwaysInRange): SpellCircleContext {
      return new SpellCircleContext(impetusPos, aabb, activatorAlwaysInRange);
   }

   public override fun toString(): String {
      return "SpellCircleContext(impetusPos=${this.impetusPos}, aabb=${this.aabb}, activatorAlwaysInRange=${this.activatorAlwaysInRange})";
   }

   public override fun hashCode(): Int {
      return (this.impetusPos.hashCode() * 31 + this.aabb.hashCode()) * 31 + java.lang.Boolean.hashCode(this.activatorAlwaysInRange);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is SpellCircleContext) {
         return false;
      } else {
         val var2: SpellCircleContext = other as SpellCircleContext;
         if (!(this.impetusPos == (other as SpellCircleContext).impetusPos)) {
            return false;
         } else if (!(this.aabb == var2.aabb)) {
            return false;
         } else {
            return this.activatorAlwaysInRange == var2.activatorAlwaysInRange;
         }
      }
   }

   public companion object {
      public const val TAG_IMPETUS_X: String
      public const val TAG_IMPETUS_Y: String
      public const val TAG_IMPETUS_Z: String
      public const val TAG_MIN_X: String
      public const val TAG_MIN_Y: String
      public const val TAG_MIN_Z: String
      public const val TAG_MAX_X: String
      public const val TAG_MAX_Y: String
      public const val TAG_MAX_Z: String
      public const val TAG_PLAYER_ALWAYS_IN_RANGE: String

      public fun fromNBT(tag: CompoundTag): SpellCircleContext {
         return new SpellCircleContext(
            new BlockPos(tag.getInt("impetus_x"), tag.getInt("impetus_y"), tag.getInt("impetus_z")),
            new AABB(
               tag.getDouble("min_x"), tag.getDouble("min_y"), tag.getDouble("min_z"), tag.getDouble("max_x"), tag.getDouble("max_y"), tag.getDouble("max_z")
            ),
            tag.getBoolean("player_always_in_range")
         );
      }
   }
}

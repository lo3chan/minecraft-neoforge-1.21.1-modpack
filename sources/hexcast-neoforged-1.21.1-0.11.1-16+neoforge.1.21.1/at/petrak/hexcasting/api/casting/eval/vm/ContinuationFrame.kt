package at.petrak.hexcasting.api.casting.eval.vm

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.lib.hex.HexContinuationTypes
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel

public interface ContinuationFrame {
   public val type: at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame.Type<*>

   public abstract fun evaluate(continuation: SpellContinuation, level: ServerLevel, harness: CastingVM): CastResult {
   }

   public abstract fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
   }

   public abstract fun serializeToNBT(): CompoundTag {
   }

   public abstract fun size(): Int {
   }

   @SourceDebugExtension(["SMAP\nContinuationFrame.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ContinuationFrame.kt\nat/petrak/hexcasting/api/casting/eval/vm/ContinuationFrame$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,105:1\n1#2:106\n*E\n"])
   public companion object {
      public fun fromNBT(tag: CompoundTag, world: ServerLevel): ContinuationFrame {
         val var10000: ContinuationFrame.Type = this.getTypeFromTag(tag);
         if (var10000 == null) {
            return new FrameEvaluate(new SpellList.LList(0, CollectionsKt.emptyList()), false);
         } else {
            val var4: Tag = tag.get("hexcasting:data");
            val var7: CompoundTag = var4 as? CompoundTag;
            if ((var4 as? CompoundTag) != null) {
               val var8: ContinuationFrame = var10000.deserializeFromNBT(var7, world);
               if (var8 != null) {
                  return var8;
               }
            }

            return new FrameEvaluate(new SpellList.LList(0, CollectionsKt.emptyList()), false);
         }
      }

      public fun toNBT(frame: ContinuationFrame): CompoundTag {
         val type: ContinuationFrame.Type = frame.getType();
         val var10000: ResourceLocation = HexContinuationTypes.REGISTRY.getKey(type);
         if (var10000 == null) {
            throw new IllegalStateException("Tried to serialize an unregistered continuation type. Continuation: $frame ; Type${type.getClass().getTypeName()}");
         } else {
            val data: CompoundTag = frame.serializeToNBT();
            val out: CompoundTag = new CompoundTag();
            out.putString("hexcasting:type", var10000.toString());
            out.put("hexcasting:data", data as Tag);
            return out;
         }
      }

      private fun getTypeFromTag(tag: CompoundTag): at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame.Type<*>? {
         label11:
         if (!tag.contains("hexcasting:type", 8)) {
            return null;
         } else {
            val var10000: ResourceLocation = ResourceLocation.tryParse(tag.getString("hexcasting:type"));
            return if (var10000 == null) null else HexContinuationTypes.REGISTRY.get(var10000) as ContinuationFrame.Type;
         }
      }
   }

   public interface Type<U extends ContinuationFrame> {
      public abstract fun deserializeFromNBT(tag: CompoundTag, world: ServerLevel): Any? {
      }
   }
}

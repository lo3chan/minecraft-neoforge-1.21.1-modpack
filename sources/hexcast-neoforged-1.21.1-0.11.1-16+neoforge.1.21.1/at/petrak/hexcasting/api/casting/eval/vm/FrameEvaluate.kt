package at.petrak.hexcasting.api.casting.eval.vm

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame.Type
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.HexUtils
import at.petrak.hexcasting.api.utils.NBTBuilder
import at.petrak.hexcasting.api.utils.NbtCompoundBuilder
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel

@SourceDebugExtension(["SMAP\nFrameEvaluate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FrameEvaluate.kt\nat/petrak/hexcasting/api/casting/eval/vm/FrameEvaluate\n+ 2 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n+ 3 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n*L\n1#1,74:1\n14#2,8:75\n63#3,2:83\n87#3:85\n113#3:86\n88#3:87\n*S KotlinDebug\n*F\n+ 1 FrameEvaluate.kt\nat/petrak/hexcasting/api/casting/eval/vm/FrameEvaluate\n*L\n51#1:75,8\n52#1:83,2\n53#1:85\n53#1:86\n53#1:87\n*E\n"])
public data class FrameEvaluate(list: SpellList, isMetacasting: Boolean) : ContinuationFrame {
   public final val list: SpellList
   public final val isMetacasting: Boolean
   public open val type: Type<*>

   init {
      this.list = list;
      this.isMetacasting = isMetacasting;
      this.type = TYPE;
   }

   public override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
      return TuplesKt.to(false, stack);
   }

   public override fun evaluate(continuation: SpellContinuation, level: ServerLevel, harness: CastingVM): CastResult {
      val var10000: CastResult;
      if (this.list.getNonEmpty()) {
         val update: CastResult = harness.executeInner(
            this.list.getCar(),
            level,
            if (this.list.getCdr().getNonEmpty()) continuation.pushFrame(new FrameEvaluate(this.list.getCdr(), this.isMetacasting)) else continuation
         );
         if (this.isMetacasting && !(update.getSound() == HexEvalSounds.MISHAP)) {
            val var10006: EvalSound = HexEvalSounds.HERMES;
            var10000 = CastResult.copy$default(update, null, null, null, null, null, var10006, 31, null);
         } else {
            var10000 = update;
         }
      } else {
         val var10002: Iota = new ListIota(this.list);
         val var10005: java.util.List = CollectionsKt.emptyList();
         val var6: ResolvedPatternType = ResolvedPatternType.EVALUATED;
         val var10007: EvalSound = HexEvalSounds.HERMES;
         var10000 = new CastResult(var10002, continuation, null, var10005, var6, var10007);
      }

      return var10000;
   }

   public override fun serializeToNBT(): CompoundTag {
      val `this_$iv`: NBTBuilder = NBTBuilder.INSTANCE;
      val var5: CompoundTag = NbtCompoundBuilder.constructor-impl(new CompoundTag());
      var5.put("patterns", HexUtils.serializeToNBT(this.list));
      val var10002: ByteTag = ByteTag.valueOf((byte)(if (this.isMetacasting) 1 else 0));
      var5.put("isMetacasting", var10002 as Tag);
      return var5;
   }

   public override fun size(): Int {
      return this.list.size();
   }

   public operator fun component1(): SpellList {
      return this.list;
   }

   public operator fun component2(): Boolean {
      return this.isMetacasting;
   }

   public fun copy(list: SpellList = this.list, isMetacasting: Boolean = this.isMetacasting): FrameEvaluate {
      return new FrameEvaluate(list, isMetacasting);
   }

   public override fun toString(): String {
      return "FrameEvaluate(list=${this.list}, isMetacasting=${this.isMetacasting})";
   }

   public override fun hashCode(): Int {
      return this.list.hashCode() * 31 + java.lang.Boolean.hashCode(this.isMetacasting);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is FrameEvaluate) {
         return false;
      } else {
         val var2: FrameEvaluate = other as FrameEvaluate;
         if (!(this.list == (other as FrameEvaluate).list)) {
            return false;
         } else {
            return this.isMetacasting == var2.isMetacasting;
         }
      }
   }

   public companion object {
      public final val TYPE: Type<FrameEvaluate>
   }
}

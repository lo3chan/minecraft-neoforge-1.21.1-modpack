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
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel

@SourceDebugExtension(["SMAP\nFrameForEach.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FrameForEach.kt\nat/petrak/hexcasting/api/casting/eval/vm/FrameForEach\n+ 2 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n+ 3 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n*L\n1#1,116:1\n14#2,8:117\n63#3,2:125\n63#3,2:127\n63#3,2:129\n63#3,2:131\n*S KotlinDebug\n*F\n+ 1 FrameForEach.kt\nat/petrak/hexcasting/api/casting/eval/vm/FrameForEach\n*L\n83#1:117,8\n84#1:125,2\n85#1:127,2\n87#1:129,2\n88#1:131,2\n*E\n"])
public data class FrameForEach(data: SpellList, code: SpellList, baseStack: List<Iota>?, acc: MutableList<Iota>) : ContinuationFrame {
   public final val data: SpellList
   public final val code: SpellList
   public final val baseStack: List<Iota>?
   public final val acc: MutableList<Iota>
   public open val type: Type<*>

   init {
      this.data = data;
      this.code = code;
      this.baseStack = baseStack;
      this.acc = acc;
      this.type = TYPE;
   }

   public override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
      var var10000: java.util.List;
      label11: {
         if (this.baseStack != null) {
            var10000 = CollectionsKt.toMutableList(this.baseStack);
            if (var10000 != null) {
               break label11;
            }
         }

         var10000 = new ArrayList();
      }

      this.acc.addAll(stack);
      var10000.add(new ListIota(this.acc));
      return TuplesKt.to(true, var10000);
   }

   public override fun evaluate(continuation: SpellContinuation, level: ServerLevel, harness: CastingVM): CastResult {
      val var10000: java.util.List;
      if (this.baseStack == null) {
         var10000 = CollectionsKt.toList(harness.getImage().getStack());
      } else {
         this.acc.addAll(harness.getImage().getStack());
         var10000 = this.baseStack;
      }

      val var11: Triple = if (this.data.getNonEmpty())
         new Triple(
            this.data.getCar(),
            harness.getImage().withUsedOp(),
            continuation.pushFrame(new FrameForEach(this.data.getCdr(), this.code, var10000, this.acc)).pushFrame(new FrameEvaluate(this.code, true))
         )
         else
         new Triple(new ListIota(this.acc), harness.getImage(), continuation);
      val var10: Iota = var11.component1() as Iota;
      val newImage: CastingImage = var11.component2() as CastingImage;
      val newCont: SpellContinuation = var11.component3() as SpellContinuation;
      val tStack: java.util.List = CollectionsKt.toMutableList(var10000);
      tStack.add(var10);
      val var10002: Iota = new ListIota(this.code);
      val var10004: CastingImage = CastingImage.copy$default(newImage, tStack, 0, null, false, 0L, null, 62, null);
      val var10005: java.util.List = CollectionsKt.emptyList();
      val var10006: ResolvedPatternType = ResolvedPatternType.EVALUATED;
      val var10007: EvalSound = HexEvalSounds.THOTH;
      return new CastResult(var10002, newCont, var10004, var10005, var10006, var10007);
   }

   public override fun serializeToNBT(): CompoundTag {
      val `this_$iv`: NBTBuilder = NBTBuilder.INSTANCE;
      val var5: CompoundTag = NbtCompoundBuilder.constructor-impl(new CompoundTag());
      var5.put("data", HexUtils.serializeToNBT(this.data));
      var5.put("code", HexUtils.serializeToNBT(this.code));
      if (this.baseStack != null) {
         var5.put("base", HexUtils.serializeToNBT(this.baseStack));
      }

      var5.put("accumulator", HexUtils.serializeToNBT(this.acc));
      return var5;
   }

   public override fun size(): Int {
      return this.data.size() + this.code.size() + this.acc.size() + (if (this.baseStack != null) this.baseStack.size() else 0);
   }

   public operator fun component1(): SpellList {
      return this.data;
   }

   public operator fun component2(): SpellList {
      return this.code;
   }

   public operator fun component3(): List<Iota>? {
      return this.baseStack;
   }

   public operator fun component4(): MutableList<Iota> {
      return this.acc;
   }

   public fun copy(data: SpellList = this.data, code: SpellList = this.code, baseStack: List<Iota>? = this.baseStack, acc: MutableList<Iota> = this.acc): FrameForEach {
      return new FrameForEach(data, code, baseStack, acc);
   }

   public override fun toString(): String {
      return "FrameForEach(data=${this.data}, code=${this.code}, baseStack=${this.baseStack}, acc=${this.acc})";
   }

   public override fun hashCode(): Int {
      return ((this.data.hashCode() * 31 + this.code.hashCode()) * 31 + (if (this.baseStack == null) 0 else this.baseStack.hashCode())) * 31
         + this.acc.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is FrameForEach) {
         return false;
      } else {
         val var2: FrameForEach = other as FrameForEach;
         if (!(this.data == (other as FrameForEach).data)) {
            return false;
         } else if (!(this.code == var2.code)) {
            return false;
         } else if (!(this.baseStack == var2.baseStack)) {
            return false;
         } else {
            return this.acc == var2.acc;
         }
      }
   }

   public companion object {
      public final val TYPE: Type<FrameForEach>
   }
}

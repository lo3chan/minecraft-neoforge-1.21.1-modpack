package at.petrak.hexcasting.api.casting.eval.vm

import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame.Type
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel

public object FrameFinishEval : ContinuationFrame {
   public final val TYPE: Type<FrameFinishEval> = (new ContinuationFrame.Type<FrameFinishEval>() {
      public FrameFinishEval deserializeFromNBT(CompoundTag tag, ServerLevel world) {
         return FrameFinishEval.INSTANCE;
      }
   }) as ContinuationFrame.Type

   public open val type: Type<FrameFinishEval> = TYPE

   public override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
      return TuplesKt.to(true, stack);
   }

   public override fun evaluate(continuation: SpellContinuation, level: ServerLevel, harness: CastingVM): CastResult {
      val var10002: Iota = new NullIota();
      val var10005: java.util.List = CollectionsKt.emptyList();
      val var10006: ResolvedPatternType = ResolvedPatternType.EVALUATED;
      val var10007: EvalSound = HexEvalSounds.NOTHING;
      return new CastResult(var10002, continuation, null, var10005, var10006, var10007);
   }

   public override fun serializeToNBT(): CompoundTag {
      return new CompoundTag();
   }

   public override fun size(): Int {
      return 0;
   }
}

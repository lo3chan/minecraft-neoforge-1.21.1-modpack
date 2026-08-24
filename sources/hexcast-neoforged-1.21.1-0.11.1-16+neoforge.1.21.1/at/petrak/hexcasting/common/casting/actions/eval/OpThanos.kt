package at.petrak.hexcasting.common.casting.actions.eval

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds

public object OpThanos : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val opsLeft: Long = HexConfig.server().maxOpCount() - image.getOpsConsumed();
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      stack.add(new DoubleIota((double)opsLeft));
      val image2: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
      val var10003: java.util.List = CollectionsKt.emptyList();
      val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
      return new OperationResult(image2, var10003, continuation, var10005);
   }
}

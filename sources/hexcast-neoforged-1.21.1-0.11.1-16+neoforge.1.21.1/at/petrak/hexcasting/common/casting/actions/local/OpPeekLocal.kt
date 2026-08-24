package at.petrak.hexcasting.common.casting.actions.local

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds

public object OpPeekLocal : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      val rm: Iota = if (image.getUserData().contains(HexAPI.RAVENMIND_USERDATA))
         IotaType.deserialize(image.getUserData().getCompound(HexAPI.RAVENMIND_USERDATA), env.getWorld())
         else
         new NullIota();
      stack.add(rm);
      val image2: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
      val var10003: java.util.List = CollectionsKt.emptyList();
      val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
      return new OperationResult(image2, var10003, continuation, var10005);
   }
}

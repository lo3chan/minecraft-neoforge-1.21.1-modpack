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
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.nbt.Tag

public object OpPushLocal : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      if (stack.isEmpty()) {
         throw new MishapNotEnoughArgs(1, 0);
      } else {
         val newLocal: Iota = stack.removeLast() as Iota;
         if (newLocal.getType() == HexIotaTypes.NULL) {
            image.getUserData().remove(HexAPI.RAVENMIND_USERDATA);
         } else {
            image.getUserData().put(HexAPI.RAVENMIND_USERDATA, IotaType.serialize(newLocal) as Tag);
         }

         val image2: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
         val var10003: java.util.List = CollectionsKt.emptyList();
         val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
         return new OperationResult(image2, var10003, continuation, var10005);
      }
   }
}

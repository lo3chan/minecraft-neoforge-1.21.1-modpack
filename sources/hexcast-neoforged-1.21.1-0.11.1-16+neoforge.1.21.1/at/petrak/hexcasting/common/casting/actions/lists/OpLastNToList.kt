package at.petrak.hexcasting.common.casting.actions.lists

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nOpLastNToList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpLastNToList.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpLastNToList\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,33:1\n304#2:34\n*S KotlinDebug\n*F\n+ 1 OpLastNToList.kt\nat/petrak/hexcasting/common/casting/actions/lists/OpLastNToList\n*L\n27#1:34\n*E\n"])
public object OpLastNToList : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      if (stack.isEmpty()) {
         throw new MishapNotEnoughArgs(1, 0);
      } else {
         val yoinkCount: Int = OperatorUtils.getPositiveIntUnderInclusive$default(CollectionsKt.takeLast(stack, 1), 0, stack.size() - 1, 0, 4, null);
         stack.removeLast();
         val output: java.util.List = new ArrayList();
         output.addAll(CollectionsKt.takeLast(stack, yoinkCount));

         for (int i = 0; i < yoinkCount; i++) {
            stack.removeLast();
         }

         stack.addAll(CollectionsKt.listOf(new ListIota(output)));
         val var9: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
         val var10003: java.util.List = CollectionsKt.emptyList();
         val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
         return new OperationResult(var9, var10003, continuation, var10005);
      }
   }
}

package at.petrak.hexcasting.common.casting.actions.math

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota

public object OpFloor : ConstMediaAction {
   public open val argc: Int
      public open get() {
         return 1;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      return CollectionsKt.listOf(OperatorUtils.aplKinnie(OperatorUtils.getNumOrVec(args, 0, this.getArgc()), OpFloor::execute$floor__proxy));
   }

   override fun getMediaCost(): Long {
      return ConstMediaAction.DefaultImpls.getMediaCost(this);
   }

   override fun executeWithOpCount(args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
      return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
   }

   @JvmStatic
   fun `execute$floor__proxy`(`p0$x`: Double): Double {
      return Math.floor(`p0$x`);
   }
}

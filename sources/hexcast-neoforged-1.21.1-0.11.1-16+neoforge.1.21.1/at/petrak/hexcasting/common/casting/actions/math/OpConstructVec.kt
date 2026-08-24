package at.petrak.hexcasting.common.casting.actions.math

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpConstructVec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpConstructVec.kt\nat/petrak/hexcasting/common/casting/actions/math/OpConstructVec\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,19:1\n308#2:20\n*S KotlinDebug\n*F\n+ 1 OpConstructVec.kt\nat/petrak/hexcasting/common/casting/actions/math/OpConstructVec\n*L\n16#1:20\n*E\n"])
public object OpConstructVec : ConstMediaAction {
   public open val argc: Int = 3

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      return CollectionsKt.listOf(
         new Vec3Iota(
            new Vec3(
               OperatorUtils.getDouble(args, 0, this.getArgc()),
               OperatorUtils.getDouble(args, 1, this.getArgc()),
               OperatorUtils.getDouble(args, 2, this.getArgc())
            )
         )
      );
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
}

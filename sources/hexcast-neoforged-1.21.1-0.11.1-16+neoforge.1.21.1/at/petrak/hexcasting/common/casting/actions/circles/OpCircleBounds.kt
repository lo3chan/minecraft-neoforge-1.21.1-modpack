package at.petrak.hexcasting.common.casting.actions.circles

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus
import at.petrak.hexcasting.api.casting.circles.CircleExecutionState
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpCircleBounds.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpCircleBounds.kt\nat/petrak/hexcasting/common/casting/actions/circles/OpCircleBounds\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,27:1\n308#2:28\n308#2:29\n*S KotlinDebug\n*F\n+ 1 OpCircleBounds.kt\nat/petrak/hexcasting/common/casting/actions/circles/OpCircleBounds\n*L\n22#1:28\n24#1:29\n*E\n"])
public class OpCircleBounds(max: Boolean) : ConstMediaAction {
   public final val max: Boolean
   public open val argc: Int

   init {
      this.max = max;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      if (env !is CircleCastEnv) {
         throw new MishapNoSpellCircle();
      } else {
         val var10000: BlockEntityAbstractImpetus = (env as CircleCastEnv).getImpetus();
         if (var10000 == null) {
            throw new MishapNoSpellCircle();
         } else {
            val var9: CircleExecutionState = var10000.getExecutionState();
            return if (this.max)
               CollectionsKt.listOf(new Vec3Iota(new Vec3(var9.bounds.maxX - 0.5, var9.bounds.maxY - 0.5, var9.bounds.maxZ - 0.5)))
               else
               CollectionsKt.listOf(new Vec3Iota(new Vec3(var9.bounds.minX + 0.5, var9.bounds.minY + 0.5, var9.bounds.minZ + 0.5)));
         }
      }
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

package at.petrak.hexcasting.common.casting.actions.circles

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpImpetusPos.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpImpetusPos.kt\nat/petrak/hexcasting/common/casting/actions/circles/OpImpetusPos\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,20:1\n306#2:21\n*S KotlinDebug\n*F\n+ 1 OpImpetusPos.kt\nat/petrak/hexcasting/common/casting/actions/circles/OpImpetusPos\n*L\n17#1:21\n*E\n"])
public object OpImpetusPos : ConstMediaAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
      if (ctx !is CircleCastEnv) {
         throw new MishapNoSpellCircle();
      } else {
         val var10000: BlockPos = (ctx as CircleCastEnv).circleState().impetusPos;
         return CollectionsKt.listOf(new Vec3Iota(Vec3.atCenterOf(var10000 as Vec3i)));
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

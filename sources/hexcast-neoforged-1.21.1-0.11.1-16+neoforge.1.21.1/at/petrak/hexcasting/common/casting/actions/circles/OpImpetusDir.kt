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
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f

@SourceDebugExtension(["SMAP\nOpImpetusDir.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpImpetusDir.kt\nat/petrak/hexcasting/common/casting/actions/circles/OpImpetusDir\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,24:1\n307#2:25\n*S KotlinDebug\n*F\n+ 1 OpImpetusDir.kt\nat/petrak/hexcasting/common/casting/actions/circles/OpImpetusDir\n*L\n21#1:25\n*E\n"])
public object OpImpetusDir : ConstMediaAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
      if (ctx !is CircleCastEnv) {
         throw new MishapNoSpellCircle();
      } else {
         val var10000: Vector3f = (ctx as CircleCastEnv).circleState().impetusDir.step();
         return CollectionsKt.listOf(new Vec3Iota(new Vec3(var10000)));
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

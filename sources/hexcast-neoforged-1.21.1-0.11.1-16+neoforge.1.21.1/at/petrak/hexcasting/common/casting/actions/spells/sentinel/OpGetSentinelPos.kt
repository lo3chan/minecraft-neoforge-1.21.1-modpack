package at.petrak.hexcasting.common.casting.actions.spells.sentinel

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapLocationInWrongDimension
import at.petrak.hexcasting.api.player.Sentinel
import at.petrak.hexcasting.xplat.IXplatAbstractions
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpGetSentinelPos.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpGetSentinelPos.kt\nat/petrak/hexcasting/common/casting/actions/spells/sentinel/OpGetSentinelPos\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,22:1\n308#2:23\n*S KotlinDebug\n*F\n+ 1 OpGetSentinelPos.kt\nat/petrak/hexcasting/common/casting/actions/spells/sentinel/OpGetSentinelPos\n*L\n19#1:23\n*E\n"])
public object OpGetSentinelPos : ConstMediaAction {
   public open val argc: Int
   public open val mediaCost: Long = 1000L

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val var10000: Sentinel = IXplatAbstractions.INSTANCE.getSentinel(env.getCaster() as Player);
      if (var10000 == null) {
         return CollectionsKt.listOf(new NullIota());
      } else if (!(var10000.dimension() == env.getWorld().dimension())) {
         val var10002: ResourceLocation = var10000.dimension().location();
         throw new MishapLocationInWrongDimension(var10002);
      } else {
         val var6: Vec3 = var10000.position();
         return CollectionsKt.listOf(new Vec3Iota(var6));
      }
   }

   override fun executeWithOpCount(args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
      return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
   }
}

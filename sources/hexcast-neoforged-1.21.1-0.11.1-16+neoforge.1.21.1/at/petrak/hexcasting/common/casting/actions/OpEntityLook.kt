package at.petrak.hexcasting.common.casting.actions

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpEntityLook.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpEntityLook.kt\nat/petrak/hexcasting/common/casting/actions/OpEntityLook\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,18:1\n308#2:19\n*S KotlinDebug\n*F\n+ 1 OpEntityLook.kt\nat/petrak/hexcasting/common/casting/actions/OpEntityLook\n*L\n15#1:19\n*E\n"])
public object OpEntityLook : ConstMediaAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val e: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      env.assertEntityInRange(e);
      val var10000: Vec3 = e.getLookAngle();
      return CollectionsKt.listOf(new Vec3Iota(var10000));
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

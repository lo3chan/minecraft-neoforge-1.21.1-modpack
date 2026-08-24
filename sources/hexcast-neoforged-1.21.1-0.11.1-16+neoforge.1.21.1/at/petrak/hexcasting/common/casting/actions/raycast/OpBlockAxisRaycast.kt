package at.petrak.hexcasting.common.casting.actions.raycast

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.Vec3i
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.ClipContext.Block
import net.minecraft.world.level.ClipContext.Fluid
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.HitResult.Type
import org.joml.Vector3f

@SourceDebugExtension(["SMAP\nOpBlockAxisRaycast.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpBlockAxisRaycast.kt\nat/petrak/hexcasting/common/casting/actions/raycast/OpBlockAxisRaycast\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,41:1\n307#2:42\n*S KotlinDebug\n*F\n+ 1 OpBlockAxisRaycast.kt\nat/petrak/hexcasting/common/casting/actions/raycast/OpBlockAxisRaycast\n*L\n35#1:42\n*E\n"])
public object OpBlockAxisRaycast : ConstMediaAction {
   public open val argc: Int = 2
   public open val mediaCost: Long = 100L

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val origin: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      val look: Vec3 = OperatorUtils.getVec3(args, 1, this.getArgc());
      env.assertVecInRange(origin);
      val blockHitResult: BlockHitResult = env.getWorld()
         .clip(new ClipContext(origin, Action.Companion.raycastEnd(origin, look), Block.COLLIDER, Fluid.NONE, env.getCaster() as Entity));
      val var10000: java.util.List;
      if (blockHitResult.getType() === Type.BLOCK && env.isVecInRange(Vec3.atCenterOf(blockHitResult.getBlockPos() as Vec3i))) {
         val var8: Vector3f = blockHitResult.getDirection().step();
         var10000 = CollectionsKt.listOf(new Vec3Iota(new Vec3(var8)));
      } else {
         var10000 = CollectionsKt.listOf(new NullIota());
      }

      return var10000;
   }

   override fun executeWithOpCount(args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
      return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
   }
}

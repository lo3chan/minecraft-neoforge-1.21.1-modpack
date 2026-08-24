package at.petrak.hexcasting.common.casting.actions.raycast

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import java.util.Optional
import java.util.function.Predicate
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpEntityRaycast.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpEntityRaycast.kt\nat/petrak/hexcasting/common/casting/actions/raycast/OpEntityRaycast\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,85:1\n310#2:86\n*S KotlinDebug\n*F\n+ 1 OpEntityRaycast.kt\nat/petrak/hexcasting/common/casting/actions/raycast/OpEntityRaycast\n*L\n39#1:86\n*E\n"])
public object OpEntityRaycast : ConstMediaAction {
   public open val argc: Int = 2
   public open val mediaCost: Long = 100L

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val origin: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      val endp: Vec3 = Action.Companion.raycastEnd(origin, OperatorUtils.getVec3(args, 1, this.getArgc()));
      env.assertVecInRange(origin);
      val var10001: Entity = env.getCaster() as Entity;
      val var10002: ServerLevel = env.getWorld();
      val entityHitResult: EntityHitResult = this.getEntityHitResult(
         var10001, var10002 as Level, origin, endp, new AABB(origin, endp), OpEntityRaycast::execute$lambda$0, 1000000.0
      );
      val var10000: java.util.List;
      if (entityHitResult != null && env.isEntityInRange(entityHitResult.getEntity())) {
         val `$this$asActionResult$iv`: Entity = entityHitResult.getEntity();
         var10000 = CollectionsKt.listOf(if (`$this$asActionResult$iv` == null) new NullIota() else new EntityIota(`$this$asActionResult$iv`));
      } else {
         var10000 = CollectionsKt.listOf(new NullIota());
      }

      return var10000;
   }

   public fun getEntityHitResult(entity: Entity?, level: Level, startPos: Vec3, endPos: Vec3, aabb: AABB, isValid: Predicate<Entity>, maxSqrLength: Double): EntityHitResult? {
      var sqrLength: Double = maxSqrLength;
      var hitEntity: Entity = null;
      var hitPos: Vec3 = null;

      for (Object var10000 : level.getEntities(entity, aabb, isValid)) {
         val nextEntity: Entity = var10000 as Entity;
         val hitBox: AABB = (var10000 as Entity).getBoundingBox().inflate((double)(var10000 as Entity).getPickRadius());
         val overlapBox: Optional = hitBox.clip(startPos, endPos);
         if (hitBox.contains(startPos)) {
            if (sqrLength >= 0.0) {
               hitEntity = nextEntity;
               hitPos = overlapBox.orElse(startPos);
               sqrLength = 0.0;
            }
         } else if (overlapBox.isPresent()) {
            var10000 = overlapBox.get();
            val maybePos: Vec3 = var10000 as Vec3;
            val sqrDist: Double = startPos.distanceToSqr(var10000 as Vec3);
            if (sqrDist < sqrLength || sqrLength == 0.0) {
               if (nextEntity.getRootVehicle() === (if (entity != null) entity.getRootVehicle() else null)) {
                  if (sqrLength == 0.0) {
                     hitEntity = nextEntity;
                     hitPos = maybePos;
                  }
               } else {
                  hitEntity = nextEntity;
                  hitPos = maybePos;
                  sqrLength = sqrDist;
               }
            }
         }
      }

      val var21: EntityHitResult;
      if (hitEntity == null) {
         var21 = null;
      } else {
         var21 = new EntityHitResult(hitEntity, hitPos);
      }

      return var21;
   }

   override fun executeWithOpCount(args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
      return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
   }

   @JvmStatic
   fun `execute$lambda$0`(it: Entity): Boolean {
      return true;
   }
}

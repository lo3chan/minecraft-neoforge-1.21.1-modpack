package at.petrak.hexcasting.common.casting.actions.selectors

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import java.util.function.Predicate
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpGetEntityAt.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpGetEntityAt.kt\nat/petrak/hexcasting/common/casting/actions/selectors/OpGetEntityAt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,27:1\n1056#2:28\n310#3:29\n*S KotlinDebug\n*F\n+ 1 OpGetEntityAt.kt\nat/petrak/hexcasting/common/casting/actions/selectors/OpGetEntityAt\n*L\n21#1:28\n24#1:29\n*E\n"])
public class OpGetEntityAt(checker: Predicate<Entity>) : ConstMediaAction {
   public final val checker: Predicate<Entity>
   public open val argc: Int

   init {
      this.checker = checker;
      this.argc = 1;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val pos: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      env.assertVecInRange(pos);
      val var10000: java.util.List = env.getWorld()
         .getEntities(null, new AABB(pos.add(new Vec3(-0.5, -0.5, -0.5)), pos.add(new Vec3(0.5, 0.5, 0.5))), OpGetEntityAt::execute$lambda$1);
      val var9: Entity = CollectionsKt.getOrNull(CollectionsKt.sortedWith(var10000, new OpGetEntityAt$execute$$inlined$sortedBy$1(pos)), 0) as Entity;
      return CollectionsKt.listOf(if (var9 == null) new NullIota() else new EntityIota(var9));
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
   fun `execute$lambda$0`(`$env`: CastingEnvironment, `this$0`: OpGetEntityAt, it: Entity): Boolean {
      val var10000: OpGetEntitiesBy.Companion = OpGetEntitiesBy.Companion;
      return var10000.isReasonablySelectable(`$env`, it) && `this$0`.checker.test(it);
   }

   @JvmStatic
   fun `execute$lambda$1`(`$tmp0`: Function1, p0: Any): Boolean {
      return `$tmp0`.invoke(p0) as java.lang.Boolean;
   }
}

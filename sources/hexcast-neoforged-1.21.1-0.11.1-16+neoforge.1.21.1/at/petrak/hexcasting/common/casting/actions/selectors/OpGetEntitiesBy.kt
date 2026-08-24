package at.petrak.hexcasting.common.casting.actions.selectors

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import java.util.ArrayList
import java.util.function.Predicate
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpGetEntitiesBy.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpGetEntitiesBy.kt\nat/petrak/hexcasting/common/casting/actions/selectors/OpGetEntitiesBy\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,57:1\n1056#2:58\n1563#2:59\n1634#2,3:60\n304#3:63\n*S KotlinDebug\n*F\n+ 1 OpGetEntitiesBy.kt\nat/petrak/hexcasting/common/casting/actions/selectors/OpGetEntitiesBy\n*L\n33#1:58\n34#1:59\n34#1:60,3\n34#1:63\n*E\n"])
public class OpGetEntitiesBy(checker: Predicate<Entity>, negate: Boolean) : ConstMediaAction {
   public final val checker: Predicate<Entity>
   public final val negate: Boolean
   public open val argc: Int

   init {
      this.checker = checker;
      this.negate = negate;
      this.argc = 2;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val pos: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      val radius: Double = OperatorUtils.getPositiveDouble(args, 1, this.getArgc());
      env.assertVecInRange(pos);
      val var10000: java.util.List = env.getWorld()
         .getEntities(
            null, new AABB(pos.add(new Vec3(-radius, -radius, -radius)), pos.add(new Vec3(radius, radius, radius))), OpGetEntitiesBy::execute$lambda$1
         );
      val var19: java.lang.Iterable = CollectionsKt.sortedWith(var10000, new OpGetEntitiesBy$execute$$inlined$sortedBy$1(pos));
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var19, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(new EntityIota(`item$iv$iv` as Entity));
      }

      return CollectionsKt.listOf(new ListIota(`destination$iv$iv` as MutableList<Iota>));
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
   fun `execute$lambda$0`(`$env`: CastingEnvironment, `$pos`: Vec3, `$radius`: Double, `this$0`: OpGetEntitiesBy, it: Entity): Boolean {
      val var10000: OpGetEntitiesBy.Companion = Companion;
      return var10000.isReasonablySelectable(`$env`, it) && it.distanceToSqr(`$pos`) <= `$radius` * `$radius` && `this$0`.checker.test(it) != `this$0`.negate;
   }

   @JvmStatic
   fun `execute$lambda$1`(`$tmp0`: Function1, p0: Any): Boolean {
      return `$tmp0`.invoke(p0) as java.lang.Boolean;
   }

   public companion object {
      public fun isReasonablySelectable(ctx: CastingEnvironment, e: Entity): Boolean {
         return ctx.isEntityInRange(e) && e.isAlive() && !e.isSpectator();
      }

      public fun isAnimal(e: Entity): Boolean {
         return e is Animal || e is WaterAnimal;
      }

      public fun isMonster(e: Entity): Boolean {
         return e is Enemy;
      }

      public fun isItem(e: Entity): Boolean {
         return e is ItemEntity;
      }

      public fun isPlayer(e: Entity): Boolean {
         return e is Player;
      }

      public fun isLiving(e: Entity): Boolean {
         return e is LivingEntity;
      }
   }
}

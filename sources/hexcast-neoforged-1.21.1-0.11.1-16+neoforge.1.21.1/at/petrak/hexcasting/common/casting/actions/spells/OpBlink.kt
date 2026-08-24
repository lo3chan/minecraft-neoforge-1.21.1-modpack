package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import at.petrak.hexcasting.api.casting.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.common.casting.actions.spells.great.OpTeleport
import kotlin.math.MathKt
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

public object OpBlink : SpellAction {
   public open val argc: Int = 2

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      val delta: Double = OperatorUtils.getDouble(args, 1, this.getArgc());
      env.assertEntityInRange(target);
      if (target.canUsePortal(false) && !target.getType().is(HexTags.Entities.CANNOT_TELEPORT)) {
         val dvec: Vec3 = target.getLookAngle().scale(delta);
         val endPos: Vec3 = target.position().add(dvec);
         if (!HexConfig.server().canTeleportInThisDimension(env.getWorld().dimension())) {
            throw new MishapBadLocation(endPos, "bad_dimension");
         } else {
            env.assertVecInRange(target.position());
            env.assertVecInRange(endPos);
            if (!env.isVecInWorld(endPos.subtract(0.0, 1.0, 0.0))) {
               throw new MishapBadLocation(endPos, "too_close_to_out");
            } else {
               val targetMiddlePos: Vec3 = target.position().add(0.0, (double)target.getEyeHeight() / 2.0, 0.0);
               val var10002: RenderedSpell = new OpBlink.Spell(target, delta);
               val var10003: Long = MathKt.roundToLong((double)50000L * Math.abs(delta) * 0.5);
               val var9: Array<ParticleSpray> = new ParticleSpray[2];
               var var10006: ParticleSpray.Companion = ParticleSpray.Companion;
               var9[0] = var10006.cloud(targetMiddlePos, 2.0, 50);
               var10006 = ParticleSpray.Companion;
               val var10007: Vec3 = targetMiddlePos.add(dvec);
               var9[1] = var10006.burst(var10007, 2.0, 100);
               return new SpellAction.Result(var10002, var10003, CollectionsKt.listOf(var9), 0L, 8, null);
            }
         }
      } else {
         throw new MishapImmuneEntity(target);
      }
   }

   override fun hasCastingSound(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
   }

   override fun awardsCastingStat(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
   }

   override fun executeWithUserdata(args: MutableList<Iota>, env: CastingEnvironment, userData: CompoundTag): SpellAction.Result {
      return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return SpellAction.DefaultImpls.operate(this, env, image, continuation);
   }

   private data class Spell(target: Entity, delta: Double) : RenderedSpell {
      public final val target: Entity
      public final val delta: Double

      init {
         this.target = target;
         this.delta = delta;
      }

      public override fun cast(env: CastingEnvironment) {
         if (HexConfig.server().canTeleportInThisDimension(env.getWorld().dimension())) {
            val delta: Vec3 = this.target.getLookAngle().scale(this.delta);
            val var10000: OpTeleport = OpTeleport.INSTANCE;
            val var10001: Entity = this.target;
            val var10003: ServerLevel = env.getWorld();
            var10000.teleportRespectSticky(var10001, delta, var10003);
         }
      }

      public operator fun component1(): Entity {
         return this.target;
      }

      public operator fun component2(): Double {
         return this.delta;
      }

      public fun copy(target: Entity = this.target, delta: Double = this.delta): at.petrak.hexcasting.common.casting.actions.spells.OpBlink.Spell {
         return new OpBlink.Spell(target, delta);
      }

      public override fun toString(): String {
         return "Spell(target=${this.target}, delta=${this.delta})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode() * 31 + java.lang.Double.hashCode(this.delta);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpBlink.Spell) {
            return false;
         } else {
            val var2: OpBlink.Spell = other as OpBlink.Spell;
            if (!(this.target == (other as OpBlink.Spell).target)) {
               return false;
            } else {
               return java.lang.Double.compare(this.delta, var2.delta) == 0;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}

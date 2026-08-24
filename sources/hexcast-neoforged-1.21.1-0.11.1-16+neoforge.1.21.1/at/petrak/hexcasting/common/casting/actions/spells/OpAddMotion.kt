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
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

public object OpAddMotion : SpellAction {
   public open val argc: Int
      public open get() {
         return 2;
      }


   public final val MAX_MOTION: Double = 8192.0

   public override fun executeWithUserdata(args: List<Iota>, env: CastingEnvironment, userData: CompoundTag): Result {
      val target: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      val motion: Vec3 = OperatorUtils.getVec3(args, 1, this.getArgc());
      env.assertEntityInRange(target);
      var motionForCost: Double = motion.lengthSqr();
      if (CastingImage.Companion.checkAndMarkGivenMotion(userData, target)) {
         motionForCost++;
      }

      val shrunkMotion: Vec3 = if (motion.lengthSqr() > MAX_MOTION * MAX_MOTION) motion.normalize().scale(MAX_MOTION) else motion;
      val var10002: RenderedSpell = new OpAddMotion.Spell(target, shrunkMotion);
      val var10003: Long = (long)(motionForCost * 10000L);
      val var10006: Vec3 = target.position().add(0.0, (double)target.getEyeHeight() / 2.0, 0.0);
      val var10007: Vec3 = motion.normalize();
      return new SpellAction.Result(var10002, var10003, CollectionsKt.listOf(new ParticleSpray(var10006, var10007, 0.0, 0.1, 0, 16, null)), 0L, 8, null);
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      throw new IllegalStateException();
   }

   override fun hasCastingSound(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
   }

   override fun awardsCastingStat(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return SpellAction.DefaultImpls.operate(this, env, image, continuation);
   }

   private data class Spell(target: Entity, motion: Vec3) : RenderedSpell {
      public final val target: Entity
      public final val motion: Vec3

      init {
         this.target = target;
         this.motion = motion;
      }

      public override fun cast(env: CastingEnvironment) {
         this.target.push(this.motion.x, this.motion.y, this.motion.z);
         this.target.hurtMarked = true;
      }

      public operator fun component1(): Entity {
         return this.target;
      }

      public operator fun component2(): Vec3 {
         return this.motion;
      }

      public fun copy(target: Entity = this.target, motion: Vec3 = this.motion): at.petrak.hexcasting.common.casting.actions.spells.OpAddMotion.Spell {
         return new OpAddMotion.Spell(target, motion);
      }

      public override fun toString(): String {
         return "Spell(target=${this.target}, motion=${this.motion})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode() * 31 + this.motion.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpAddMotion.Spell) {
            return false;
         } else {
            val var2: OpAddMotion.Spell = other as OpAddMotion.Spell;
            if (!(this.target == (other as OpAddMotion.Spell).target)) {
               return false;
            } else {
               return this.motion == var2.motion;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}

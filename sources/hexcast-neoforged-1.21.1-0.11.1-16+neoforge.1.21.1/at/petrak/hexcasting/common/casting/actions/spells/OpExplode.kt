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
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level.ExplosionInteraction
import net.minecraft.world.phys.Vec3

public class OpExplode(fire: Boolean) : SpellAction {
   public final val fire: Boolean

   public open val argc: Int
      public open get() {
         return 2;
      }


   init {
      this.fire = fire;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val pos: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      val strength: Double = OperatorUtils.getPositiveDoubleUnderInclusive(args, 1, 10.0, this.getArgc());
      env.assertVecInRange(pos);
      val cost: Double = 10000L * (3 * Mth.clamp(strength, 0.0, 10.0) + (if (this.fire) 1.0 else 0.125));
      return new SpellAction.Result(
         new OpExplode.Spell(pos, strength, this.fire), (long)cost, CollectionsKt.listOf(ParticleSpray.Companion.burst(pos, strength, 50)), 0L, 8, null
      );
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

   private data class Spell(pos: Vec3, strength: Double, fire: Boolean) : RenderedSpell {
      public final val pos: Vec3
      public final val strength: Double
      public final val fire: Boolean

      init {
         this.pos = pos;
         this.strength = strength;
         this.fire = fire;
      }

      public override fun cast(env: CastingEnvironment) {
         if (env.canEditBlockAt(BlockPos.containing(this.pos as Position))) {
            env.getWorld().explode(env.getCaster() as Entity, this.pos.x, this.pos.y, this.pos.z, (float)this.strength, this.fire, ExplosionInteraction.NONE);
         }
      }

      public operator fun component1(): Vec3 {
         return this.pos;
      }

      public operator fun component2(): Double {
         return this.strength;
      }

      public operator fun component3(): Boolean {
         return this.fire;
      }

      public fun copy(pos: Vec3 = this.pos, strength: Double = this.strength, fire: Boolean = this.fire): at.petrak.hexcasting.common.casting.actions.spells.OpExplode.Spell {
         return new OpExplode.Spell(pos, strength, fire);
      }

      public override fun toString(): String {
         return "Spell(pos=${this.pos}, strength=${this.strength}, fire=${this.fire})";
      }

      public override fun hashCode(): Int {
         return (this.pos.hashCode() * 31 + java.lang.Double.hashCode(this.strength)) * 31 + java.lang.Boolean.hashCode(this.fire);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpExplode.Spell) {
            return false;
         } else {
            val var2: OpExplode.Spell = other as OpExplode.Spell;
            if (!(this.pos == (other as OpExplode.Spell).pos)) {
               return false;
            } else if (java.lang.Double.compare(this.strength, var2.strength) != 0) {
               return false;
            } else {
               return this.fire == var2.fire;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}

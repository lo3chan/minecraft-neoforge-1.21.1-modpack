package at.petrak.hexcasting.api.casting.castables

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import java.util.ArrayList
import net.minecraft.nbt.CompoundTag

public interface SpellAction : Action {
   public val argc: Int

   public open fun hasCastingSound(ctx: CastingEnvironment): Boolean {
   }

   public open fun awardsCastingStat(ctx: CastingEnvironment): Boolean {
   }

   public abstract fun execute(args: List<Iota>, env: CastingEnvironment): at.petrak.hexcasting.api.casting.castables.SpellAction.Result {
   }

   public open fun executeWithUserdata(args: List<Iota>, env: CastingEnvironment, userData: CompoundTag): at.petrak.hexcasting.api.casting.castables.SpellAction.Result {
   }

   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun hasCastingSound(`$this`: SpellAction, ctx: CastingEnvironment): Boolean {
         return true;
      }

      @JvmStatic
      fun awardsCastingStat(`$this`: SpellAction, ctx: CastingEnvironment): Boolean {
         return true;
      }

      @JvmStatic
      fun executeWithUserdata(`$this`: SpellAction, args: MutableList<Iota>, env: CastingEnvironment, userData: CompoundTag): SpellAction.Result {
         return `$this`.execute(args, env);
      }

      @JvmStatic
      fun operate(`$this`: SpellAction, env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
         val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
         if (`$this`.getArgc() > stack.size()) {
            throw new MishapNotEnoughArgs(`$this`.getArgc(), stack.size());
         } else {
            val args: java.util.List = CollectionsKt.takeLast(stack, `$this`.getArgc());
            var userDataMut: Int = 0;

            for (int result = $this.getArgc(); _i < result; _i++) {
               stack.removeLast();
            }

            val var11: CompoundTag = image.getUserData().copy();
            val var12: SpellAction.Result = `$this`.executeWithUserdata(args, env, var11);
            val sideEffects: java.util.List = new ArrayList();
            if (var12.getCost() > 0L) {
               sideEffects.add(new OperatorSideEffect.ConsumeMedia(var12.getCost()));
            }

            sideEffects.add(new OperatorSideEffect.AttemptSpell(var12.getEffect(), `$this`.hasCastingSound(env), `$this`.awardsCastingStat(env)));

            for (ParticleSpray spray : result.getParticles()) {
               sideEffects.add(new OperatorSideEffect.Particles(sound));
            }

            val var13: CastingImage = CastingImage.copy$default(image, stack, 0, null, false, image.getOpsConsumed() + var12.getOpCount(), var11, 14, null);
            val var14: EvalSound = if (`$this`.hasCastingSound(env)) HexEvalSounds.SPELL else HexEvalSounds.MUTE;
            return new OperationResult(var13, sideEffects, continuation, var14);
         }
      }
   }

   public data class Result(effect: RenderedSpell, cost: Long, particles: List<ParticleSpray>, opCount: Long = 1L) {
      public final val effect: RenderedSpell
      public final val cost: Long
      public final val particles: List<ParticleSpray>
      public final val opCount: Long

      init {
         this.effect = effect;
         this.cost = cost;
         this.particles = particles;
         this.opCount = opCount;
      }

      public operator fun component1(): RenderedSpell {
         return this.effect;
      }

      public operator fun component2(): Long {
         return this.cost;
      }

      public operator fun component3(): List<ParticleSpray> {
         return this.particles;
      }

      public operator fun component4(): Long {
         return this.opCount;
      }

      public fun copy(
         effect: RenderedSpell = this.effect,
         cost: Long = this.cost,
         particles: List<ParticleSpray> = this.particles,
         opCount: Long = this.opCount
      ): at.petrak.hexcasting.api.casting.castables.SpellAction.Result {
         return new SpellAction.Result(effect, cost, particles, opCount);
      }

      public override fun toString(): String {
         return "Result(effect=${this.effect}, cost=${this.cost}, particles=${this.particles}, opCount=${this.opCount})";
      }

      public override fun hashCode(): Int {
         return ((this.effect.hashCode() * 31 + java.lang.Long.hashCode(this.cost)) * 31 + this.particles.hashCode()) * 31
            + java.lang.Long.hashCode(this.opCount);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is SpellAction.Result) {
            return false;
         } else {
            val var2: SpellAction.Result = other as SpellAction.Result;
            if (!(this.effect == (other as SpellAction.Result).effect)) {
               return false;
            } else if (this.cost != var2.cost) {
               return false;
            } else if (!(this.particles == var2.particles)) {
               return false;
            } else {
               return this.opCount == var2.opCount;
            }
         }
      }
   }
}

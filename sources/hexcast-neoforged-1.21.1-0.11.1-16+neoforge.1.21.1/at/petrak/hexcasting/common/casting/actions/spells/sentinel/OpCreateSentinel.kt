package at.petrak.hexcasting.common.casting.actions.spells.sentinel

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
import at.petrak.hexcasting.api.player.Sentinel
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

public class OpCreateSentinel(extendsRange: Boolean) : SpellAction {
   public final val extendsRange: Boolean
   public open val argc: Int

   init {
      this.extendsRange = extendsRange;
      this.argc = 1;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      env.assertVecInRange(target);
      return new SpellAction.Result(
         new OpCreateSentinel.Spell(target, this.extendsRange),
         10000L * (if (this.extendsRange) 2 else 1),
         CollectionsKt.listOf(ParticleSpray.Companion.burst$default(ParticleSpray.Companion, target, 2.0, 0, 4, null)),
         0L,
         8,
         null
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

   private data class Spell(target: Vec3, extendsRange: Boolean) : RenderedSpell {
      public final val target: Vec3
      public final val extendsRange: Boolean

      init {
         this.target = target;
         this.extendsRange = extendsRange;
      }

      public override fun cast(env: CastingEnvironment) {
         IXplatAbstractions.INSTANCE.setSentinel(env.getCaster() as Player, new Sentinel(this.extendsRange, this.target, env.getWorld().dimension()));
      }

      public operator fun component1(): Vec3 {
         return this.target;
      }

      public operator fun component2(): Boolean {
         return this.extendsRange;
      }

      public fun copy(target: Vec3 = this.target, extendsRange: Boolean = this.extendsRange): at.petrak.hexcasting.common.casting.actions.spells.sentinel.OpCreateSentinel.Spell {
         return new OpCreateSentinel.Spell(target, extendsRange);
      }

      public override fun toString(): String {
         return "Spell(target=${this.target}, extendsRange=${this.extendsRange})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode() * 31 + java.lang.Boolean.hashCode(this.extendsRange);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpCreateSentinel.Spell) {
            return false;
         } else {
            val var2: OpCreateSentinel.Spell = other as OpCreateSentinel.Spell;
            if (!(this.target == (other as OpCreateSentinel.Spell).target)) {
               return false;
            } else {
               return this.extendsRange == var2.extendsRange;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}

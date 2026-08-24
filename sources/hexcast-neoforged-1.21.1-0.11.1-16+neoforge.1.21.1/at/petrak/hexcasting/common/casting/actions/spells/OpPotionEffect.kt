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
import net.minecraft.core.Holder
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3

public class OpPotionEffect(effect: Holder<MobEffect>, baseCost: Long, allowPotency: Boolean, potencyCubic: Boolean) : SpellAction {
   public final val effect: Holder<MobEffect>
   public final val baseCost: Long
   public final val allowPotency: Boolean
   public final val potencyCubic: Boolean

   public open val argc: Int
      public open get() {
         return if (this.allowPotency) 3 else 2;
      }


   init {
      this.effect = effect;
      this.baseCost = baseCost;
      this.allowPotency = allowPotency;
      this.potencyCubic = potencyCubic;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: LivingEntity = OperatorUtils.getLivingEntityButNotArmorStand(args, 0, this.getArgc());
      val duration: Double = OperatorUtils.getPositiveDouble(args, 1, this.getArgc());
      val potency: Double = if (this.allowPotency) OperatorUtils.getPositiveDoubleUnderInclusive(args, 2, 127.0, this.getArgc()) else 1.0;
      env.assertEntityInRange(target as Entity);
      val cost: Double = this.baseCost * duration * (if (this.potencyCubic) potency * potency * potency else potency * potency);
      val var10002: RenderedSpell = new OpPotionEffect.Spell(this.effect, target, duration, potency);
      val var10003: Long = (long)cost;
      val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10005: Vec3 = target.position().add(0.0, (double)target.getEyeHeight() / 2.0, 0.0);
      return new SpellAction.Result(
         var10002, var10003, CollectionsKt.listOf(ParticleSpray.Companion.cloud$default(var10004, var10005, 1.0, 0, 4, null)), 0L, 8, null
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

   private class Spell(effect: Holder<MobEffect>, target: LivingEntity, duration: Double, potency: Double) : RenderedSpell {
      public final val effect: Holder<MobEffect>
      public final val target: LivingEntity
      public final val duration: Double
      public final val potency: Double

      init {
         this.effect = effect;
         this.target = target;
         this.duration = duration;
         this.potency = potency;
      }

      public override fun cast(env: CastingEnvironment) {
         if (this.duration > 0.05) {
            this.target.addEffect(new MobEffectInstance(this.effect, (int)(this.duration * (double)20), (int)this.potency - 1));
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}

package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.ktxt.AccessorWrappers
import java.util.Arrays
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.phys.Vec3

public abstract class Mishap : java.lang.Throwable {
   public abstract fun accentColor(ctx: CastingEnvironment, errorCtx: at.petrak.hexcasting.api.casting.mishaps.Mishap.Context): FrozenPigment {
   }

   public open fun particleSpray(ctx: CastingEnvironment): ParticleSpray {
      val var10002: Vec3 = ctx.mishapSprayPos().add(0.0, 0.2, 0.0);
      return new ParticleSpray(var10002, new Vec3(0.0, 2.0, 0.0), 0.2, 0.7853981633974483, 40);
   }

   public open fun resolutionType(ctx: CastingEnvironment): ResolvedPatternType {
      return ResolvedPatternType.ERRORED;
   }

   public abstract fun execute(env: CastingEnvironment, errorCtx: at.petrak.hexcasting.api.casting.mishaps.Mishap.Context, stack: MutableList<Iota>) {
   }

   protected abstract fun errorMessage(ctx: CastingEnvironment, errorCtx: at.petrak.hexcasting.api.casting.mishaps.Mishap.Context): Component? {
   }

   public fun executeReturnStack(ctx: CastingEnvironment, errorCtx: at.petrak.hexcasting.api.casting.mishaps.Mishap.Context, stack: MutableList<Iota>): List<
         Iota
      > {
      this.execute(ctx, errorCtx, stack);
      return stack;
   }

   public fun errorMessageWithName(ctx: CastingEnvironment, errorCtx: at.petrak.hexcasting.api.casting.mishaps.Mishap.Context): Component? {
      val var10000: Component;
      if (errorCtx.getName() != null) {
         val var3: Array<Any> = new Object[]{errorCtx.getName(), null};
         val var10003: Component = this.errorMessage(ctx, errorCtx);
         if (var10003 == null) {
            return null;
         }

         var3[1] = var10003;
         var10000 = HexUtils.asTranslatedComponent("hexcasting.mishap", var3) as Component;
      } else {
         var10000 = this.errorMessage(ctx, errorCtx);
      }

      return var10000;
   }

   protected fun dyeColor(color: DyeColor): FrozenPigment {
      val var10004: Any = HexItems.DYE_PIGMENTS.get(color);
      return new FrozenPigment(new ItemStack(var10004 as ItemLike), Util.NIL_UUID);
   }

   protected fun error(stub: String, vararg args: Any): Component {
      return HexUtils.asTranslatedComponent("hexcasting.mishap.$stub", Arrays.copyOf(args, args.length)) as Component;
   }

   protected fun actionName(name: Component?): Component {
      var var10000: Component = name;
      if (name == null) {
         var10000 = HexUtils.getLightPurple(HexUtils.getAsTranslatedComponent("hexcasting.spell.null")) as Component;
      }

      return var10000;
   }

   protected fun blockAtPos(ctx: CastingEnvironment, pos: BlockPos): Component {
      val var10000: MutableComponent = ctx.getWorld().getBlockState(pos).getBlock().getName();
      return var10000 as Component;
   }

   public companion object {
      public fun trulyHurt(entity: LivingEntity, source: DamageSource, amount: Float) {
         AccessorWrappers.setHurtWithStamp(entity, source, entity.level().getGameTime());
         val targetHealth: Float = entity.getHealth() - amount;
         if (entity.invulnerableTime > 10) {
            if (AccessorWrappers.getLastHurt(entity) < amount) {
               entity.invulnerableTime = 0;
            } else {
               AccessorWrappers.setLastHurt(entity, AccessorWrappers.getLastHurt(entity) - amount);
            }
         }

         if (!entity.hurt(source, amount) && !entity.isInvulnerableTo(source) && !entity.level().isClientSide && !entity.isDeadOrDying()) {
            entity.setHealth(targetHealth);
            AccessorWrappers.markHurt(entity as Entity);
            if (entity.isDeadOrDying()) {
               if (!AccessorWrappers.checkTotemDeathProtection(entity, source)) {
                  val var6: SoundEvent = AccessorWrappers.getDeathSoundAccessor(entity);
                  if (var6 != null) {
                     entity.playSound(var6, AccessorWrappers.getSoundVolumeAccessor(entity), entity.getVoicePitch());
                  }

                  entity.die(source);
               }
            } else {
               AccessorWrappers.playHurtSound(entity, source);
            }
         }
      }
   }

   public data class Context(pattern: HexPattern?, name: Component?) {
      public final val pattern: HexPattern?
      public final val name: Component?

      init {
         this.pattern = pattern;
         this.name = name;
      }

      public operator fun component1(): HexPattern? {
         return this.pattern;
      }

      public operator fun component2(): Component? {
         return this.name;
      }

      public fun copy(pattern: HexPattern? = this.pattern, name: Component? = this.name): at.petrak.hexcasting.api.casting.mishaps.Mishap.Context {
         return new Mishap.Context(pattern, name);
      }

      public override fun toString(): String {
         return "Context(pattern=${this.pattern}, name=${this.name})";
      }

      public override fun hashCode(): Int {
         return (if (this.pattern == null) 0 else this.pattern.hashCode()) * 31 + (if (this.name == null) 0 else this.name.hashCode());
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is Mishap.Context) {
            return false;
         } else {
            val var2: Mishap.Context = other as Mishap.Context;
            if (!(this.pattern == (other as Mishap.Context).pattern)) {
               return false;
            } else {
               return this.name == var2.name;
            }
         }
      }
   }
}

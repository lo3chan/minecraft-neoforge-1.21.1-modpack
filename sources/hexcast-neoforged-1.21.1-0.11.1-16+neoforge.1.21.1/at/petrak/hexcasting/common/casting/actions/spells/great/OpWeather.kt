package at.petrak.hexcasting.common.casting.actions.spells.great

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel

public class OpWeather(rain: Boolean) : SpellAction {
   public final val rain: Boolean
   public open val argc: Int

   init {
      this.rain = rain;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      return new SpellAction.Result(new OpWeather.Spell(this.rain), if (this.rain) 100000L else 50000L, CollectionsKt.emptyList(), 0L, 8, null);
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

   private data class Spell(rain: Boolean) : RenderedSpell {
      public final val rain: Boolean

      init {
         this.rain = rain;
      }

      public override fun cast(env: CastingEnvironment) {
         val w: ServerLevel = env.getWorld();
         if (w.isRaining() != this.rain) {
            w.getLevelData().setRaining(this.rain);
            val var3: Pair = if (this.rain) TuplesKt.to(30, 90) else TuplesKt.to(60, 180);
            val time: Int = w.random.nextInt((var3.component1() as java.lang.Number).intValue(), (var3.component2() as java.lang.Number).intValue()) * 20 * 60;
            if (this.rain) {
               w.setWeatherParameters(0, time, true, w.random.nextDouble() < 0.05);
            } else {
               w.setWeatherParameters(time, 0, false, false);
            }
         }
      }

      public operator fun component1(): Boolean {
         return this.rain;
      }

      public fun copy(rain: Boolean = this.rain): at.petrak.hexcasting.common.casting.actions.spells.great.OpWeather.Spell {
         return new OpWeather.Spell(rain);
      }

      public override fun toString(): String {
         return "Spell(rain=${this.rain})";
      }

      public override fun hashCode(): Int {
         return java.lang.Boolean.hashCode(this.rain);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpWeather.Spell) {
            return false;
         } else {
            return this.rain == (other as OpWeather.Spell).rain;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}

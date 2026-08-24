package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3

public class MishapDivideByZero(operand1: Component, operand2: Component, suffix: String = "divide") : Mishap {
   public final val operand1: Component
   public final val operand2: Component
   public final val suffix: String

   init {
      this.operand1 = operand1;
      this.operand2 = operand2;
      this.suffix = suffix;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.RED);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      stack.add(new GarbageIota());
      env.getMishapEnvironment().damage(0.5F);
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("divide_by_zero.${this.suffix}", new Object[]{this.operand1, this.operand2});
   }

   public companion object {
      private const val PREFIX: String

      @JvmStatic
      public final val zero: MutableComponent
         public final get() {
            return HexUtils.getAsTranslatedComponent("hexcasting.mishap.divide_by_zero.zero");
         }


      @JvmStatic
      public final val zerothPower: MutableComponent
         public final get() {
            return HexUtils.getAsTranslatedComponent("hexcasting.mishap.divide_by_zero.zero.power");
         }


      @JvmStatic
      public final val zeroVector: MutableComponent
         public final get() {
            return HexUtils.getAsTranslatedComponent("hexcasting.mishap.divide_by_zero.zero.vec");
         }


      public fun of(operand1: Double, operand2: Double, suffix: String = "divide"): MishapDivideByZero {
         return if (suffix == "exponent")
            new MishapDivideByZero(this.translate(new DoubleIota(operand1)), this.powerOf(new DoubleIota(operand2)), suffix)
            else
            new MishapDivideByZero(this.translate(new DoubleIota(operand1)), this.translate(new DoubleIota(operand2)), suffix);
      }

      public fun of(operand1: Iota, operand2: Iota, suffix: String = "divide"): MishapDivideByZero {
         return if (suffix == "exponent")
            new MishapDivideByZero(this.translate(operand1), this.powerOf(operand2), suffix)
            else
            new MishapDivideByZero(this.translate(operand1), this.translate(operand2), suffix);
      }

      public fun tan(angle: Double): MishapDivideByZero {
         val translatedAngle: Component = this.translate(new DoubleIota(angle));
         return new MishapDivideByZero(
            HexUtils.asTranslatedComponent("hexcasting.mishap.divide_by_zero.sin", translatedAngle) as Component,
            HexUtils.asTranslatedComponent("hexcasting.mishap.divide_by_zero.cos", translatedAngle) as Component,
            null,
            4,
            null
         );
      }

      public fun tan(angle: DoubleIota): MishapDivideByZero {
         val translatedAngle: Component = this.translate(angle);
         return new MishapDivideByZero(
            HexUtils.asTranslatedComponent("hexcasting.mishap.divide_by_zero.sin", translatedAngle) as Component,
            HexUtils.asTranslatedComponent("hexcasting.mishap.divide_by_zero.cos", translatedAngle) as Component,
            null,
            4,
            null
         );
      }

      public fun powerOf(power: Component): MutableComponent {
         return HexUtils.asTranslatedComponent("hexcasting.mishap.divide_by_zero.power", power);
      }

      public fun powerOf(datum: Iota): Component {
         val var10000: Component;
         if (datum is DoubleIota && (datum as DoubleIota).getDouble() == 0.0) {
            var10000 = this.getZerothPower() as Component;
         } else {
            var10000 = datum.display();
         }

         return var10000;
      }

      public fun translate(datum: Iota): Component {
         val var10000: Component;
         if (datum is DoubleIota && (datum as DoubleIota).getDouble() == 0.0) {
            var10000 = this.getZero() as Component;
         } else if (datum is Vec3Iota && (datum as Vec3Iota).getVec3() == Vec3.ZERO) {
            var10000 = this.getZeroVector() as Component;
         } else {
            var10000 = datum.display();
         }

         return var10000;
      }
   }
}

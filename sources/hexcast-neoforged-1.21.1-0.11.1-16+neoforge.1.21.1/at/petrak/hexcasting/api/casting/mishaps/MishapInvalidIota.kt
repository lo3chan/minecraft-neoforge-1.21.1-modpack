package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import java.util.Arrays
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

public class MishapInvalidIota(perpetrator: Iota, reverseIdx: Int, expected: Component) : Mishap {
   public final val perpetrator: Iota
   public final val reverseIdx: Int
   public final val expected: Component

   init {
      this.perpetrator = perpetrator;
      this.reverseIdx = reverseIdx;
      this.expected = expected;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.GRAY);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      stack.set(stack.size() - 1 - this.reverseIdx, new GarbageIota());
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("invalid_value", new Object[]{this.expected, this.reverseIdx, this.perpetrator.display()});
   }

   public companion object {
      public fun ofType(perpetrator: Iota, reverseIdx: Int, name: String): MishapInvalidIota {
         return this.of(perpetrator, reverseIdx, "class.$name");
      }

      public fun of(perpetrator: Iota, reverseIdx: Int, name: String, vararg translations: Any): MishapInvalidIota {
         return new MishapInvalidIota(
            perpetrator,
            reverseIdx,
            HexUtils.asTranslatedComponent("hexcasting.mishap.invalid_value.$name", Arrays.copyOf(translations, translations.length)) as Component
         );
      }
   }
}

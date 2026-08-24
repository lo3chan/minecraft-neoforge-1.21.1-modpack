package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

public class MishapTooManyCloseParens : Mishap {
   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.ORANGE);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      if (errorCtx.getPattern() != null) {
         stack.add(new PatternIota(errorCtx.getPattern()));
      }
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("too_many_close_parens", new Object[0]);
   }
}

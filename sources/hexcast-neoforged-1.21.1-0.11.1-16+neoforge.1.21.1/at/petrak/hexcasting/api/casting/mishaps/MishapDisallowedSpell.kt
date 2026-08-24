package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

public class MishapDisallowedSpell(type: String = "disallowed") : Mishap {
   public final val type: String

   init {
      this.type = type;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.BLACK);
   }

   public override fun resolutionType(ctx: CastingEnvironment): ResolvedPatternType {
      return ResolvedPatternType.INVALID;
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error(this.type, new Object[0]);
   }

   fun MishapDisallowedSpell() {
      this(null, 1, null);
   }
}

package at.petrak.hexcasting.api.casting.mishaps.circle

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

public class MishapBoolDirectrixNotBool(perpetrator: Iota, pos: BlockPos) : Mishap {
   public final val perpetrator: Iota
   public final val pos: BlockPos

   init {
      this.perpetrator = perpetrator;
      this.pos = pos;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.GRAY);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      env.getWorld().destroyBlock(this.pos, true);
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("circle.bool_directrix_no_bool", new Object[]{this.pos.toShortString(), this.perpetrator.display()});
   }
}

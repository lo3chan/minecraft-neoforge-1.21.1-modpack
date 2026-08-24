package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

public class MishapNoAkashicRecord(pos: BlockPos) : Mishap {
   public final val pos: BlockPos

   init {
      this.pos = pos;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.PURPLE);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      env.getMishapEnvironment().removeXp(100);
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("no_akashic_record", new Object[]{this.pos.toShortString()});
   }
}

package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level.ExplosionInteraction
import net.minecraft.world.phys.Vec3

public class MishapBadBlock(pos: BlockPos, expected: Component) : Mishap {
   public final val pos: BlockPos
   public final val expected: Component

   init {
      this.pos = pos;
      this.expected = expected;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.LIME);
   }

   public override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      ctx.getWorld()
         .explode(null, (double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5, 0.25F, ExplosionInteraction.NONE);
   }

   public override fun particleSpray(ctx: CastingEnvironment): ParticleSpray {
      val var10000: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10001: Vec3 = Vec3.atCenterOf(this.pos as Vec3i);
      return ParticleSpray.Companion.burst$default(var10000, var10001, 1.0, 0, 4, null);
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("bad_block", new Object[]{this.expected, this.pos.toShortString(), this.blockAtPos(ctx, this.pos)});
   }

   public companion object {
      public fun of(pos: BlockPos, stub: String): MishapBadBlock {
         return new MishapBadBlock(pos, HexUtils.getAsTranslatedComponent("hexcasting.mishap.bad_block.$stub") as Component);
      }
   }
}

package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexDamageTypes
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3

public class MishapAlreadyBrainswept(mob: Mob) : Mishap {
   public final val mob: Mob

   init {
      this.mob = mob;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.GREEN);
   }

   public override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      this.mob.hurt(HexDamageTypes.source(this.mob.level(), HexDamageTypes.OVERCAST, ctx.getCaster() as Entity), this.mob.getHealth());
   }

   public override fun particleSpray(ctx: CastingEnvironment): ParticleSpray {
      val var10000: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10001: Vec3 = this.mob.getEyePosition();
      return ParticleSpray.Companion.burst$default(var10000, var10001, 1.0, 0, 4, null);
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("already_brainswept", new Object[0]);
   }
}

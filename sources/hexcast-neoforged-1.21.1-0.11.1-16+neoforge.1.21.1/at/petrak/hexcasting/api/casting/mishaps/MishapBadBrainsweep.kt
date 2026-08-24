package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexDamageTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.Component
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3

public class MishapBadBrainsweep(mob: Mob, pos: BlockPos) : Mishap {
   public final val mob: Mob
   public final val pos: BlockPos

   init {
      this.mob = mob;
      this.pos = pos;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.GREEN);
   }

   public override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      val var10000: Mishap.Companion = Mishap.Companion;
      val var10001: LivingEntity = this.mob as LivingEntity;
      val var10002: DamageSource = HexDamageTypes.source(this.mob.level(), HexDamageTypes.OVERCAST, ctx.getCaster() as Entity);
      var10000.trulyHurt(var10001, var10002, 1.0F);
   }

   public override fun particleSpray(ctx: CastingEnvironment): ParticleSpray {
      val var10000: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10001: Vec3 = Vec3.atCenterOf(this.pos as Vec3i);
      return ParticleSpray.Companion.burst$default(var10000, var10001, 1.0, 0, 4, null);
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("bad_brainsweep", new Object[]{this.blockAtPos(ctx, this.pos)});
   }
}

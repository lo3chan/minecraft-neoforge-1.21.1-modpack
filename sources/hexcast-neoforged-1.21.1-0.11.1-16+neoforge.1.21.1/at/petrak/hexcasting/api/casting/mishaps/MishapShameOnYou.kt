package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexDamageTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.DyeColor

public class MishapShameOnYou : Mishap {
   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.BLACK);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      val caster: ServerPlayer = env.getCaster();
      if (caster != null) {
         val var10000: Mishap.Companion = Mishap.Companion;
         val var10001: LivingEntity = caster as LivingEntity;
         val var10002: DamageSource = HexDamageTypes.source(caster.level(), HexDamageTypes.SHAME_ON_YOU);
         var10000.trulyHurt(var10001, var10002, 69420.0F);
      }
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("shame", new Object[0]);
   }
}

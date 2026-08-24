package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.advancements.HexAdvancementTriggers
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3

public class MishapUnenlightened : Mishap {
   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.RED);
   }

   public override fun resolutionType(ctx: CastingEnvironment): ResolvedPatternType {
      return ResolvedPatternType.INVALID;
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      env.getMishapEnvironment().dropHeldItems();
      val var10000: ServerPlayer = env.getCaster();
      if (var10000 != null) {
         var10000.sendSystemMessage(HexUtils.getAsTranslatedComponent("hexcasting.message.cant_great_spell") as Component, true);
      }

      val pos: Vec3 = env.mishapSprayPos();
      env.getWorld().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.5F, 0.7F);
      HexAdvancementTriggers.FAIL_GREAT_SPELL_TRIGGER.trigger(env.getCaster());
   }

   protected open fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Nothing? {
      return null;
   }
}

package net.bettercombat.neoforge;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.particle.BetterCombatParticles;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod("bettercombat")
public final class NeoForgeMod {
   public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "bettercombat");

   public NeoForgeMod(IEventBus modEventBus) {
      BetterCombatMod.init();
      modEventBus.addListener(RegisterEvent.class, NeoForgeMod::register);
      SOUND_EVENTS.register(modEventBus);
   }

   public static void register(RegisterEvent event) {
      event.register(Registries.PARTICLE_TYPE, reg -> BetterCombatParticles.register());
   }

   static {
      SoundHelper.soundKeys
         .forEach(
            soundKey -> SOUND_EVENTS.register(
               soundKey, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("bettercombat", soundKey))
            )
         );
   }
}

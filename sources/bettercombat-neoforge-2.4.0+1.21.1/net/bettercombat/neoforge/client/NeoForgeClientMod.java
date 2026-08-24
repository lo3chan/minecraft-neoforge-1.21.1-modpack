package net.bettercombat.neoforge.client;

import me.shedaniel.autoconfig.AutoConfig;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.Keybindings;
import net.bettercombat.client.particle.SlashParticle;
import net.bettercombat.config.ClientConfigWrapper;
import net.bettercombat.particle.BetterCombatParticles;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(
   modid = "bettercombat",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class NeoForgeClientMod {
   @SubscribeEvent
   public static void registerKeys(RegisterKeyMappingsEvent event) {
      Keybindings.all.forEach(event::register);
   }

   @SubscribeEvent
   public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
      for (BetterCombatParticles.Entry entry : BetterCombatParticles.ENTRIES) {
         event.registerSpriteSet(entry.particleType(), spriteSet -> new SlashParticle.Provider(spriteSet, entry.params()));
      }
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      BetterCombatClientMod.init();
      BetterCombatClientMod.loadAnimation();
      ItemProperties.registerGeneric(ResourceLocation.fromNamespaceAndPath("bettercombat", "loaded"), (stack, world, entity, seed) -> 1.0F);
      ModLoadingContext.get()
         .registerExtensionPoint(
            IConfigScreenFactory.class, () -> (modContainer, parent) -> (Screen)AutoConfig.getConfigScreen(ClientConfigWrapper.class, parent).get()
         );
   }
}

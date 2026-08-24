package dev.corgitaco.enhancedcelestials2shaders.neoforge;

import dev.corgitaco.enhancedcelestials2shaders.LunarShadersMod;
import dev.corgitaco.enhancedcelestials2shaders.neoforge.platform.NeoForgePlatformHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("enhancedcelestials2shaders")
public class LunarShadersNeoForge {
   public LunarShadersNeoForge(IEventBus modEventBus) {
      modEventBus.addListener(this::onClientSetup);
   }

   private void onClientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         LunarShadersMod.init(new NeoForgePlatformHelper());
         NeoForgeClientHandler.init();
      });
   }
}

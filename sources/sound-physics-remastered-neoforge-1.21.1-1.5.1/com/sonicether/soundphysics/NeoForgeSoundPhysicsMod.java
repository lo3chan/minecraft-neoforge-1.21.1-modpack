package com.sonicether.soundphysics;

import com.sonicether.soundphysics.integration.ClothConfigIntegration;
import java.nio.file.Path;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("sound_physics_remastered")
public class NeoForgeSoundPhysicsMod extends SoundPhysicsMod {
   public NeoForgeSoundPhysicsMod(IEventBus eventBus) {
      eventBus.addListener(this::commonSetup);
      eventBus.addListener(this::clientSetup);
   }

   public void commonSetup(FMLCommonSetupEvent event) {
      this.init();
   }

   public void clientSetup(FMLClientSetupEvent event) {
      this.initClient();
      if (isClothConfigLoaded()) {
         ModLoadingContext.get()
            .registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> ClothConfigIntegration.createConfigScreen(parent));
      }
   }

   private static boolean isClothConfigLoaded() {
      if (ModList.get().isLoaded("cloth_config")) {
         try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            Loggers.log("Using Cloth Config GUI");
            return true;
         } catch (Exception var1) {
            Loggers.log("Failed to load Cloth Config: {}", var1.getMessage());
            var1.printStackTrace();
         }
      }

      return false;
   }

   @Override
   public Path getConfigFolder() {
      return FMLLoader.getGamePath().resolve("config");
   }
}

package dev.corgitaco.enhancedcelestials2shaders.neoforge.platform;

import dev.corgitaco.enhancedcelestials2shaders.platform.PlatformHelper;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

public class NeoForgePlatformHelper implements PlatformHelper {
   @Override
   public String getPlatformName() {
      return "neoforge";
   }

   @Override
   public String getMinecraftVersion() {
      return "1.21.1";
   }

   @Override
   public boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }

   @Override
   public boolean isClient() {
      return FMLLoader.getDist().isClient();
   }

   @Override
   public boolean isDedicatedServer() {
      return FMLLoader.getDist().isDedicatedServer();
   }

   @Override
   public String getGameDirectory() {
      return FMLPaths.GAMEDIR.get().toString();
   }

   @Override
   public String getConfigDirectory() {
      return FMLPaths.CONFIGDIR.get().toString();
   }

   @Override
   public Level getClientLevel() {
      if (!this.isClient()) {
         return null;
      } else {
         Minecraft mc = Minecraft.getInstance();
         return mc != null ? mc.level : null;
      }
   }

   @Override
   public boolean areShadersActive() {
      return this.isModLoaded("iris") && IrisApi.getInstance().isShaderPackInUse();
   }
}

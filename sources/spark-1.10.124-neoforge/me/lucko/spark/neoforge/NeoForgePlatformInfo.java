package me.lucko.spark.neoforge;

import me.lucko.spark.common.platform.PlatformInfo;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforgespi.language.IModInfo;

public class NeoForgePlatformInfo implements PlatformInfo {
   private final PlatformInfo.Type type;

   public NeoForgePlatformInfo(PlatformInfo.Type type) {
      this.type = type;
   }

   @Override
   public PlatformInfo.Type getType() {
      return this.type;
   }

   @Override
   public String getName() {
      return "NeoForge";
   }

   @Override
   public String getBrand() {
      return ModList.get().getModContainerById("neoforge").map(ModContainer::getModInfo).<String>map(IModInfo::getDisplayName).orElse("NeoForge");
   }

   @Override
   public String getVersion() {
      return FMLLoader.versionInfo().neoForgeVersion();
   }

   @Override
   public String getMinecraftVersion() {
      return FMLLoader.versionInfo().mcVersion();
   }
}

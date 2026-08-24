package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.rhino.Context;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforgespi.language.IModInfo;

public class PlatformWrapper {
   private static Map<String, PlatformWrapper.ModInfo> allMods;

   @Deprecated
   public static String getName() {
      KubeJS.LOGGER.warn("Platform.getName() only exists for legacy reasons! If you have scripts that use this, please update them!");
      return "neoforge";
   }

   @Deprecated
   public static boolean isForge() {
      KubeJS.LOGGER.warn("Platform.isForge() only exists for legacy reasons! If you have scripts that use this, please update them!");
      return true;
   }

   @Deprecated
   public static boolean isFabric() {
      KubeJS.LOGGER.warn("Fabric support has been sunset; Platform.isFabric() will always return false!");
      return false;
   }

   public static String getMcVersion() {
      return "1.21.1";
   }

   public static Set<String> getList() {
      return getMods().keySet();
   }

   public static String getModVersion() {
      return KubeJS.VERSION;
   }

   public static boolean isLoaded(String modId) {
      return getMods().containsKey(modId);
   }

   public static PlatformWrapper.ModInfo getInfo(String modID) {
      return getMods().computeIfAbsent(modID, PlatformWrapper.ModInfo::new);
   }

   public static Map<String, PlatformWrapper.ModInfo> getMods() {
      if (allMods == null) {
         allMods = new LinkedHashMap<>();

         for (IModInfo mod : ModList.get().getMods()) {
            PlatformWrapper.ModInfo info = new PlatformWrapper.ModInfo(mod.getModId());
            info.name = mod.getDisplayName();
            info.version = mod.getVersion().toString();
            allMods.put(info.id, info);
         }
      }

      return allMods;
   }

   public static boolean isDevelopmentEnvironment() {
      return !FMLLoader.isProduction();
   }

   public static boolean isClientEnvironment() {
      return FMLLoader.getDist().isClient();
   }

   public static void setModName(String modId, String name) {
      getInfo(modId).setName(name);
   }

   public static int getMinecraftVersion() {
      return 2101;
   }

   public static String getMinecraftVersionString() {
      return "1.21.1";
   }

   public static boolean isGeneratingData() {
      return DatagenModLoader.isRunningDataGen();
   }

   public static void breakpoint(Context cx, Object... args) {
      KubeJS.LOGGER.info(Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", ")));
      KubeJSPlugins.forEachPlugin(p -> p.breakpoint(cx, args));
   }

   public static String getCurrentThreadName() {
      return Thread.currentThread().getName();
   }

   public static String getPackMode() {
      return CommonProperties.get().packMode;
   }

   public static class ModInfo {
      private final String id;
      private String name;
      private String version;
      private String customName;

      public ModInfo(String i) {
         this.id = i;
         this.name = this.id;
         this.version = "0.0.0";
         this.customName = "";
      }

      public String getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String n) {
         this.name = n;
         this.customName = n;

         try {
            Optional<? extends ModContainer> mc = ModList.get().getModContainerById(this.id);
            if (mc.isPresent() && mc.get().getModInfo() instanceof net.neoforged.fml.loading.moddiscovery.ModInfo i) {
               Field field = net.neoforged.fml.loading.moddiscovery.ModInfo.class.getDeclaredField("displayName");
               field.setAccessible(true);
               field.set(i, this.name);
            }
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

      public String getVersion() {
         return this.version;
      }

      public String getCustomName() {
         return this.customName;
      }
   }
}

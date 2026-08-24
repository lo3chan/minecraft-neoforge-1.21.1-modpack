package com.seibel.distanthorizons.core;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.jpountz.lz4.LZ4FrameOutputStream;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderObjectFactory;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderEvent;
import com.seibel.distanthorizons.core.api.external.methods.config.DhApiConfig;
import com.seibel.distanthorizons.core.api.external.methods.data.DhApiTerrainDataRepo;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.config.eventHandlers.IgnoredDimensionCsvHandler;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.DhApiRenderProxy;
import com.seibel.distanthorizons.core.sql.DatabaseUpdater;
import com.seibel.distanthorizons.core.world.DhApiWorldProxy;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import dh_sqlite.SQLiteConnection;
import dh_sqlite.SQLiteJDBCLoader;
import dh_sqlite.core.NativeDB;
import dhcomgithubluben.zstd.Zstd;
import dhcomgithubluben.zstd.ZstdOutputStream;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.tukaani.xz.XZOutputStream;

public class Initializer {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftSharedWrapper MC_SHARED = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);

   public static void preConfigInit() {
      LOGGER.info("Running library validation...");
      if (!MC_SHARED.isDedicatedServer()) {
         try {
            Class<TinyFileDialogs> e = TinyFileDialogs.class;
         } catch (Throwable var9) {
            MC_CLIENT.crashMinecraft("Distant Horizons critical setup error: LWJGL 3 or newer required. Error: [" + var9.getMessage() + "].", var9);
         }
      }

      try {
         Class<?> lz4Compressor = LZ4FrameOutputStream.class;
         Class<?> zstdCompressor = ZstdOutputStream.class;
         byte[] testCompressByteArray = new byte[1024];

         for (int i = 0; i < testCompressByteArray.length; i++) {
            testCompressByteArray[i] = (byte)(i % 126);
         }

         byte[] compressedBytes = Zstd.compress(testCompressByteArray);
         Zstd.decompress(compressedBytes);
         Class<?> lzmaCompressor = XZOutputStream.class;
         Class<?> config = Config.class;
         Class<?> oldFastUtil = LongArrayList.class;
         Class<?> sqliteJava = SQLiteConnection.class;
         Class<?> sqliteNative = NativeDB.class;
         boolean sqliteLoaded = SQLiteJDBCLoader.initialize();
         if (!sqliteLoaded) {
            throw new RuntimeException("Failed to load SQLite native library. Hopefully SQLite logged a reason for this failure.");
         }
      } catch (Throwable var10) {
         MC_CLIENT.crashMinecraft(
            "Distant Horizons critical setup error: One or more libraries are either in-accessible, corrupted, or overwritten by another mod. Error: ["
               + var10.getMessage()
               + "].",
            var10
         );
      }

      try {
         int scriptCount = DatabaseUpdater.getAutoUpdateScriptCount();
         if (scriptCount == 0) {
            throw new NullPointerException("No auto update scripts found, but no error thrown. This might mean the script list file is corrupted or empty.");
         }
      } catch (Exception var8) {
         MC_CLIENT.crashMinecraft(
            "Critical programmer error: Can't read SQL Scripts resource folder is either missing or malformed. Error: [" + var8.getMessage() + "].", var8
         );
      }

      DhApi.Delayed.configs = DhApiConfig.INSTANCE;
      DhApi.Delayed.terrainRepo = DhApiTerrainDataRepo.INSTANCE;
      DhApi.Delayed.worldProxy = DhApiWorldProxy.INSTANCE;
      DhApi.Delayed.renderProxy = DhApiRenderProxy.INSTANCE;
      DhApi.Delayed.wrapperFactory = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
      if (DhApi.Delayed.wrapperFactory == null) {
         MC_CLIENT.crashMinecraft("Programmer Error: No [" + IWrapperFactory.class.getSimpleName() + "] assigned to the DhApi.", new Exception());
      }

      DhApi.Delayed.customRenderObjectFactory = SingletonInjector.INSTANCE.get(IDhApiCustomRenderObjectFactory.class);
      if (DhApi.Delayed.customRenderObjectFactory == null) {
         MC_CLIENT.crashMinecraft(
            "Programmer Error: No [" + IDhApiCustomRenderObjectFactory.class.getSimpleName() + "] assigned to the DhApi.", new Exception()
         );
      }

      DhApi.events.bind(DhApiBeforeRenderEvent.class, IgnoredDimensionCsvHandler.INSTANCE);
   }

   public static void postConfigInit() {
      boolean g1GcInUse = false;
      StringBuilder garbageCollectorNames = new StringBuilder();

      for (GarbageCollectorMXBean gcMxBean : ManagementFactory.getGarbageCollectorMXBeans()) {
         if (!garbageCollectorNames.toString().isEmpty()) {
            garbageCollectorNames.append(", ");
         }

         garbageCollectorNames.append(gcMxBean.getName());
         if (gcMxBean.getName().toLowerCase().contains("g1 ")) {
            g1GcInUse = true;
         }
      }

      LOGGER.info("Garbage collectors: [" + garbageCollectorNames + "]");
      if (g1GcInUse) {
         String warningMessageHeader = "Distant Horizons: G1 Garbage collector detected.";
         String warningMessageBody = "This can cause FPS stuttering. \nIt's recommended to use a concurrent garbage collector \nlike ZGC (Java 21+) or Shenandoah (Java 8 through 17) \nfor a smoother experience. \nThis warning can be disabled in the DH config.";
         if (com.seibel.distanthorizons.core.config.Config.Common.Logging.Warning.logGarbageCollectorWarning.get()) {
            LOGGER.warn(warningMessageHeader + "\n" + warningMessageBody + "");
         }

         if (com.seibel.distanthorizons.core.config.Config.Common.Logging.Warning.showGarbageCollectorWarning.get()) {
            ClientApi.INSTANCE.showChatMessageNextFrame("§6" + warningMessageHeader + "§r" + "\n" + warningMessageBody + "");
         }
      }

      g1GcInUse = false;
      RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

      for (String arg : runtimeMxBean.getInputArguments()) {
         if (arg.toLowerCase().contains("DisableExplicitGC".toLowerCase())) {
            g1GcInUse = true;
         }
      }

      LOGGER.info("Explicit Garbage Collection: [" + (g1GcInUse ? "Disabled" : "Enabled") + "]");
      if (g1GcInUse) {
         String warningMessageHeaderx = "Distant Horizons: Explicit Garbage Collection Disabled.";
         String warningMessageBodyx = "This can cause out of memory crashes. \nThe reason explicit GC would be disabled is to prevent \nstuttering, which is better fixed by using a concurrent \ngarbage collector like \nZGC (Java 21+) or Shenandoah (Java 8 through 17). \nThis warning can be disabled in the DH config.";
         if (com.seibel.distanthorizons.core.config.Config.Common.Logging.Warning.logExplicitGcDisabledWarning.get()) {
            LOGGER.warn(warningMessageHeaderx + "\n" + warningMessageBodyx + "");
         }

         if (com.seibel.distanthorizons.core.config.Config.Common.Logging.Warning.showExplicitGcDisabledWarning.get()) {
            ClientApi.INSTANCE.showChatMessageNextFrame("§6" + warningMessageHeaderx + "§r" + "\n" + warningMessageBodyx + "");
         }
      }
   }
}

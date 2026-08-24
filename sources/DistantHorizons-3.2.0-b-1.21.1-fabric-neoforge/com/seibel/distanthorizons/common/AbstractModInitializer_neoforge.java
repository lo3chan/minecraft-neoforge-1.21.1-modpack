package com.seibel.distanthorizons.common;

import com.mojang.brigadier.CommandDispatcher;
import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingEngine;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiTransparency;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterDhInitEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeDhInitEvent;
import com.seibel.distanthorizons.common.commands.CommandInitializer_neoforge;
import com.seibel.distanthorizons.common.wrappers.DependencySetup_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.NativeDialogUtil;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftClientWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftServerWrapper_neoforge;
import com.seibel.distanthorizons.core.Initializer;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.ConfigHandler;
import com.seibel.distanthorizons.core.config.eventHandlers.presets.ThreadPresetConfigEventHandler;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.ModJarInfo;
import com.seibel.distanthorizons.core.jar.updater.SelfUpdater;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.render.renderer.StubDebugWireframeRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.IVersionConstants;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModChecker;
import com.seibel.distanthorizons.coreapi.ModInfo;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

public abstract class AbstractModInitializer_neoforge {
   protected static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private CommandInitializer_neoforge commandInitializer;

   protected abstract void createInitialSharedBindings();

   protected abstract void createInitialClientBindings();

   protected abstract AbstractModInitializer$IEventProxy_neoforge createClientProxy();

   protected abstract AbstractModInitializer$IEventProxy_neoforge createServerProxy(boolean bl);

   protected abstract void initializeModCompat();

   protected abstract void subscribeRegisterCommandsEvent(Consumer<CommandDispatcher<CommandSourceStack>> consumer);

   protected abstract void subscribeClientStartedEvent(Runnable runnable);

   protected abstract void subscribeServerStartingEvent(Consumer<MinecraftServer> consumer);

   protected abstract void runDelayedSetup();

   public void onInitializeClient() {
      DependencySetup_neoforge.createClientBindings();
      this.createInitialClientBindings();
      LOGGER.info("Initializing Distant Horizons client, firing DhApiBeforeDhInitEvent...");
      ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeDhInitEvent.class, null);
      this.startup();
      this.logBuildInfo();
      this.createClientProxy().registerEvents();
      this.createServerProxy(false).registerEvents();
      this.initializeModCompat();
      this.initConfig();
      logIncompatibilityWarnings();
      setDisabledDhConfigBasedOnMods();
      setUnsupportedConfigsBasedOnMcVersion();
      Initializer.postConfigInit();
      LOGGER.info("Distant Horizons client Initialized.");
      this.subscribeClientStartedEvent(this::postInit);
      this.subscribeClientStartedEvent(this::postClientInit);
   }

   public void onInitializeServer() {
      DependencySetup_neoforge.createServerBindings();
      LOGGER.info("Initializing Distant Horizons server, firing DhApiBeforeDhInitEvent event...");
      ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeDhInitEvent.class, null);
      this.startup();
      this.logBuildInfo();
      ThreadPresetConfigEventHandler.INSTANCE.toString();
      this.createServerProxy(true).registerEvents();
      this.initializeModCompat();
      LOGGER.info("Distant Horizons server Initialized, adding event subscribers...");
      this.commandInitializer = new CommandInitializer_neoforge();
      this.subscribeRegisterCommandsEvent(dispatcher -> this.commandInitializer.initCommands(dispatcher));
      this.subscribeServerStartingEvent(server -> {
         MinecraftServerWrapper_neoforge.INSTANCE.dedicatedServer = (DedicatedServer)server;
         this.initConfig();
         Initializer.postConfigInit();
         this.postInit();
         this.postServerInit();
         this.commandInitializer.onServerReady();
         this.checkForUpdates();
         String serverFolderPath = server.getServerDirectory() + "";
         LOGGER.info("Distant Horizons server Initialized at " + serverFolderPath);
      });
   }

   private void startup() {
      DependencySetup_neoforge.createSharedBindings();
      Initializer.preConfigInit();
      this.createInitialSharedBindings();
   }

   private void logBuildInfo() {
      LOGGER.info("Distant Horizons, Version: 3.2.0-b");
      if (ModInfo.IS_DEV_BUILD) {
         LOGGER.info("DH Branch: " + ModJarInfo.Git_Branch);
         LOGGER.info("DH Commit: " + ModJarInfo.Git_Commit);
         LOGGER.info("DH Jar Build Source: " + ModJarInfo.Build_Source);
      }
   }

   protected <T extends IModAccessor> void tryCreateModCompatAccessor(String modId, Class<? super T> accessorClass, Supplier<T> accessorConstructor) {
      IModChecker modChecker = SingletonInjector.INSTANCE.get(IModChecker.class);
      if (modChecker.isModLoaded(modId)) {
         ModAccessorInjector.INSTANCE.bind((Class<? extends IModAccessor>)accessorClass, accessorConstructor.get());
      } else {
         LOGGER.debug("Skipping mod compatibility accessor for: [" + modId + "]");
      }
   }

   private void initConfig() {
      ConfigHandler.tryRunFirstTimeSetup();
      Config.completeDelayedSetup();
      DhLogger.runDelayedConfigSetup();
   }

   private void checkForUpdates() {
      if (Config.Client.Advanced.AutoUpdater.enableAutoUpdater.get()) {
         if (Config.Client.Advanced.AutoUpdater.enableSilentUpdates.get()) {
            LOGGER.info("Silent updates are not allowed for dedicated servers; force disabling.");
            Config.Client.Advanced.AutoUpdater.enableSilentUpdates.set(false);
         }

         SelfUpdater.onStart();
      }
   }

   private void postInit() {
      LOGGER.info("Running Delayed setup...");
      this.runDelayedSetup();
      if (ConfigHandler.INSTANCE == null) {
         throw new IllegalStateException("Config was not initialized. Make sure to call LodCommonMain.initConfig() before calling this method.");
      } else {
         LOGGER.info("Delayed setup complete, firing DhApiAfterDhInitEvent event...");
         ApiEventInjector.INSTANCE.fireAllEvents(DhApiAfterDhInitEvent.class, null);
      }
   }

   private void postClientInit() {
      CompletableFuture<Void> future = new CompletableFuture<>();
      Thread dhSetupThread = new Thread(() -> {
         try {
            DependencySetup_neoforge.setRenderingApiBindings();
         } catch (Exception var5) {
            NativeDialogUtil.showDialog("Distant Horizons", var5.getMessage(), "ok", "error");
            MinecraftClientWrapper_neoforge.INSTANCE.crashMinecraft(var5.getMessage(), var5);
            future.completeExceptionally(var5);
         } finally {
            future.complete(null);
         }
      });
      dhSetupThread.setName("DH-PostClientInit Thread");
      dhSetupThread.start();
      future.join();
   }

   private void postServerInit() {
      SingletonInjector.INSTANCE.bind(AbstractDebugWireframeRenderer.class, new StubDebugWireframeRenderer());
   }

   private static void logIncompatibilityWarnings() {
      boolean showChatWarnings = Config.Common.Logging.Warning.showModCompatibilityWarningsOnStartup.get();
      IModChecker modChecker = SingletonInjector.INSTANCE.get(IModChecker.class);
      IVersionConstants versionConstants = SingletonInjector.INSTANCE.get(IVersionConstants.class);
      String startingString = "Partially Incompatible Distant Horizons mod detected: ";
      if (modChecker.isModLoaded("alexscaves")) {
         if (showChatWarnings) {
            String message = "§6Distant Horizons: Alex's Cave detected.§rYou may have to change Alex's config for DH to render. ";
            ClientApi.INSTANCE.showChatMessageNextFrame(message);
         }

         LOGGER.warn(startingString + "[Alex's Caves] may require some config changes in order to render Distant Horizons correctly.");
      }

      if (modChecker.isModLoaded("wwoo")) {
         String wwooWarning = "LODs generated by DH may have grid lines between sections. Disabling either WWOO or DH's distant generator will fix the problem.";
         if (showChatWarnings) {
            String message = "§6Distant Horizons: WWOO detected.§r\n" + wwooWarning;
            ClientApi.INSTANCE.showChatMessageNextFrame(message);
         }

         LOGGER.warn(startingString + "[WWOO] " + wwooWarning);
      }

      boolean chunkyPresent = false;

      try {
         Class.forName("org.popcraft.chunky.api.ChunkyAPI");
         chunkyPresent = true;
      } catch (ClassNotFoundException var11) {
      }

      if (chunkyPresent) {
         String chunkyWarning = "Chunky can cause DH LODs to have holes since Chunky can generate chunks faster than DH can process them. \nUsing DH's distant generator instead of chunky or increasing DH's CPU thread count can resolve the issue.";
         if (showChatWarnings) {
            String message = "§6Distant Horizons: Chunky detected.§r\n" + chunkyWarning;
            ClientApi.INSTANCE.showChatMessageNextFrame(message);
         }

         LOGGER.warn(startingString + "[Chunky] " + chunkyWarning);
      }

      IIrisAccessor iris = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
      if (iris != null) {
         EDhApiRenderingEngine renderEngine = Config.Client.Advanced.Graphics.Experimental.renderingEngine.get();
         if (renderEngine == EDhApiRenderingEngine.BLAZE_3D) {
            String irisUnsupportedMessage = "Iris doesn't support DH when using the ["
               + EDhApiRenderingEngine.BLAZE_3D
               + "] rendering engine, this will need to be fixed on Iris end. As a temporary fix please change the rendering engine to ["
               + EDhApiRenderingEngine.OPEN_GL
               + "] or ["
               + EDhApiRenderingEngine.AUTO
               + "] in the DH config file.";
            LOGGER.fatal(irisUnsupportedMessage);
            NativeDialogUtil.showDialog("Distant Horizons", irisUnsupportedMessage, "ok", "error");
            IMinecraftClientWrapper mc = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
            String errorMessage = "loading Distant Horizons. " + irisUnsupportedMessage;
            String exceptionError = "Distant Horizons conditional mod config Exception";
            mc.crashMinecraft(errorMessage, new Exception(exceptionError));
         } else if (renderEngine == EDhApiRenderingEngine.AUTO) {
            Config.Client.Advanced.Graphics.Experimental.renderingEngine.setApiValue(EDhApiRenderingEngine.OPEN_GL);
            EDhApiRenderingEngine recommendedEngine = versionConstants.getDefaultRenderingEngine();
            if (recommendedEngine != EDhApiRenderingEngine.OPEN_GL) {
               LOGGER.warn(
                  "Changing Distant Horizons' rendering engine to ["
                     + EDhApiRenderingEngine.OPEN_GL
                     + "] to allow for Iris rendering. This renderer will be unavailable once Minecraft moves to Vulkan and must be fixed on Iris' end."
               );
            }
         }
      }
   }

   private static void setUnsupportedConfigsBasedOnMcVersion() {
      Config.Client.Advanced.Graphics.Experimental.renderingEngine.setMcVersionOverrideValue(EDhApiRenderingEngine.OPEN_GL);
   }

   private static void setDisabledDhConfigBasedOnMods() {
      IIrisAccessor irisAccessor = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
      if (irisAccessor != null) {
         Config.Client.Advanced.Graphics.Quality.transparency.setApiValue(EDhApiTransparency.COMPLETE);
      }
   }
}

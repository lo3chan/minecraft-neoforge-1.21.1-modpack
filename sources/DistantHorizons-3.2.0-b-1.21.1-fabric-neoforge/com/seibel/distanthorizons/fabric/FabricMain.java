package com.seibel.distanthorizons.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.seibel.distanthorizons.common.AbstractModInitializer$IEventProxy_fabric;
import com.seibel.distanthorizons.common.AbstractModInitializer_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.NativeDialogUtil;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IPluginPacketSender;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IBCLibAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IC2meAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IImmersivePortalsAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModChecker;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IOptifineAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.ISodiumAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IStarlightAccessor;
import com.seibel.distanthorizons.fabric.wrappers.modAccessor.BCLibAccessor;
import com.seibel.distanthorizons.fabric.wrappers.modAccessor.C2meAccessor;
import com.seibel.distanthorizons.fabric.wrappers.modAccessor.ImmersivePortalsAccessorFabric;
import com.seibel.distanthorizons.fabric.wrappers.modAccessor.IrisAccessor;
import com.seibel.distanthorizons.fabric.wrappers.modAccessor.ModChecker;
import com.seibel.distanthorizons.fabric.wrappers.modAccessor.OptifineAccessor;
import com.seibel.distanthorizons.fabric.wrappers.modAccessor.SodiumAccessor;
import com.seibel.distanthorizons.fabric.wrappers.modAccessor.StarlightAccessor;
import java.util.function.Consumer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStarted;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_2168;
import net.minecraft.class_2960;
import net.minecraft.server.MinecraftServer;

public class FabricMain extends AbstractModInitializer_fabric implements ClientModInitializer, DedicatedServerModInitializer {
   private static final class_2960 INITIAL_PHASE = class_2960.method_60655("distant_horizons", "dedicated_server_initial");
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   @Override
   protected void createInitialSharedBindings() {
      SingletonInjector.INSTANCE.bind(IModChecker.class, ModChecker.INSTANCE);
      SingletonInjector.INSTANCE.bind(IPluginPacketSender.class, new FabricPluginPacketSender());
   }

   @Override
   protected void createInitialClientBindings() {
   }

   @Override
   protected AbstractModInitializer$IEventProxy_fabric createClientProxy() {
      return new FabricClientProxy();
   }

   @Override
   protected AbstractModInitializer$IEventProxy_fabric createServerProxy(boolean isDedicated) {
      return new FabricServerProxy(isDedicated);
   }

   @Override
   protected void initializeModCompat() {
      IModChecker modChecker = SingletonInjector.INSTANCE.get(IModChecker.class);
      if (modChecker.isModLoaded("sodium")) {
         ModAccessorInjector.INSTANCE.bind(ISodiumAccessor.class, (IModAccessor)(new SodiumAccessor()));
         if (!modChecker.isModLoaded("indium") && SodiumAccessor.isSodiumV5OrLess) {
            String indiumMissingMessage = "Distant Horizons needs Indium to work with Sodium.\nPlease download Indium from https://modrinth.com/mod/indium";
            LOGGER.fatal(indiumMissingMessage);
            NativeDialogUtil.showDialog("Distant Horizons", indiumMissingMessage, "ok", "error");
            IMinecraftClientWrapper mc = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
            String errorMessage = "loading Distant Horizons. Distant Horizons requires Indium in order to run with Sodium.";
            String exceptionError = "Distant Horizons conditional mod Exception";
            mc.crashMinecraft(errorMessage, new Exception(exceptionError));
         }
      }

      this.tryCreateModCompatAccessor("starlight", IStarlightAccessor.class, StarlightAccessor::new);
      this.tryCreateModCompatAccessor("optifine", IOptifineAccessor.class, OptifineAccessor::new);
      this.tryCreateModCompatAccessor("bclib", IBCLibAccessor.class, BCLibAccessor::new);
      this.tryCreateModCompatAccessor("c2me", IC2meAccessor.class, C2meAccessor::new);
      this.tryCreateModCompatAccessor("imm_ptl_core", IImmersivePortalsAccessor.class, ImmersivePortalsAccessorFabric::new);
      this.tryCreateModCompatAccessor("iris", IIrisAccessor.class, IrisAccessor::new);
   }

   @Override
   protected void subscribeRegisterCommandsEvent(Consumer<CommandDispatcher<class_2168>> eventHandler) {
      CommandRegistrationCallback.EVENT.register((CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> eventHandler.accept(dispatcher));
   }

   @Override
   protected void subscribeClientStartedEvent(Runnable eventHandler) {
      ClientLifecycleEvents.CLIENT_STARTED.register((ClientStarted)mc -> eventHandler.run());
   }

   @Override
   protected void subscribeServerStartingEvent(Consumer<MinecraftServer> eventHandler) {
      ServerLifecycleEvents.SERVER_STARTING.addPhaseOrdering(INITIAL_PHASE, Event.DEFAULT_PHASE);
      ServerLifecycleEvents.SERVER_STARTING.register(INITIAL_PHASE, eventHandler::accept);
   }

   @Override
   protected void runDelayedSetup() {
      SingletonInjector.INSTANCE.runDelayedSetup();
      if (!Config.Client.Advanced.Graphics.Fog.enableVanillaFog.get() && SingletonInjector.INSTANCE.get(IModChecker.class).isModLoaded("bclib")) {
         ModAccessorInjector.INSTANCE.get(IBCLibAccessor.class).setRenderCustomFog(false);
      }
   }
}

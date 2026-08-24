package com.seibel.distanthorizons.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import com.seibel.distanthorizons.common.AbstractModInitializer$IEventProxy_neoforge;
import com.seibel.distanthorizons.common.AbstractModInitializer_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.GetConfigScreen_neoforge;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.api.internal.ServerApi;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IPluginPacketSender;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IC2meAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IImmersivePortalsAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModChecker;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IOptifineAccessor;
import com.seibel.distanthorizons.neoforge.wrappers.NeoforgeMinecraftRenderWrapper;
import com.seibel.distanthorizons.neoforge.wrappers.modAccessor.C2meAccessor;
import com.seibel.distanthorizons.neoforge.wrappers.modAccessor.ImmersivePortalsAccessorNeoForge;
import com.seibel.distanthorizons.neoforge.wrappers.modAccessor.IrisAccessor;
import com.seibel.distanthorizons.neoforge.wrappers.modAccessor.ModChecker;
import com.seibel.distanthorizons.neoforge.wrappers.modAccessor.OptifineAccessor;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod("distanthorizons")
public class NeoforgeMain extends AbstractModInitializer_neoforge {
   public NeoforgeMain(IEventBus eventBus) {
      eventBus.addListener(e -> {
         this.onInitializeClient();
         eventBus.addListener(this::registerNetworkingClientServer);
      });
      eventBus.addListener(e -> {
         this.onInitializeServer();
         eventBus.addListener(this::registerNetworkingServer);
      });
   }

   public void registerNetworkingClientServer(RegisterPayloadHandlersEvent event) {
      NeoforgePluginPacketSender.setPacketHandler(event, (player, message) -> {
         ClientApi.INSTANCE.pluginMessageReceived(message);
         ServerApi.INSTANCE.pluginMessageReceived(player, message);
      });
   }

   public void registerNetworkingServer(RegisterPayloadHandlersEvent event) {
      NeoforgePluginPacketSender.setPacketHandler(event, ServerApi.INSTANCE::pluginMessageReceived);
   }

   @Override
   protected AbstractModInitializer$IEventProxy_neoforge createServerProxy(boolean isDedicated) {
      return new NeoforgeServerProxy(isDedicated);
   }

   @Override
   protected void createInitialSharedBindings() {
      SingletonInjector.INSTANCE.bind(IModChecker.class, ModChecker.INSTANCE);
      SingletonInjector.INSTANCE.bind(IPluginPacketSender.class, new NeoforgePluginPacketSender());
   }

   @Override
   protected void createInitialClientBindings() {
      SingletonInjector.INSTANCE.replaceBinding(IMinecraftRenderWrapper.class, NeoforgeMinecraftRenderWrapper.INSTANCE);
   }

   @Override
   protected AbstractModInitializer$IEventProxy_neoforge createClientProxy() {
      return new NeoforgeClientProxy();
   }

   @Override
   protected void initializeModCompat() {
      this.tryCreateModCompatAccessor("optifine", IOptifineAccessor.class, OptifineAccessor::new);
      this.tryCreateModCompatAccessor("c2me", IC2meAccessor.class, C2meAccessor::new);
      this.tryCreateModCompatAccessor("immersive_portals", IImmersivePortalsAccessor.class, ImmersivePortalsAccessorNeoForge::new);
      this.tryCreateModCompatAccessor("iris", IIrisAccessor.class, IrisAccessor::new);
      ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> GetConfigScreen_neoforge.getScreen(parent));
   }

   @Override
   protected void subscribeRegisterCommandsEvent(Consumer<CommandDispatcher<CommandSourceStack>> eventHandler) {
      NeoForge.EVENT_BUS.addListener(e -> eventHandler.accept(e.getDispatcher()));
   }

   @Override
   protected void subscribeClientStartedEvent(Runnable eventHandler) {
      eventHandler.run();
   }

   @Override
   protected void subscribeServerStartingEvent(Consumer<MinecraftServer> eventHandler) {
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, e -> eventHandler.accept(e.getServer()));
   }

   @Override
   protected void runDelayedSetup() {
      SingletonInjector.INSTANCE.runDelayedSetup();
   }
}

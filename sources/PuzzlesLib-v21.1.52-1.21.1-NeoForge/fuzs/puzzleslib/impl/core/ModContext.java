package fuzs.puzzleslib.impl.core;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class ModContext {
   private static final Map<String, ModContext> MOD_CONTEXTS = Maps.newConcurrentMap();
   private final String modId;
   protected final Type<BrandPayload> payloadType;
   @Nullable
   private NetworkHandlerRegistryImpl networkHandler;
   @Nullable
   private ConfigHolderImpl configHolder;
   @Nullable
   private RegistryManagerImpl registryManager;
   @Nullable
   protected CapabilityController capabilityController;

   protected ModContext(String modId) {
      this.modId = modId;
      this.payloadType = new Type(ResourceLocation.fromNamespaceAndPath(modId, "handshake"));
   }

   public static void forEach(Consumer<ModContext> modContextConsumer) {
      MOD_CONTEXTS.values().forEach(modContextConsumer);
   }

   public static Map<String, ModContext> getModContexts() {
      return Collections.unmodifiableMap(MOD_CONTEXTS);
   }

   public static ModContext get(String modId) {
      return MOD_CONTEXTS.computeIfAbsent(modId, ProxyImpl.get()::getModContext);
   }

   public abstract boolean isPresentServerside();

   public abstract boolean isPresentClientside(ServerPlayer var1);

   public final NetworkHandler.Builder getNetworkHandler() {
      return this.networkHandler == null ? (this.networkHandler = this.createNetworkHandler(this.modId)) : this.networkHandler;
   }

   protected abstract NetworkHandlerRegistryImpl createNetworkHandler(String var1);

   public final ConfigHolder.Builder getConfigHolder() {
      return this.configHolder == null ? (this.configHolder = this.createConfigHolder(this.modId)) : this.configHolder;
   }

   protected abstract ConfigHolderImpl createConfigHolder(String var1);

   public final RegistryManager getRegistryManager() {
      return this.registryManager == null ? (this.registryManager = this.createRegistryManager(this.modId)) : this.registryManager;
   }

   protected abstract RegistryManagerImpl createRegistryManager(String var1);

   public final CapabilityController getCapabilityController() {
      return this.capabilityController == null ? (this.capabilityController = this.createCapabilityController(this.modId)) : this.capabilityController;
   }

   protected abstract CapabilityController createCapabilityController(String var1);

   public final void runBeforeConstruction() {
      if (this.networkHandler != null) {
         this.networkHandler.freeze();
      }

      if (this.configHolder != null) {
         this.configHolder.freeze();
      }
   }

   public final void runAfterConstruction() {
      if (this.networkHandler != null) {
      }

      if (this.configHolder != null) {
         this.configHolder.isFrozenOrThrow();
      }

      if (this.registryManager != null) {
         this.registryManager.freeze();
         this.registryManager.isFrozenOrThrow();
      }
   }
}

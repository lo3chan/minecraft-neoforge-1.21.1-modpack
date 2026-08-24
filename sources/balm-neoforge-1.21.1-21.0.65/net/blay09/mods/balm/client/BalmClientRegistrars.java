package net.blay09.mods.balm.client;

import java.util.function.Consumer;
import net.blay09.mods.balm.api.client.BalmClientRuntime;
import net.blay09.mods.balm.api.client.module.BalmClientModule;
import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.gui.screens.inventory.BalmMenuScreenRegistrar;
import net.blay09.mods.balm.client.model.geom.BalmModelLayerRegistrar;
import net.blay09.mods.balm.client.model.item.BalmItemPropertyRegistrar;
import net.blay09.mods.balm.client.particle.BalmParticleProviderRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.balm.client.renderer.chunk.BalmBlockRenderTypeRegistrar;
import net.blay09.mods.balm.client.renderer.entity.BalmEntityRendererRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmClientResourceReloadListenerRegistrar;

public class BalmClientRegistrars {
   private final BalmClientRuntime<?> runtime;
   private final String namespace;

   public BalmClientRegistrars(BalmClientRuntime<?> runtime, String namespace) {
      this.runtime = runtime;
      this.namespace = namespace;
   }

   public void menuScreens(Consumer<BalmMenuScreenRegistrar> initializer) {
      this.runtime.menuScreens(this.namespace, initializer);
   }

   public void blockEntityRenderers(Consumer<BalmBlockEntityRendererRegistrar> initializer) {
      this.runtime.blockEntityRenderers(this.namespace, initializer);
   }

   public void entityRenderers(Consumer<BalmEntityRendererRegistrar> initializer) {
      this.runtime.entityRenderers(this.namespace, initializer);
   }

   public void blockStateModels(Consumer<BalmBlockStateModelRegistrar> initializer) {
      this.runtime.blockStateModels(this.namespace, initializer);
   }

   public void modelLayers(Consumer<BalmModelLayerRegistrar> initializer) {
      this.runtime.modelLayers(this.namespace, initializer);
   }

   public void itemProperties(Consumer<BalmItemPropertyRegistrar> initializer) {
      this.runtime.itemProperties(this.namespace, initializer);
   }

   public void blockColors(Consumer<BalmBlockColorRegistrar> initializer) {
      this.runtime.blockColors(this.namespace, initializer);
   }

   public void particleProviders(Consumer<BalmParticleProviderRegistrar> initializer) {
      this.runtime.particleProviders(this.namespace, initializer);
   }

   public void blockRenderTypes(Consumer<BalmBlockRenderTypeRegistrar> initializer) {
      this.runtime.blockRenderTypes(this.namespace, initializer);
   }

   public void keyMappings(Consumer<BalmKeyMappingRegistrar> initializer) {
      this.runtime.keyMappings(this.namespace, initializer);
   }

   public void resourceReloadListeners(Consumer<BalmClientResourceReloadListenerRegistrar> initializer) {
      this.runtime.resourceReloadListeners(this.namespace, initializer);
   }

   public void clientTooltipComponents(Consumer<BalmClientTooltipComponentRegistrar> initializer) {
      this.runtime.clientTooltipComponents(this.namespace, initializer);
   }

   public void registerModule(BalmClientModule module) {
      this.runtime.registerModule(this, module);
   }
}

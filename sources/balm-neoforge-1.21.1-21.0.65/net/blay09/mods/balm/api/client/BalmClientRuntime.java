package net.blay09.mods.balm.api.client;

import java.util.function.Consumer;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmRuntimeLoadContext;
import net.blay09.mods.balm.api.client.commands.BalmClientCommands;
import net.blay09.mods.balm.api.client.keymappings.BalmKeyMappings;
import net.blay09.mods.balm.api.client.module.BalmClientModule;
import net.blay09.mods.balm.api.client.rendering.BalmModels;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.balm.api.client.rendering.BalmTextures;
import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.BalmClientTooltipComponentRegistrar;
import net.blay09.mods.balm.client.BalmKeyMappingRegistrar;
import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.color.item.BalmItemColorRegistrar;
import net.blay09.mods.balm.client.gui.screens.inventory.BalmMenuScreenRegistrar;
import net.blay09.mods.balm.client.model.geom.BalmModelLayerRegistrar;
import net.blay09.mods.balm.client.model.item.BalmItemPropertyRegistrar;
import net.blay09.mods.balm.client.particle.BalmParticleProviderRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.balm.client.renderer.chunk.BalmBlockRenderTypeRegistrar;
import net.blay09.mods.balm.client.renderer.entity.BalmEntityRendererRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmClientResourceReloadListenerRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface BalmClientRuntime<TLoadContext extends BalmRuntimeLoadContext> {
   BalmRenderers getRenderers();

   @Deprecated
   BalmScreens getScreens();

   BalmModels getModels();

   @Deprecated
   BalmKeyMappings getKeyMappings();

   @Deprecated
   default void initializeMod(String modId, TLoadContext context, Runnable initializer) {
      this.initializeMod(modId, context, registrars -> initializer.run());
   }

   void initializeMod(String var1, TLoadContext var2, Consumer<BalmClientRegistrars> var3);

   default void initializeModule(BalmClientModule module) {
      String modId = module.getId().getNamespace();
      module.registerEvents(Balm.getEvents());
      module.registerRenderers(this.getRenderers().scoped(modId));
      module.registerScreens(this.getScreens().scoped(modId));
      this.menuScreens(modId, module::registerMenuScreens);
      module.registerModels(this.getModels().scoped(modId));
      module.registerKeyMappings(this.getKeyMappings().scoped(modId));
      this.keyMappings(modId, module::registerKeyMappings);
      this.clientTooltipComponents(modId, module::registerClientTooltipComponents);
      this.resourceReloadListeners(modId, module::registerClientReloadListeners);
      this.blockColors(modId, module::registerBlockColors);
      this.itemColors(modId, module::registerItemColors);
      this.itemProperties(modId, module::registerItemProperties);
      this.blockRenderTypes(modId, module::registerBlockRenderTypes);
      this.blockEntityRenderers(modId, module::registerBlockEntityRenderers);
      this.entityRenderers(modId, module::registerEntityRenderers);
      this.particleProviders(modId, module::registerParticleProviders);
      this.modelLayers(modId, module::registerModelLayers);
      this.blockStateModels(modId, module::registerBlockStateModels);
      module.registerClientCommands(this.clientCommands());
      module.initialize();
   }

   boolean isReady();

   void onRuntimeAvailable(Runnable var1);

   @Deprecated
   default void registerModule(BalmClientModule module) {
      this.registerModule(new BalmClientRegistrars(this, module.getId().getNamespace()), module);
   }

   BalmClientCommands clientCommands();

   void registerModule(BalmClientRegistrars var1, BalmClientModule var2);

   @Deprecated
   void addResourceReloadListener(ResourceLocation var1, PreparableReloadListener var2);

   @Deprecated
   BalmTextures getTextures();

   void blockEntityRenderers(String var1, Consumer<BalmBlockEntityRendererRegistrar> var2);

   void blockStateModels(String var1, Consumer<BalmBlockStateModelRegistrar> var2);

   void entityRenderers(String var1, Consumer<BalmEntityRendererRegistrar> var2);

   void menuScreens(String var1, Consumer<BalmMenuScreenRegistrar> var2);

   void keyMappings(String var1, Consumer<BalmKeyMappingRegistrar> var2);

   void modelLayers(String var1, Consumer<BalmModelLayerRegistrar> var2);

   void blockColors(String var1, Consumer<BalmBlockColorRegistrar> var2);

   void itemColors(String var1, Consumer<BalmItemColorRegistrar> var2);

   void itemProperties(String var1, Consumer<BalmItemPropertyRegistrar> var2);

   void particleProviders(String var1, Consumer<BalmParticleProviderRegistrar> var2);

   void blockRenderTypes(String var1, Consumer<BalmBlockRenderTypeRegistrar> var2);

   void resourceReloadListeners(String var1, Consumer<BalmClientResourceReloadListenerRegistrar> var2);

   void clientTooltipComponents(String var1, Consumer<BalmClientTooltipComponentRegistrar> var2);
}

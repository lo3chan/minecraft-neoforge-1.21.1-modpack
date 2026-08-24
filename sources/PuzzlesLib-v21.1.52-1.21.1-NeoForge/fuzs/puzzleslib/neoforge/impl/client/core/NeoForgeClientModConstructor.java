package fuzs.puzzleslib.neoforge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.client.core.context.BlockRenderTypesContextImpl;
import fuzs.puzzleslib.impl.client.core.context.FluidRenderTypesContextImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.core.context.AdditionalModelsContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.BlockColorProvidersContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.BlockEntityRenderersContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.BuiltinModelItemRendererContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.ClientTooltipComponentsContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.CoreShadersContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.EntityRenderersContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.EntitySpectatorShaderContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.ItemColorProvidersContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.ItemDecorationContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.ItemModelPropertiesContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.KeyMappingsContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.LayerDefinitionsContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.LivingEntityRenderLayersContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.MenuScreensContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.ParticleProvidersContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.RenderBuffersContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.ResourcePackSourcesContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.client.core.context.SkullRenderersContextNeoForgeImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class NeoForgeClientModConstructor implements ModConstructorImpl<ClientModConstructor> {
   public void construct(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> contentRegistrationFlags) {
      NeoForgeModContainerHelper.getOptionalModEventBus(modId)
         .ifPresent(
            eventBus -> {
               modConstructor.onConstructMod();
               List<ResourceManagerReloadListener> dynamicRenderers = new ArrayList<>();
               eventBus.addListener(event -> event.enqueueWork(() -> {
                  modConstructor.onClientSetup();
                  modConstructor.onRegisterItemModelProperties(new ItemModelPropertiesContextNeoForgeImpl());
                  modConstructor.onRegisterBlockRenderTypes(new BlockRenderTypesContextImpl());
                  modConstructor.onRegisterFluidRenderTypes(new FluidRenderTypesContextImpl());
               }));
               eventBus.addListener(event -> modConstructor.onRegisterMenuScreens(new MenuScreensContextNeoForgeImpl(event)));
               eventBus.addListener(event -> {
                  modConstructor.onRegisterEntityRenderers(new EntityRenderersContextNeoForgeImpl(event::registerEntityRenderer));
                  modConstructor.onRegisterBlockEntityRenderers(new BlockEntityRenderersContextNeoForgeImpl(event::registerBlockEntityRenderer));
               });
               eventBus.addListener(event -> modConstructor.onRegisterClientTooltipComponents(new ClientTooltipComponentsContextNeoForgeImpl(event::register)));
               eventBus.addListener(event -> modConstructor.onRegisterParticleProviders(new ParticleProvidersContextNeoForgeImpl(event)));
               eventBus.addListener(event -> modConstructor.onRegisterLayerDefinitions(new LayerDefinitionsContextNeoForgeImpl(event::registerLayerDefinition)));
               eventBus.addListener(event -> modConstructor.onRegisterAdditionalModels(new AdditionalModelsContextNeoForgeImpl(event::register)));
               eventBus.addListener(event -> modConstructor.onRegisterItemDecorations(new ItemDecorationContextNeoForgeImpl(event::register)));
               eventBus.addListener(event -> modConstructor.onRegisterEntitySpectatorShaders(new EntitySpectatorShaderContextNeoForgeImpl(event::register)));
               eventBus.addListener(event -> modConstructor.onRegisterSkullRenderers(new SkullRenderersContextNeoForgeImpl(event)));
               eventBus.addListener(
                  event -> {
                     if (contentRegistrationFlags.contains(ContentRegistrationFlags.DYNAMIC_RENDERERS)) {
                        event.registerReloadListener(
                           ForwardingReloadListenerHelper.fromResourceManagerReloadListeners(
                              ResourceLocationHelper.fromNamespaceAndPath(modId, "built_in_model_item_renderers"), dynamicRenderers
                           )
                        );
                     }
                  }
               );
               eventBus.addListener(event -> modConstructor.onRegisterLivingEntityRenderLayers(new LivingEntityRenderLayersContextNeoForgeImpl(event)));
               eventBus.addListener(event -> modConstructor.onRegisterKeyMappings(new KeyMappingsContextNeoForgeImpl(event::register)));
               eventBus.addListener(
                  event -> modConstructor.onRegisterBlockColorProviders(
                     new BlockColorProvidersContextNeoForgeImpl((x$0, xva$1) -> event.register(x$0, new Block[]{xva$1}), event.getBlockColors())
                  )
               );
               eventBus.addListener(
                  event -> modConstructor.onRegisterItemColorProviders(
                     new ItemColorProvidersContextNeoForgeImpl((x$0, xva$1) -> event.register(x$0, new ItemLike[]{xva$1}), event.getItemColors())
                  )
               );
               eventBus.addListener(event -> {
                  if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                     modConstructor.onAddResourcePackFinders(new ResourcePackSourcesContextNeoForgeImpl(event));
                  }
               });
               eventBus.addListener(
                  event -> modConstructor.onRegisterCoreShaders(new CoreShadersContextNeoForgeImpl(event::registerShader, event.getResourceProvider()))
               );
               eventBus.addListener(event -> modConstructor.onRegisterRenderBuffers(new RenderBuffersContextNeoForgeImpl(event::registerRenderBuffer)));
               eventBus.addListener(
                  event -> modConstructor.onRegisterBuiltinModelItemRenderers(
                     new BuiltinModelItemRendererContextNeoForgeImpl((x$0, xva$1) -> event.registerItem(x$0, new Item[]{xva$1}), modId, dynamicRenderers)
                  )
               );
            }
         );
   }
}

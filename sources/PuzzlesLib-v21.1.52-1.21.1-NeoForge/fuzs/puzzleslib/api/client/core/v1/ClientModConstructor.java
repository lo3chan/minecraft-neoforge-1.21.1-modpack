package fuzs.puzzleslib.api.client.core.v1;

import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.core.v1.context.ClientTooltipComponentsContext;
import fuzs.puzzleslib.api.client.core.v1.context.ColorProvidersContext;
import fuzs.puzzleslib.api.client.core.v1.context.CoreShadersContext;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.EntitySpectatorShaderContext;
import fuzs.puzzleslib.api.client.core.v1.context.ItemDecorationContext;
import fuzs.puzzleslib.api.client.core.v1.context.ItemModelPropertiesContext;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.puzzleslib.api.client.core.v1.context.LivingEntityRenderLayersContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.core.v1.context.ParticleProvidersContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderBuffersContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.core.v1.context.SkullRenderersContext;
import fuzs.puzzleslib.api.core.v1.BaseModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import java.util.function.Supplier;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public interface ClientModConstructor extends BaseModConstructor {
   static void construct(String modId, Supplier<ClientModConstructor> modConstructorSupplier) {
      construct(ResourceLocation.fromNamespaceAndPath(modId, "client"), modConstructorSupplier);
   }

   static void construct(ResourceLocation resourceLocation, Supplier<ClientModConstructor> modConstructorSupplier) {
      ModConstructorImpl.construct(resourceLocation, modConstructorSupplier, ClientProxyImpl.get()::getClientModConstructorImpl);
   }

   default void onConstructMod() {
   }

   default void onClientSetup() {
   }

   default void onRegisterEntityRenderers(EntityRenderersContext context) {
   }

   default void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
   }

   default void onRegisterClientTooltipComponents(ClientTooltipComponentsContext context) {
   }

   default void onRegisterParticleProviders(ParticleProvidersContext context) {
   }

   default void onRegisterMenuScreens(MenuScreensContext context) {
   }

   default void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
   }

   default void onRegisterAdditionalModels(AdditionalModelsContext context) {
   }

   default void onRegisterItemModelProperties(ItemModelPropertiesContext context) {
   }

   default void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
   }

   default void onRegisterItemDecorations(ItemDecorationContext context) {
   }

   default void onRegisterEntitySpectatorShaders(EntitySpectatorShaderContext context) {
   }

   default void onRegisterSkullRenderers(SkullRenderersContext context) {
   }

   @Deprecated
   default void onRegisterLivingEntityRenderLayers(LivingEntityRenderLayersContext context) {
   }

   default void onRegisterKeyMappings(KeyMappingsContext context) {
   }

   default void onRegisterBlockRenderTypes(RenderTypesContext<Block> context) {
   }

   default void onRegisterFluidRenderTypes(RenderTypesContext<Fluid> context) {
   }

   default void onRegisterBlockColorProviders(ColorProvidersContext<Block, BlockColor> context) {
   }

   default void onRegisterItemColorProviders(ColorProvidersContext<Item, ItemColor> context) {
   }

   default void onAddResourcePackFinders(PackRepositorySourcesContext context) {
   }

   default void onRegisterCoreShaders(CoreShadersContext context) {
   }

   default void onRegisterRenderBuffers(RenderBuffersContext context) {
   }
}

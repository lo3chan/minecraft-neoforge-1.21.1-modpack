package fuzs.visualworkbench.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext.Factory;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.resources.v1.DynamicPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.client.handler.BlockModelHandler;
import fuzs.visualworkbench.client.renderer.blockentity.CraftingTableBlockEntityRenderer;
import fuzs.visualworkbench.data.client.DynamicModelProvider;
import fuzs.visualworkbench.init.ModRegistry;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class VisualWorkbenchClient implements ClientModConstructor {
   public void onConstructMod() {
      registerHandlers();
   }

   private static void registerHandlers() {
      ModelEvents.MODIFY_UNBAKED_MODEL.register(BlockModelHandler::onModifyUnbakedModel);
      LoadCompleteCallback.EVENT.register(BlockModelHandler::onLoadComplete);
   }

   public void onRegisterMenuScreens(MenuScreensContext context) {
      context.registerMenuScreen((MenuType)ModRegistry.CRAFTING_MENU_TYPE.value(), CraftingScreen::new);
   }

   public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
      context.registerBlockEntityRenderer((BlockEntityType)ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY_TYPE.value(), CraftingTableBlockEntityRenderer::new);
   }

   public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
      context.addRepositorySource(
         new RepositorySource[]{
            PackResourcesHelper.buildClientPack(
               VisualWorkbench.id("default_block_models"), DynamicPackResources.create(new Factory[]{DynamicModelProvider::new}), true
            )
         }
      );
   }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.event.RecipesUpdatedEvent
 *  net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
 *  net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent
 *  net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
 *  net.neoforged.neoforge.event.GameShuttingDownEvent
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package mezz.jei.neoforge;

import java.util.List;
import java.util.function.Function;
import mezz.jei.api.IModPlugin;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.IngredientTooltipComponent;
import mezz.jei.common.gui.IngredientsTooltipComponent;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.gui.config.InternalKeyMappings;
import mezz.jei.gui.overlay.bookmarks.PreviewTooltipComponent;
import mezz.jei.library.gui.ingredients.TagContentTooltipComponent;
import mezz.jei.library.plugins.vanilla.crafting.JeiShapedRecipe;
import mezz.jei.library.recipes.RecipeSerializers;
import mezz.jei.library.startup.JeiStarter;
import mezz.jei.library.startup.StartData;
import mezz.jei.neoforge.chat.JeiChatEventHandler;
import mezz.jei.neoforge.chat.JeiChatTooltipEventHandler;
import mezz.jei.neoforge.chat.JeiInternalShowCommand;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import mezz.jei.neoforge.network.NetworkHandler;
import mezz.jei.neoforge.plugins.neoforge.NeoForgeGuiPlugin;
import mezz.jei.neoforge.startup.ForgePluginFinder;
import mezz.jei.neoforge.startup.StartEventObserver;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class JustEnoughItemsClient {
    private final PermanentEventSubscriptions subscriptions;
    private final JeiStarter jeiStarter;

    public JustEnoughItemsClient(NetworkHandler networkHandler, PermanentEventSubscriptions subscriptions) {
        this.subscriptions = subscriptions;
        IConnectionToServer serverConnection = networkHandler.getConnectionToServer();
        List<IModPlugin> plugins = ForgePluginFinder.getModPlugins();
        StartData startData = new StartData(plugins, serverConnection);
        this.jeiStarter = new JeiStarter(startData);
        StartEventObserver startEventObserver = new StartEventObserver(this.jeiStarter::start, this.jeiStarter::stop);
        startEventObserver.register(subscriptions);
    }

    public void register() {
        this.subscriptions.register(RegisterClientReloadListenersEvent.class, this::onRegisterReloadListenerEvent);
        this.subscriptions.register(RegisterClientTooltipComponentFactoriesEvent.class, this::onRegisterClientTooltipEvent);
        this.subscriptions.register(RecipesUpdatedEvent.class, this::onRecipesUpdatedEvent);
        this.subscriptions.register(GameShuttingDownEvent.class, e -> this.onGameShuttingDown());
        this.subscriptions.register(RegisterKeyMappingsEvent.class, e -> {
            InternalKeyMappings keyMappings = new InternalKeyMappings(arg_0 -> ((RegisterKeyMappingsEvent)e).register(arg_0));
            Internal.setKeyMappings(keyMappings);
        });
        JeiChatEventHandler.register(this.subscriptions);
        JeiChatTooltipEventHandler.register(this.subscriptions);
        JeiInternalShowCommand.register(this.subscriptions);
        IEventBus modEventBus = this.subscriptions.getModEventBus();
        DeferredRegister deferredRegister = DeferredRegister.create((Registry)BuiltInRegistries.RECIPE_SERIALIZER, (String)"jei");
        deferredRegister.register(modEventBus);
        DeferredHolder jeiShaped = deferredRegister.register("jei_shaped", JeiShapedRecipe.Serializer::new);
        RecipeSerializers.register(jeiShaped);
    }

    private void onGameShuttingDown() {
        this.jeiStarter.stop();
        Internal.onClientStopping();
    }

    private void onRecipesUpdatedEvent(RecipesUpdatedEvent event) {
        List<RecipeHolder<?>> recipes = List.copyOf(event.getRecipeManager().getRecipes());
        if (!recipes.isEmpty()) {
            Internal.setClientSyncedRecipes(recipes);
        }
    }

    private void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event) {
        Textures textures = Internal.getTextures();
        event.registerReloadListener((PreparableReloadListener)textures.getGuiSpriteManager());
        event.registerReloadListener((PreparableReloadListener)this.createReloadListener());
    }

    private void onRegisterClientTooltipEvent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(IngredientTooltipComponent.class, Function.identity());
        event.register(IngredientsTooltipComponent.class, Function.identity());
        event.register(PreviewTooltipComponent.class, Function.identity());
        event.register(TagContentTooltipComponent.class, Function.identity());
    }

    private ResourceManagerReloadListener createReloadListener() {
        return resourceManager -> NeoForgeGuiPlugin.getResourceReloadHandler().ifPresent(r -> r.onResourceManagerReload(resourceManager));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.startup;

import com.google.common.collect.ImmutableSetMultimap;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.api.search.ISearchStorageBuilderFactory;
import mezz.jei.common.Internal;
import mezz.jei.common.config.ConfigManager;
import mezz.jei.common.config.DebugConfig;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.common.config.JeiClientConfigs;
import mezz.jei.common.config.file.ConfigSchemaBuilder;
import mezz.jei.common.config.file.FileWatcher;
import mezz.jei.common.network.ClientConnectionHelper;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.platform.Services;
import mezz.jei.common.recipes.VanillaClientRecipeLoader;
import mezz.jei.common.util.ChatUtil;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.LoggedTimer;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.common.util.Translator;
import mezz.jei.library.color.ColorHelper;
import mezz.jei.library.config.ColorNameConfig;
import mezz.jei.library.config.EditModeConfig;
import mezz.jei.library.config.ModIdFormatConfig;
import mezz.jei.library.config.RecipeCategorySortingConfig;
import mezz.jei.library.focus.FocusFactory;
import mezz.jei.library.helpers.CodecHelper;
import mezz.jei.library.ingredients.IngredientManager;
import mezz.jei.library.ingredients.subtypes.SubtypeManager;
import mezz.jei.library.load.PluginCaller;
import mezz.jei.library.load.PluginHelper;
import mezz.jei.library.load.PluginLoader;
import mezz.jei.library.load.registration.RuntimeRegistration;
import mezz.jei.library.plugins.jei.JeiInternalPlugin;
import mezz.jei.library.plugins.vanilla.VanillaPlugin;
import mezz.jei.library.recipes.RecipeManager;
import mezz.jei.library.runtime.JeiHelpers;
import mezz.jei.library.runtime.JeiRuntime;
import mezz.jei.library.startup.IStopCallback;
import mezz.jei.library.startup.PluginAwareJeiFeatures;
import mezz.jei.library.startup.StartData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JeiStarter {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String VANILLA_SERVER_BRAND = "vanilla";
    private final StartData data;
    private final List<IModPlugin> plugins;
    private final VanillaPlugin vanillaPlugin;
    private final ModIdFormatConfig modIdFormatConfig;
    private final ColorNameConfig colorNameConfig;
    private final RecipeCategorySortingConfig recipeCategorySortingConfig;
    private final FileWatcher fileWatcher = new FileWatcher("JEI Config File Watcher");
    private final ConfigManager configManager;
    private final JeiClientConfigs jeiClientConfigs;
    private final List<IStopCallback> stopCallbacks = new ArrayList<IStopCallback>();
    private boolean running = false;

    public JeiStarter(StartData data) {
        ErrorUtil.checkNotEmpty(data.plugins(), "plugins");
        this.data = data;
        this.plugins = data.plugins();
        PluginHelper.removePluginsWithCrashingUids(this.plugins);
        this.vanillaPlugin = PluginHelper.getPluginWithClass(VanillaPlugin.class, this.plugins).orElseThrow(() -> new IllegalStateException("vanilla plugin not found"));
        JeiInternalPlugin jeiInternalPlugin = PluginHelper.getPluginWithClass(JeiInternalPlugin.class, this.plugins).orElse(null);
        PluginHelper.sortPlugins(this.plugins, this.vanillaPlugin, jeiInternalPlugin);
        Path configDir = Services.PLATFORM.getConfigHelper().createJeiConfigDir();
        this.configManager = new ConfigManager();
        ConfigSchemaBuilder debugFileBuilder = new ConfigSchemaBuilder(configDir.resolve("jei-debug.ini"), "jei.config.debug");
        DebugConfig.create(debugFileBuilder);
        debugFileBuilder.build().register(this.fileWatcher, this.configManager);
        ConfigSchemaBuilder modFileBuilder = new ConfigSchemaBuilder(configDir.resolve("jei-mod-id-format.ini"), "jei.config.modIdFormat");
        this.modIdFormatConfig = new ModIdFormatConfig(modFileBuilder);
        modFileBuilder.build().register(this.fileWatcher, this.configManager);
        ConfigSchemaBuilder colorFileBuilder = new ConfigSchemaBuilder(configDir.resolve("jei-colors.ini"), "jei.config.colors");
        this.colorNameConfig = new ColorNameConfig(colorFileBuilder);
        colorFileBuilder.build().register(this.fileWatcher, this.configManager);
        this.jeiClientConfigs = new JeiClientConfigs(configDir.resolve("jei-client.ini"));
        this.jeiClientConfigs.register(this.fileWatcher, this.configManager);
        Internal.setJeiClientConfigs(this.jeiClientConfigs);
        this.fileWatcher.start();
        this.recipeCategorySortingConfig = new RecipeCategorySortingConfig(configDir.resolve("recipe-category-sort-order.ini"));
        PluginCaller.callOnPlugins("Sending ConfigManager", this.plugins, p -> p.onConfigManagerAvailable(this.configManager));
    }

    public void start() {
        List<RecipeHolder<?>> vanillaRecipes;
        if (this.running) {
            LOGGER.error("Failed to start JEI, it is already running.");
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            LOGGER.error("Failed to start JEI, there is no Minecraft client level.");
            return;
        }
        RegistryAccess registryAccess = level.registryAccess();
        RegistryUtil.setRegistryAccess(registryAccess);
        if (!Internal.hasClientRecipes() && !(vanillaRecipes = VanillaClientRecipeLoader.getVanillaRecipes(registryAccess)).isEmpty()) {
            Internal.setClientFallbackRecipes(vanillaRecipes);
            level.getRecipeManager().replaceRecipes(vanillaRecipes);
        }
        LoggedTimer totalTime = new LoggedTimer();
        totalTime.start("Starting JEI");
        this.configManager.onJeiStarted();
        PluginCaller.callOnPlugins("Configuring JEI", this.plugins, p -> p.configureJei(new PluginAwareJeiFeatures(Internal.getJeiFeatures(), (IModPlugin)p)));
        ColorHelper colorHelper = new ColorHelper(this.colorNameConfig);
        IIngredientFilterConfig ingredientFilterConfig = this.jeiClientConfigs.getIngredientFilterConfig();
        SubtypeManager subtypeManager = PluginLoader.registerSubtypes(this.data);
        IngredientManager ingredientManager = PluginLoader.registerIngredients(this.data, subtypeManager, colorHelper, ingredientFilterConfig);
        this.stopCallbacks.add(ingredientManager::onRuntimeStopped);
        FocusFactory focusFactory = new FocusFactory(ingredientManager);
        CodecHelper codecHelper = new CodecHelper(ingredientManager, focusFactory);
        Path configDir = Services.PLATFORM.getConfigHelper().createJeiConfigDir();
        EditModeConfig.FileSerializer editModeSerializer = new EditModeConfig.FileSerializer(configDir.resolve("blacklist.json"), registryAccess, codecHelper);
        EditModeConfig editModeConfig = new EditModeConfig(editModeSerializer, ingredientManager);
        ImmutableSetMultimap<String, String> modAliases = PluginLoader.registerModAliases(this.data, ingredientFilterConfig);
        JeiHelpers jeiHelpers = PluginLoader.createJeiHelpers(modAliases, this.modIdFormatConfig, colorHelper, editModeConfig, focusFactory, codecHelper, ingredientManager, subtypeManager);
        this.stopCallbacks.add(jeiHelpers::onRuntimeStopped);
        ISearchStorageBuilderFactory searchStorageBuilderFactory = PluginLoader.createSearchStorageFactory(this.plugins);
        RecipeManager recipeManager = PluginLoader.createRecipeManager(this.plugins, this.vanillaPlugin, this.recipeCategorySortingConfig, jeiHelpers, ingredientManager);
        IRecipeTransferManager recipeTransferManager = PluginLoader.createRecipeTransferManager(this.vanillaPlugin, this.plugins, jeiHelpers, this.data.serverConnection());
        LoggedTimer timer = new LoggedTimer();
        timer.start("Building runtime");
        IScreenHelper screenHelper = PluginLoader.createGuiScreenHelper(this.plugins, jeiHelpers, ingredientManager);
        RuntimeRegistration runtimeRegistration = new RuntimeRegistration(recipeManager, jeiHelpers, editModeConfig, ingredientManager, recipeTransferManager, screenHelper, searchStorageBuilderFactory);
        PluginCaller.callOnPlugins("Registering Runtime", this.plugins, p -> p.registerRuntime(runtimeRegistration));
        JeiRuntime jeiRuntime = new JeiRuntime(recipeManager, ingredientManager, Internal.getKeyMappings(), jeiHelpers, screenHelper, recipeTransferManager, editModeConfig, runtimeRegistration.getIngredientListOverlay(), runtimeRegistration.getBookmarkOverlay(), runtimeRegistration.getRecipesGui(), runtimeRegistration.getIngredientFilter(), this.configManager);
        timer.stop();
        PluginCaller.callOnPlugins("Sending Runtime", this.plugins, p -> p.onRuntimeAvailable(jeiRuntime));
        Internal.setRuntime(jeiRuntime);
        this.running = true;
        totalTime.stop();
        this.verifyClientRecipes(minecraft);
    }

    private void verifyClientRecipes(Minecraft minecraft) {
        IConnectionToServer serverConnection = this.data.serverConnection();
        List<RecipeHolder<?>> clientRecipes = Internal.getClientSyncedRecipes();
        if (Internal.hasClientSyncedRecipes() && clientRecipes.isEmpty()) {
            String key = "jei.message.server.recipe.sync.error";
            JeiStarter.writeChatMessage(minecraft, (Component)Component.translatable((String)key).withStyle(ChatFormatting.RED));
            LOGGER.error(Translator.translateToLocal(key));
        } else if (Internal.hasClientFallbackRecipes()) {
            if (!serverConnection.isJeiOnServer() && serverConnection.isSameModLoader()) {
                String key = "jei.message.server.recipe.sync.jei.missing";
                String serverBrand = ClientConnectionHelper.getServerBrand();
                JeiStarter.writeChatMessage(minecraft, (Component)Component.translatable((String)key, (Object[])new Object[]{serverBrand}).withStyle(ChatFormatting.RED));
                LOGGER.warn(Translator.translateToLocalFormatted(key, serverBrand));
            } else if (ClientConnectionHelper.hasServerBrand(VANILLA_SERVER_BRAND)) {
                String key = "jei.message.server.recipe.sync.vanilla";
                JeiStarter.writeChatMessage(minecraft, (Component)Component.translatable((String)key).withStyle(ChatFormatting.YELLOW));
                LOGGER.warn(Translator.translateToLocal(key));
            } else {
                String key = "jei.message.server.recipe.sync.unavailable";
                String serverBrand = ClientConnectionHelper.getServerBrand();
                JeiStarter.writeChatMessage(minecraft, (Component)Component.translatable((String)key, (Object[])new Object[]{serverBrand}).withStyle(ChatFormatting.RED));
                LOGGER.warn(Translator.translateToLocalFormatted(key, serverBrand));
            }
        }
    }

    private static void writeChatMessage(Minecraft minecraft, Component component) {
        LocalPlayer player = minecraft.player;
        if (player != null) {
            ChatUtil.writeChatMessage((Player)player, component);
        }
    }

    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
        LOGGER.info("Stopping JEI");
        List<IModPlugin> plugins = this.data.plugins();
        PluginCaller.callOnPlugins("Sending Runtime Unavailable", plugins, IModPlugin::onRuntimeUnavailable);
        Internal.onRuntimeStopped();
        for (IStopCallback stopCallback : this.stopCallbacks) {
            stopCallback.onRuntimeStopped();
        }
        this.stopCallbacks.clear();
        RegistryUtil.setRegistryAccess(null);
    }
}


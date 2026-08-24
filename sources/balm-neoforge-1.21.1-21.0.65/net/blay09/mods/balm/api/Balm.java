package net.blay09.mods.balm.api;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.balm.api.capability.BalmCapabilities;
import net.blay09.mods.balm.api.command.BalmCommands;
import net.blay09.mods.balm.api.compat.BalmModSupport;
import net.blay09.mods.balm.api.component.BalmComponents;
import net.blay09.mods.balm.api.config.BalmConfig;
import net.blay09.mods.balm.api.entity.BalmEntities;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.balm.api.loot.BalmLootTables;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.balm.api.module.BalmModule;
import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.particle.BalmParticles;
import net.blay09.mods.balm.api.permission.BalmPermissions;
import net.blay09.mods.balm.api.provider.BalmProviders;
import net.blay09.mods.balm.api.proxy.ModProxy;
import net.blay09.mods.balm.api.proxy.PlatformProxy;
import net.blay09.mods.balm.api.proxy.SidedProxy;
import net.blay09.mods.balm.api.recipe.BalmRecipes;
import net.blay09.mods.balm.api.resources.BalmResources;
import net.blay09.mods.balm.api.sound.BalmSounds;
import net.blay09.mods.balm.api.stats.BalmStats;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

public class Balm {
   private static final BalmRuntime<BalmRuntimeLoadContext> runtime = BalmRuntimeSpi.create();

   @Deprecated
   public static void registerModule(BalmModule module) {
      runtime.registerModule(module);
   }

   @Deprecated
   public static void onRuntimeAvailable(Runnable callback) {
      runtime.onRuntimeAvailable(callback);
   }

   @Deprecated(
      since = "1.22"
   )
   public static void initialize(String modId, BalmRuntimeLoadContext context, Runnable initializer) {
      initializeMod(modId, context, initializer);
   }

   @Deprecated
   public static void initializeMod(String modId, BalmRuntimeLoadContext context, Runnable initializer) {
      runtime.initializeMod(modId, context, initializer);
   }

   public static void initializeMod(String modId, BalmRuntimeLoadContext context, Consumer<BalmRegistrars> initializer) {
      runtime.initializeMod(modId, context, initializer);
   }

   public static <T extends BalmRuntimeLoadContext> void initializeMod(String modId, T context, BalmModule module) {
      runtime.initializeMod(modId, context, registrars -> registrars.registerModule(module));
   }

   public static <T extends BalmRuntimeLoadContext> void initializeMod(String modId, T context, BalmModule... modules) {
      runtime.initializeMod(modId, context, registrars -> {
         for (BalmModule module : modules) {
            registrars.registerModule(module);
         }
      });
   }

   public static boolean isModLoaded(String modId) {
      return runtime.isModLoaded(modId);
   }

   public static String getModName(String modId) {
      return runtime.getModName(modId);
   }

   public static <T> PlatformProxy<T> platformProxy() {
      return runtime.platformProxy();
   }

   public static <T> ModProxy<T> modProxy() {
      return runtime.modProxy();
   }

   public static <T> ModProxy<T> modProxy(ResourceLocation identifier) {
      return runtime.modProxy(identifier);
   }

   public static <T> SidedProxy<T> sidedProxy(String commonName, String clientName) {
      return runtime.sidedProxy(commonName, clientName);
   }

   public static void initializeIfLoaded(String modId, String className) {
      runtime.initializeIfLoaded(modId, className);
   }

   @Deprecated
   public static void addServerReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener) {
      runtime.addServerReloadListener(identifier, reloadListener);
   }

   @Deprecated
   public static void addServerReloadListener(ResourceLocation identifier, Consumer<ResourceManager> reloadListener) {
      runtime.addServerReloadListener(identifier, reloadListener);
   }

   @Deprecated
   public static BalmProxy getProxy() {
      return safeClientAccess();
   }

   public static BalmProxy safeClientAccess() {
      return runtime.getProxy();
   }

   public static BalmEvents getEvents() {
      return runtime.getEvents();
   }

   @Deprecated
   public static BalmConfig getConfig() {
      return config();
   }

   public static BalmConfig config() {
      return runtime.getConfig();
   }

   @Deprecated
   public static BalmNetworking getNetworking() {
      return networking();
   }

   public static BalmNetworking networking() {
      return runtime.getNetworking();
   }

   @Deprecated
   public static BalmWorldGen getWorldGen() {
      return runtime.getWorldGen();
   }

   public static BalmWorldGen biomeModifications() {
      return runtime.getWorldGen();
   }

   @Deprecated
   public static BalmBlocks getBlocks() {
      return runtime.getBlocks();
   }

   @Deprecated
   public static BalmBlockEntities getBlockEntities() {
      return runtime.getBlockEntities();
   }

   @Deprecated
   public static BalmItems getItems() {
      return runtime.getItems();
   }

   @Deprecated
   public static BalmComponents getComponents() {
      return runtime.getComponents();
   }

   @Deprecated
   public static BalmMenus getMenus() {
      return runtime.getMenus();
   }

   @Deprecated
   public static BalmHooks getHooks() {
      return hooks();
   }

   public static BalmHooks hooks() {
      return runtime.getHooks();
   }

   @Deprecated
   public static BalmRecipes getRecipes() {
      return runtime.getRecipes();
   }

   public static BalmRegistries getRegistries() {
      return runtime.getRegistries();
   }

   @Deprecated
   public static BalmSounds getSounds() {
      return runtime.getSounds();
   }

   @Deprecated
   public static BalmEntities getEntities() {
      return runtime.getEntities();
   }

   @Deprecated
   public static BalmCapabilities getCapabilities() {
      return capabilities();
   }

   public static BalmCapabilities capabilities() {
      return runtime.getCapabilities();
   }

   @Deprecated
   public static BalmCommands getCommands() {
      return commands();
   }

   public static BalmCommands commands() {
      return runtime.getCommands();
   }

   @Deprecated
   public static BalmLootTables getLootTables() {
      return lootModifiers();
   }

   public static BalmLootTables lootModifiers() {
      return runtime.getLootTables();
   }

   @Deprecated
   public static BalmStats getStats() {
      return runtime.getStats();
   }

   @Deprecated
   public static BalmModSupport getModSupport() {
      return modSupport();
   }

   public static BalmModSupport modSupport() {
      return runtime.getModSupport();
   }

   @Deprecated
   public static BalmParticles getParticles() {
      return runtime.getParticles();
   }

   @Deprecated
   public static BalmPermissions getPermissions() {
      return permissions();
   }

   public static BalmPermissions permissions() {
      return runtime.getPermissions();
   }

   @Deprecated
   public static BalmResources getResources() {
      return runtime.getResources();
   }

   public static String getPlatform() {
      return runtime.getPlatform();
   }

   public static BalmEnvironment getEnvironment() {
      return runtime.getEnvironment();
   }

   public static BalmRuntime<? extends BalmRuntimeLoadContext> getRuntime() {
      return runtime;
   }

   public static Map<String, Path> lookupAllModPaths(String path) {
      return runtime.lookupAllModPaths(path);
   }

   public static Optional<Path> lookupModPaths(String modId, String path) {
      return runtime.lookupModPath(modId, path);
   }

   @Deprecated
   public static BalmProviders getProviders() {
      return runtime.getProviders();
   }

   public static boolean isDevelopmentEnvironment() {
      return runtime.isDevelopmentEnvironment();
   }
}

package net.blay09.mods.balm.api;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.balm.api.capability.BalmCapabilities;
import net.blay09.mods.balm.api.command.BalmArgumentTypeRegistrar;
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
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistrar;
import net.blay09.mods.balm.core.particles.BalmParticleTypeRegistrar;
import net.blay09.mods.balm.platform.attachment.BalmDataAttachmentTypeRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmResourceConditionRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmResourceReloadListenerRegistrar;
import net.blay09.mods.balm.stats.BalmCustomStatRegistrar;
import net.blay09.mods.balm.world.entity.BalmEntityTypeRegistrar;
import net.blay09.mods.balm.world.entity.ai.village.poi.BalmPoiTypeRegistrar;
import net.blay09.mods.balm.world.entity.npc.villager.BalmVillagerTradeRegistrar;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.crafting.BalmRecipeTypeRegistrar;
import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

public interface BalmRuntime<TLoadContext extends BalmRuntimeLoadContext> {
   BalmConfig getConfig();

   BalmEvents getEvents();

   BalmWorldGen getWorldGen();

   @Deprecated
   BalmBlocks getBlocks();

   @Deprecated
   BalmBlockEntities getBlockEntities();

   @Deprecated
   BalmItems getItems();

   @Deprecated
   BalmMenus getMenus();

   BalmNetworking getNetworking();

   BalmHooks getHooks();

   BalmRegistries getRegistries();

   @Deprecated
   BalmSounds getSounds();

   @Deprecated
   BalmEntities getEntities();

   BalmCapabilities getCapabilities();

   @Deprecated
   BalmProviders getProviders();

   BalmCommands getCommands();

   BalmLootTables getLootTables();

   @Deprecated
   BalmStats getStats();

   @Deprecated
   BalmRecipes getRecipes();

   BalmModSupport getModSupport();

   @Deprecated
   BalmParticles getParticles();

   BalmPermissions getPermissions();

   boolean isModLoaded(String var1);

   String getModName(String var1);

   <TProxy> SidedProxy<TProxy> sidedProxy(String var1, String var2);

   default void initializeMod(String modId, TLoadContext context, Runnable initializer) {
      this.initializeMod(modId, context, registrars -> initializer.run());
   }

   void initializeMod(String var1, TLoadContext var2, Consumer<BalmRegistrars> var3);

   void initializeIfLoaded(String var1, String var2);

   void addServerReloadListener(ResourceLocation var1, PreparableReloadListener var2);

   void addServerReloadListener(ResourceLocation var1, Consumer<ResourceManager> var2);

   @Deprecated
   BalmComponents getComponents();

   <T> PlatformProxy<T> platformProxy();

   <T> ModProxy<T> modProxy();

   <T> ModProxy<T> modProxy(ResourceLocation var1);

   String getPlatform();

   default void initializeModule(BalmModule module) {
      String modId = module.getId().getNamespace();
      module.registerConfig(this.getConfig());
      module.registerResources(this.getResources());
      this.resourceConditions(modId, module::registerResourceConditions);
      module.registerAdditional(this.getRegistries());
      module.registerAdditional(this.registrar());
      module.registerComponents(this.getComponents());
      this.dataAttachmentTypes(modId, module::registerDataAttachmentTypes);
      this.dataComponentTypes(modId, module::registerDataComponentTypes);
      module.registerBlocks(this.getBlocks().scoped(modId));
      this.blocks(modId, module::registerBlocks);
      module.registerBlockEntities(this.getBlockEntities());
      this.blockEntityTypes(modId, module::registerBlockEntityTypes);
      module.registerItems(this.getItems().scoped(modId));
      this.items(modId, module::registerItems);
      this.creativeModeTabs(modId, module::registerCreativeModeTabs);
      module.registerEntities(this.getEntities());
      this.entityTypes(modId, module::registerEntityTypes);
      module.registerWorldGen(this.getWorldGen());
      this.poiTypes(modId, module::registerPoiTypes);
      module.registerNetworking(this.getNetworking());
      module.registerMenus(this.getMenus());
      this.menuTypes(modId, module::registerMenuTypes);
      this.argumentTypes(modId, module::registerArgumentTypes);
      module.registerCapabilities(this.getCapabilities());
      module.registerCommands(this.getCommands());
      module.registerRecipes(this.getRecipes());
      this.recipeTypes(modId, module::registerRecipeTypes);
      module.registerLootTables(this.getLootTables());
      module.registerStats(this.getStats());
      this.customStats(modId, module::registerCustomStats);
      module.registerSounds(this.getSounds());
      module.registerSoundEvents(this.registrar(Registries.SOUND_EVENT, modId));
      module.registerPermissions(this.getPermissions());
      module.registerParticles(this.getParticles());
      this.particleTypes(modId, module::registerParticleTypes);
      this.resourceReloadListeners(modId, module::registerReloadListeners);
      module.registerEvents(this.getEvents());
      module.initialize();
   }

   BalmProxy getProxy();

   boolean isReady();

   void onRuntimeAvailable(Runnable var1);

   void registerModule(BalmModule var1);

   void registerModule(BalmRegistrars var1, BalmModule var2);

   @Deprecated
   BalmResources getResources();

   BalmEnvironment getEnvironment();

   boolean isDevelopmentEnvironment();

   Map<String, Path> lookupAllModPaths(String var1);

   Optional<Path> lookupModPath(String var1, String var2);

   void menuTypes(String var1, Consumer<BalmMenuTypeRegistrar> var2);

   void entityTypes(String var1, Consumer<BalmEntityTypeRegistrar> var2);

   void particleTypes(String var1, Consumer<BalmParticleTypeRegistrar> var2);

   void customStats(String var1, Consumer<BalmCustomStatRegistrar> var2);

   void villagerTrades(String var1, Consumer<BalmVillagerTradeRegistrar> var2);

   BalmRegistrar registrar();

   default <T> BalmRegistrar.Scoped<T> registrar(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
      return this.registrar().scoped(registryKey, namespace);
   }

   void blocks(String var1, Consumer<BalmBlockRegistrar> var2);

   void items(String var1, Consumer<BalmItemRegistrar> var2);

   void recipeTypes(String var1, Consumer<BalmRecipeTypeRegistrar> var2);

   void dataComponentTypes(String var1, Consumer<BalmDataComponentTypeRegistrar> var2);

   void dataAttachmentTypes(String var1, Consumer<BalmDataAttachmentTypeRegistrar> var2);

   void creativeModeTabs(String var1, Consumer<BalmCreativeModeTabRegistrar> var2);

   void blockEntityTypes(String var1, Consumer<BalmBlockEntityTypeRegistrar> var2);

   void poiTypes(String var1, Consumer<BalmPoiTypeRegistrar> var2);

   void resourceReloadListeners(String var1, Consumer<BalmResourceReloadListenerRegistrar> var2);

   void resourceConditions(String var1, Consumer<BalmResourceConditionRegistrar> var2);

   void argumentTypes(String var1, Consumer<BalmArgumentTypeRegistrar> var2);
}

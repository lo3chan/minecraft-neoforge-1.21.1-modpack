package net.blay09.mods.balm.neoforge;

import com.mojang.datafixers.util.Pair;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.balm.api.BalmHooks;
import net.blay09.mods.balm.api.BalmRegistries;
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
import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.particle.BalmParticles;
import net.blay09.mods.balm.api.permission.BalmPermissions;
import net.blay09.mods.balm.api.provider.BalmProviders;
import net.blay09.mods.balm.api.recipe.BalmRecipes;
import net.blay09.mods.balm.api.resources.BalmResources;
import net.blay09.mods.balm.api.sound.BalmSounds;
import net.blay09.mods.balm.api.stats.BalmStats;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.balm.common.BalmLoadContexts;
import net.blay09.mods.balm.common.CommonBalmLootTables;
import net.blay09.mods.balm.common.CommonBalmRuntime;
import net.blay09.mods.balm.common.LegacyNamespaceResolver;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.balm.core.particles.BalmParticleTypeRegistrar;
import net.blay09.mods.balm.neoforge.block.NeoForgeBalmBlocks;
import net.blay09.mods.balm.neoforge.block.entity.NeoForgeBalmBlockEntities;
import net.blay09.mods.balm.neoforge.capability.NeoForgeBalmCapabilities;
import net.blay09.mods.balm.neoforge.command.NeoForgeBalmArgumentTypeRegistrar;
import net.blay09.mods.balm.neoforge.command.NeoForgeBalmCommands;
import net.blay09.mods.balm.neoforge.compat.NeoForgeBalmModSupport;
import net.blay09.mods.balm.neoforge.component.NeoForgeBalmComponents;
import net.blay09.mods.balm.neoforge.config.NeoForgeBalmConfig;
import net.blay09.mods.balm.neoforge.core.internal.NeoForgeBalmRegistrar;
import net.blay09.mods.balm.neoforge.core.particles.internal.NeoForgeBalmParticleTypeRegistrar;
import net.blay09.mods.balm.neoforge.entity.NeoForgeBalmEntities;
import net.blay09.mods.balm.neoforge.event.NeoForgeBalmCommonEvents;
import net.blay09.mods.balm.neoforge.event.NeoForgeBalmEvents;
import net.blay09.mods.balm.neoforge.item.NeoForgeBalmItems;
import net.blay09.mods.balm.neoforge.menu.NeoForgeBalmMenus;
import net.blay09.mods.balm.neoforge.network.NeoForgeBalmNetworking;
import net.blay09.mods.balm.neoforge.particle.NeoForgeBalmParticles;
import net.blay09.mods.balm.neoforge.permission.NeoForgeBalmPermissions;
import net.blay09.mods.balm.neoforge.platform.attachment.internal.NeoForgeBalmDataAttachmentTypeRegistrar;
import net.blay09.mods.balm.neoforge.provider.NeoForgeBalmProviders;
import net.blay09.mods.balm.neoforge.recipe.NeoForgeBalmRecipes;
import net.blay09.mods.balm.neoforge.resources.NeoForgeBalmResources;
import net.blay09.mods.balm.neoforge.sound.NeoForgeBalmSounds;
import net.blay09.mods.balm.neoforge.stats.NeoForgeBalmStats;
import net.blay09.mods.balm.neoforge.stats.internal.NeoForgeBalmCustomStatRegistrar;
import net.blay09.mods.balm.neoforge.world.NeoForgeBalmWorldGen;
import net.blay09.mods.balm.neoforge.world.entity.internal.NeoForgeBalmEntityTypeRegistrar;
import net.blay09.mods.balm.neoforge.world.entity.npc.villager.internal.NeoForgeBalmVillagerTradeRegistrar;
import net.blay09.mods.balm.neoforge.world.inventory.internal.NeoForgeBalmMenuTypeRegistrar;
import net.blay09.mods.balm.neoforge.world.item.internal.NeoForgeBalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.neoforge.world.level.block.entity.internal.NeoForgeBalmBlockEntityTypeRegistrar;
import net.blay09.mods.balm.platform.attachment.BalmDataAttachmentTypeRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmResourceConditionRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmResourceReloadListenerRegistrar;
import net.blay09.mods.balm.server.packs.resources.internal.NeoForgeBalmResourceConditionRegistrar;
import net.blay09.mods.balm.server.packs.resources.internal.NeoForgeBalmResourceReloadListenerRegistrar;
import net.blay09.mods.balm.stats.BalmCustomStatRegistrar;
import net.blay09.mods.balm.world.entity.BalmEntityTypeRegistrar;
import net.blay09.mods.balm.world.entity.ai.village.poi.BalmPoiTypeRegistrar;
import net.blay09.mods.balm.world.entity.ai.village.poi.internal.BalmPoiTypeRegistrarImpl;
import net.blay09.mods.balm.world.entity.npc.villager.BalmVillagerTradeRegistrar;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModFileInfo;

public class NeoForgeBalmRuntime extends CommonBalmRuntime<NeoForgeLoadContext> {
   private final NamespaceResolver legacyNamespaceResolver = new LegacyNamespaceResolver(() -> ModLoadingContext.get().getActiveNamespace());
   private final BalmWorldGen worldGen = new NeoForgeBalmWorldGen();
   @Deprecated
   private final BalmItems items = new NeoForgeBalmItems(this.legacyNamespaceResolver);
   @Deprecated
   private final BalmBlocks blocks = new NeoForgeBalmBlocks(this.legacyNamespaceResolver, this.items);
   @Deprecated
   private final BalmBlockEntities blockEntities = new NeoForgeBalmBlockEntities();
   private final NeoForgeBalmEvents events = new NeoForgeBalmEvents();
   @Deprecated
   private final BalmMenus menus = new NeoForgeBalmMenus();
   private final BalmNetworking networking = new NeoForgeBalmNetworking(this.legacyNamespaceResolver);
   private final BalmConfig config = new NeoForgeBalmConfig();
   private final BalmHooks hooks = new NeoForgeBalmHooks();
   private final BalmRegistries registries = new NeoForgeBalmRegistries();
   @Deprecated
   private final BalmSounds sounds = new NeoForgeBalmSounds();
   @Deprecated
   private final BalmEntities entities = new NeoForgeBalmEntities(this.legacyNamespaceResolver);
   private final BalmCapabilities capabilities = new NeoForgeBalmCapabilities(this.legacyNamespaceResolver);
   @Deprecated
   private final BalmProviders providers = new NeoForgeBalmProviders();
   private final BalmCommands commands = new NeoForgeBalmCommands();
   private final BalmLootTables lootTables = new CommonBalmLootTables();
   @Deprecated
   private final BalmStats stats = new NeoForgeBalmStats(this.legacyNamespaceResolver);
   @Deprecated
   private final BalmRecipes recipes = new NeoForgeBalmRecipes();
   @Deprecated
   private final BalmComponents components = new NeoForgeBalmComponents();
   private final BalmModSupport modSupport = new NeoForgeBalmModSupport(this);
   @Deprecated
   private final BalmParticles particles = new NeoForgeBalmParticles();
   private final BalmPermissions permissions = new NeoForgeBalmPermissions();
   private final BalmRegistrar registrar = new NeoForgeBalmRegistrar();
   @Deprecated
   private final BalmResources resources = new NeoForgeBalmResources();

   public NeoForgeBalmRuntime() {
      NeoForgeBalmCommonEvents.registerEvents(this.events);
   }

   @Override
   public BalmConfig getConfig() {
      return this.config;
   }

   @Override
   public BalmEvents getEvents() {
      return this.events;
   }

   @Override
   public BalmWorldGen getWorldGen() {
      return this.worldGen;
   }

   @Deprecated
   @Override
   public BalmBlocks getBlocks() {
      return this.blocks;
   }

   @Deprecated
   @Override
   public BalmBlockEntities getBlockEntities() {
      return this.blockEntities;
   }

   @Deprecated
   @Override
   public BalmItems getItems() {
      return this.items;
   }

   @Deprecated
   @Override
   public BalmMenus getMenus() {
      return this.menus;
   }

   @Override
   public BalmNetworking getNetworking() {
      return this.networking;
   }

   @Override
   public BalmHooks getHooks() {
      return this.hooks;
   }

   @Override
   public BalmRegistries getRegistries() {
      return this.registries;
   }

   @Deprecated
   @Override
   public BalmSounds getSounds() {
      return this.sounds;
   }

   @Deprecated
   @Override
   public BalmEntities getEntities() {
      return this.entities;
   }

   @Override
   public BalmCapabilities getCapabilities() {
      return this.capabilities;
   }

   @Deprecated
   @Override
   public BalmProviders getProviders() {
      return this.providers;
   }

   @Override
   public BalmCommands getCommands() {
      return this.commands;
   }

   @Override
   public BalmLootTables getLootTables() {
      return this.lootTables;
   }

   @Deprecated
   @Override
   public BalmStats getStats() {
      return this.stats;
   }

   @Deprecated
   @Override
   public BalmRecipes getRecipes() {
      return this.recipes;
   }

   @Override
   public boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }

   @Override
   protected Optional<String> getModVersion(String modId) {
      return ModList.get().getModContainerById(modId).map(it -> it.getModInfo().getVersion().toString());
   }

   @Override
   public String getModName(String modId) {
      return ModList.get().getModContainerById(modId).map(it -> it.getModInfo().getDisplayName()).orElse(modId);
   }

   public void initializeMod(String modId, NeoForgeLoadContext context, Consumer<BalmRegistrars> initializer) {
      BalmLoadContexts.register(modId, context);
      initializer.accept(new BalmRegistrars(this, modId));
      IEventBus modBus = context.modBus();
      DeferredRegisters.register(modId, modBus);
      ModBusEventRegisters.register(modId, modBus);
   }

   @Override
   public void addServerReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener) {
      NeoForge.EVENT_BUS.addListener(event -> event.addListener(reloadListener));
   }

   @Override
   public void addServerReloadListener(ResourceLocation identifier, Consumer<ResourceManager> reloadListener) {
      NeoForge.EVENT_BUS.addListener(event -> event.addListener(reloadListener::accept));
   }

   @Deprecated
   @Override
   public BalmComponents getComponents() {
      return this.components;
   }

   @Override
   public BalmModSupport getModSupport() {
      return this.modSupport;
   }

   @Deprecated
   @Override
   public BalmParticles getParticles() {
      return this.particles;
   }

   @Override
   public BalmPermissions getPermissions() {
      return this.permissions;
   }

   @Override
   public String getPlatform() {
      return "neoforge";
   }

   @Deprecated
   @Override
   public BalmResources getResources() {
      return this.resources;
   }

   @Override
   public BalmEnvironment getEnvironment() {
      return switch (FMLEnvironment.dist) {
         case CLIENT -> BalmEnvironment.CLIENT;
         case DEDICATED_SERVER -> BalmEnvironment.SERVER;
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   public Map<String, Path> lookupAllModPaths(String path) {
      return ModList.get()
         .getMods()
         .stream()
         .map(it -> new Pair(it.getModId(), it.getOwningFile().getFile().findResource(new String[]{path})))
         .filter(it -> Files.exists((Path)it.getSecond()))
         .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
   }

   @Override
   public Optional<Path> lookupModPath(String modId, String path) {
      IModFileInfo modFile = ModList.get().getModFileById(modId);
      Path nioPath = modFile.getFile().findResource(new String[]{path});
      return Files.exists(nioPath) ? Optional.of(nioPath) : Optional.empty();
   }

   @Override
   public BalmRegistrar registrar() {
      return this.registrar;
   }

   @Override
   public void creativeModeTabs(String namespace, Consumer<BalmCreativeModeTabRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmCreativeModeTabRegistrar(this.registrar(), namespace));
   }

   @Override
   public void blockEntityTypes(String namespace, Consumer<BalmBlockEntityTypeRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmBlockEntityTypeRegistrar(this.registrar(), namespace));
   }

   @Override
   public void entityTypes(String namespace, Consumer<BalmEntityTypeRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmEntityTypeRegistrar(this.registrar(), namespace));
   }

   @Override
   public void poiTypes(String namespace, Consumer<BalmPoiTypeRegistrar> initializer) {
      initializer.accept(new BalmPoiTypeRegistrarImpl(this.registrar(), namespace));
   }

   @Override
   public void menuTypes(String namespace, Consumer<BalmMenuTypeRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmMenuTypeRegistrar(this.registrar(), namespace));
   }

   @Override
   public void particleTypes(String namespace, Consumer<BalmParticleTypeRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmParticleTypeRegistrar(this.registrar(), namespace));
   }

   @Override
   public void customStats(String namespace, Consumer<BalmCustomStatRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmCustomStatRegistrar(this.registrar(), namespace));
   }

   @Override
   public void argumentTypes(String namespace, Consumer<BalmArgumentTypeRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmArgumentTypeRegistrar(namespace));
   }

   @Override
   public void villagerTrades(String namespace, Consumer<BalmVillagerTradeRegistrar> initializer) {
      NeoForge.EVENT_BUS.addListener(event -> initializer.accept(new NeoForgeBalmVillagerTradeRegistrar(event)));
   }

   @Override
   public void resourceReloadListeners(String namespace, Consumer<BalmResourceReloadListenerRegistrar> initializer) {
      NeoForge.EVENT_BUS.addListener(event -> initializer.accept(new NeoForgeBalmResourceReloadListenerRegistrar(event)));
   }

   @Override
   public void resourceConditions(String namespace, Consumer<BalmResourceConditionRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmResourceConditionRegistrar(namespace));
   }

   @Override
   public void dataAttachmentTypes(String namespace, Consumer<BalmDataAttachmentTypeRegistrar> initializer) {
      initializer.accept(new NeoForgeBalmDataAttachmentTypeRegistrar(this.registrar(), namespace));
   }
}

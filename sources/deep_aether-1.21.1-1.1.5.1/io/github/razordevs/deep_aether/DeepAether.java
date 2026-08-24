package io.github.razordevs.deep_aether;

import com.aetherteam.aether.block.dispenser.AetherDispenseBehaviors;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.genesis.entity.GenesisEntityTypes;
import com.google.common.reflect.Reflection;
import com.mojang.logging.LogUtils;
import io.github.razordevs.aeroblender.aether.AetherRuleCategory;
import io.github.razordevs.deep_aether.advancement.DAAdvancementTriggers;
import io.github.razordevs.deep_aether.block.behavior.DADispenseBehaviors;
import io.github.razordevs.deep_aether.block.behavior.DaCauldronInteraction;
import io.github.razordevs.deep_aether.datagen.DABlockstateData;
import io.github.razordevs.deep_aether.datagen.DADataMapData;
import io.github.razordevs.deep_aether.datagen.DAItemModelData;
import io.github.razordevs.deep_aether.datagen.DARecipeData;
import io.github.razordevs.deep_aether.datagen.DARegistryDataGenerator;
import io.github.razordevs.deep_aether.datagen.loot.DALootTableData;
import io.github.razordevs.deep_aether.datagen.loot.modifiers.DAGlobalLootModifiers;
import io.github.razordevs.deep_aether.datagen.loot.modifiers.DALootDataProvider;
import io.github.razordevs.deep_aether.datagen.tags.DABiomeTagData;
import io.github.razordevs.deep_aether.datagen.tags.DABlockTagData;
import io.github.razordevs.deep_aether.datagen.tags.DADamageTypeTags;
import io.github.razordevs.deep_aether.datagen.tags.DAEntityTagData;
import io.github.razordevs.deep_aether.datagen.tags.DAFluidTagData;
import io.github.razordevs.deep_aether.datagen.tags.DAItemTagData;
import io.github.razordevs.deep_aether.datagen.tags.DASoundTagData;
import io.github.razordevs.deep_aether.event.DAGeneralEvents;
import io.github.razordevs.deep_aether.fluids.DAFluidTypes;
import io.github.razordevs.deep_aether.init.DABlockEntityTypes;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAEntities;
import io.github.razordevs.deep_aether.init.DAFluids;
import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.init.DAMenuTypes;
import io.github.razordevs.deep_aether.init.DAMobEffects;
import io.github.razordevs.deep_aether.init.DAParticles;
import io.github.razordevs.deep_aether.init.DAPotions;
import io.github.razordevs.deep_aether.init.DARecipeBookTypes;
import io.github.razordevs.deep_aether.init.DARecipeCategories;
import io.github.razordevs.deep_aether.init.DASounds;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.gear.DAArmorMaterials;
import io.github.razordevs.deep_aether.item.misc.SkyrootPoisonBucketWrapper;
import io.github.razordevs.deep_aether.networking.attachment.DAAttachments;
import io.github.razordevs.deep_aether.networking.packet.DAPlayerSyncPacket;
import io.github.razordevs.deep_aether.recipe.DARecipeSerializers;
import io.github.razordevs.deep_aether.recipe.DARecipeTypes;
import io.github.razordevs.deep_aether.world.biomes.DARareRegion;
import io.github.razordevs.deep_aether.world.biomes.DARegion;
import io.github.razordevs.deep_aether.world.biomes.DASurfaceData;
import io.github.razordevs.deep_aether.world.feature.DAFeatures;
import io.github.razordevs.deep_aether.world.feature.tree.decorators.DADecoratorType;
import io.github.razordevs.deep_aether.world.feature.tree.decorators.DARootPlacers;
import io.github.razordevs.deep_aether.world.feature.tree.foliage.DAFoliagePlacers;
import io.github.razordevs.deep_aether.world.feature.tree.trunk.DaTrunkPlacerTypes;
import io.github.razordevs.deep_aether.world.placementmodifier.DAPlacementModifiers;
import io.github.razordevs.deep_aether.world.structure.DAStructurePieceTypes;
import io.github.razordevs.deep_aether.world.structure.DAStructureTypes;
import io.github.razordevs.deep_aether.world.structure.processor.DAStructureProcessor;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources.PathResourcesSupplier;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry.InteractionInformation;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

@Mod("deep_aether")
public class DeepAether {
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final String MODID = "deep_aether";
   public static final String MOD_VERSION = "1.1.5.1";
   public static final String LOST_AETHER_CONTENT = "lost_aether_content";
   public static final String AETHER_GENESIS = "aether_genesis";
   public static final String AETHER_REDUX = "aether_redux";
   public static final String ANCIENT_AETHER = "ancient_aether";
   public static final String EMISSIVITY = "aether_emissivity";
   public static final String PROTECT_YOUR_MOA = "aether_protect_your_moa";
   public static final String TREASURE_REFORGING = "aether_treasure_reforging";
   public static final String BEYOND_PARITY = "aether_beyond_parity";
   private static final Calendar CALENDER = Calendar.getInstance();
   public static final boolean IS_HALLOWEEN = CALENDER.get(2) == 9 && CALENDER.get(5) > 20 || CALENDER.get(2) == 10 && CALENDER.get(5) < 10;

   public static boolean IsHalloweenContentEnabled() {
      return IS_HALLOWEEN || (Boolean)DeepAetherConfig.SERVER.always_enable_halloween_content.get();
   }

   public DeepAether(ModContainer mod, IEventBus bus, Dist dist) {
      bus.addListener(this::dataSetup);
      bus.addListener(this::commonSetup);
      bus.addListener(this::registerCapabilities);
      bus.addListener(this::registerPackets);
      bus.addListener(this::addAetherAdditionalResourcesPack);
      DABlocks.BLOCKS.register(bus);
      DAItems.ITEMS.register(bus);
      DAParticles.PARTICLE_TYPES.register(bus);
      DAEntities.ENTITY_TYPES.register(bus);
      DASounds.SOUNDS.register(bus);
      DAFluids.FLUIDS.register(bus);
      DAFluidTypes.FLUID_TYPES.register(bus);
      DADecoratorType.REGISTRY.register(bus);
      DABlockEntityTypes.BLOCK_ENTITY_TYPES.register(bus);
      DAFoliagePlacers.FOLIAGE_PLACERS.register(bus);
      DARootPlacers.ROOT_PLACERS.register(bus);
      DaTrunkPlacerTypes.TRUNK_PLACERS.register(bus);
      DAFeatures.FEATURES.register(bus);
      DAGlobalLootModifiers.LOOT_MODIFIERS.register(bus);
      DAMobEffects.EFFECTS.register(bus);
      DAPotions.POTIONS.register(bus);
      DARecipeTypes.RECIPE_TYPES.register(bus);
      DARecipeSerializers.RECIPE_SERIALIZERS.register(bus);
      DAMenuTypes.MENUS.register(bus);
      DAAdvancementTriggers.TRIGGERS.register(bus);
      DAAttachments.ATTACHMENTS.register(bus);
      DAPlacementModifiers.PLACEMENT_MODIFIERS.register(bus);
      DAStructureTypes.STRUCTURE_TYPES.register(bus);
      DAStructurePieceTypes.STRUCTURE_PIECE_TYPES.register(bus);
      DAArmorMaterials.ARMOR_MATERIALS.register(bus);
      DAStructureProcessor.STRUCTURE_PROCESSOR_TYPES.register(bus);
      DADataComponentTypes.DATA_COMPONENT_TYPES.register(bus);
      DABlocks.registerWoodTypes();
      mod.registerConfig(Type.COMMON, DeepAetherConfig.COMMON_SPEC);
      mod.registerConfig(Type.CLIENT, DeepAetherConfig.CLIENT_SPEC);
      mod.registerConfig(Type.SERVER, DeepAetherConfig.SERVER_SPEC);
      if (dist == Dist.CLIENT) {
         bus.addListener(DARecipeCategories::registerRecipeCategories);
      }
   }

   public void registerPackets(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("deep_aether").versioned("1.0.0").optional();
      registrar.playBidirectional(DAPlayerSyncPacket.TYPE, DAPlayerSyncPacket.STREAM_CODEC, DAPlayerSyncPacket::execute);
   }

   public void dataSetup(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      ExistingFileHelper fileHelper = event.getExistingFileHelper();
      PackOutput packOutput = generator.getPackOutput();
      generator.addProvider(event.includeClient(), new DABlockstateData(packOutput, fileHelper));
      generator.addProvider(event.includeClient(), new DAItemModelData(packOutput, fileHelper));
      DatapackBuiltinEntriesProvider datapackProvider = new DARegistryDataGenerator(packOutput, event.getLookupProvider());
      CompletableFuture<Provider> lookupProvider = datapackProvider.getRegistryProvider();
      generator.addProvider(event.includeServer(), datapackProvider);
      generator.addProvider(event.includeServer(), new DARecipeData(packOutput, lookupProvider));
      DABlockTagData blockTags = new DABlockTagData(packOutput, lookupProvider, fileHelper);
      generator.addProvider(event.includeServer(), blockTags);
      generator.addProvider(event.includeServer(), new DAItemTagData(packOutput, lookupProvider, blockTags.contentsGetter(), fileHelper));
      generator.addProvider(event.includeServer(), new DABiomeTagData(packOutput, lookupProvider, fileHelper));
      generator.addProvider(event.includeServer(), new DAFluidTagData(packOutput, lookupProvider, fileHelper));
      generator.addProvider(event.includeServer(), new DAEntityTagData(packOutput, lookupProvider, fileHelper));
      generator.addProvider(event.includeServer(), new DADamageTypeTags(packOutput, lookupProvider, fileHelper));
      generator.addProvider(event.includeServer(), new DALootDataProvider(packOutput, lookupProvider));
      generator.addProvider(event.includeServer(), new DADataMapData(packOutput, lookupProvider));
      generator.addProvider(event.includeServer(), new DALootTableData(packOutput, lookupProvider));
      generator.addProvider(event.includeServer(), new DASoundTagData(packOutput, lookupProvider, fileHelper));
   }

   public void commonSetup(FMLCommonSetupEvent event) {
      Reflection.initialize(new Class[]{DARecipeBookTypes.class});
      this.registerFlawlessBossDrops();
      this.registerFluidInteractions();
      event.enqueueWork(
         () -> {
            DaCauldronInteraction.bootStrap();
            DABlocks.registerPots();
            DABlocks.registerFlammability();
            DAItems.setupBucketReplacements();
            this.registerDispenserBehaviors();
            Regions.register(
               new DARegion(
                  ResourceLocation.fromNamespaceAndPath("deep_aether", "deep_aether"), (Integer)DeepAetherConfig.COMMON.deep_aether_biome_weight.get()
               )
            );
            if (!(Boolean)DeepAetherConfig.COMMON.disable_storm_cloud_and_skyroot_rainforest_biomes.get()) {
               Regions.register(
                  new DARareRegion(
                     ResourceLocation.fromNamespaceAndPath("deep_aether", "rare"), (Integer)DeepAetherConfig.COMMON.storm_cloud_biome_weight.get()
                  )
               );
            }

            SurfaceRuleManager.addSurfaceRules(AetherRuleCategory.THE_AETHER, "deep_aether", DASurfaceData.makeRules());
            DAItems.registerAccessories();
         }
      );
   }

   private void registerFlawlessBossDrops() {
      this.getFlawlessBossDrop(
         (EntityType<?>)AetherEntityTypes.SLIDER.get(), (String)DeepAetherConfig.COMMON.slider_flawless_boss_drop.get(), (Item)DAItems.SLIDER_EYE.get()
      );
      this.getFlawlessBossDrop(
         (EntityType<?>)AetherEntityTypes.VALKYRIE_QUEEN.get(),
         (String)DeepAetherConfig.COMMON.valkyrie_queen_flawless_boss_drop.get(),
         (Item)DAItems.MEDAL_OF_HONOR.get()
      );
      this.getFlawlessBossDrop(
         (EntityType<?>)AetherEntityTypes.SUN_SPIRIT.get(), (String)DeepAetherConfig.COMMON.sun_spirit_flawless_boss_drop.get(), (Item)DAItems.SUN_CORE.get()
      );
      this.getFlawlessBossDrop(
         (EntityType<?>)DAEntities.EOTS_CONTROLLER.get(), (String)DeepAetherConfig.COMMON.eots_flawless_boss_drop.get(), (Item)DAItems.FLOATY_SCARF.get()
      );
      if (ModList.get().isLoaded("aether_genesis")) {
         this.getFlawlessBossDrop(
            (EntityType<?>)GenesisEntityTypes.SENTRY_GUARDIAN.get(),
            (String)DeepAetherConfig.COMMON.slider_flawless_boss_drop.get(),
            (Item)DAItems.SENTRY_ALARM.get()
         );
         this.getFlawlessBossDrop(
            (EntityType<?>)GenesisEntityTypes.SLIDER_HOST_MIMIC.get(),
            (String)DeepAetherConfig.COMMON.slider_flawless_boss_drop.get(),
            (Item)DAItems.MIMIC_EYE.get()
         );
         this.getFlawlessBossDrop(
            (EntityType<?>)GenesisEntityTypes.LABYRINTH_EYE.get(),
            (String)DeepAetherConfig.COMMON.slider_flawless_boss_drop.get(),
            (Item)DAItems.MAGNETIC_COG.get()
         );
      }
   }

   private void registerFluidInteractions() {
      FluidInteractionRegistry.addInteraction(
         (FluidType)DAFluidTypes.POISON_FLUID_TYPE.value(),
         new InteractionInformation((FluidType)NeoForgeMod.WATER_TYPE.value(), state -> ((Block)DABlocks.AERSMOG.get()).defaultBlockState())
      );
      FluidInteractionRegistry.addInteraction(
         (FluidType)DAFluidTypes.POISON_FLUID_TYPE.value(),
         new InteractionInformation((FluidType)NeoForgeMod.LAVA_TYPE.value(), state -> Blocks.CRYING_OBSIDIAN.defaultBlockState())
      );
   }

   public void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerItem(FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), new ItemLike[]{(ItemLike)DAItems.PLACEABLE_POISON_BUCKET.get()});
      event.registerItem(
         FluidHandler.ITEM, (stack, ctx) -> new SkyrootPoisonBucketWrapper(stack), new ItemLike[]{(ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get()}
      );
   }

   private void getFlawlessBossDrop(EntityType<?> type, String string, Item fallBack) {
      if (string.equals("null")) {
         DAGeneralEvents.FLAWLESS_BOSS_DROP.put(type, null);
      } else {
         String[] itemId = string.split(":");
         if (BuiltInRegistries.ITEM.containsKey(ResourceLocation.fromNamespaceAndPath(itemId[0], itemId[1]))) {
            DAGeneralEvents.FLAWLESS_BOSS_DROP.put(type, (Item)BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(itemId[0], itemId[1])));
         } else {
            DAGeneralEvents.FLAWLESS_BOSS_DROP.put(type, fallBack);
            LOGGER.info("Config value " + string + " is referring to a missing item! Resolving to default value");
         }
      }
   }

   private void registerDispenserBehaviors() {
      DispenserBlock.registerBehavior(Items.POTION, DADispenseBehaviors.WATER_BOTTLE_TO_AETHER_MUD_DISPENSE_BEHAVIOR);
      DispenserBlock.registerBehavior((ItemLike)DAItems.PLACEABLE_POISON_BUCKET.get(), DADispenseBehaviors.DEEP_AETHER_BUCKET_PICKUP_DISPENSE_BEHAVIOR);
      DispenserBlock.registerBehavior((ItemLike)DAItems.VIRULENT_QUICKSAND_BUCKET.get(), DADispenseBehaviors.DEEP_AETHER_BUCKET_PICKUP_DISPENSE_BEHAVIOR);
      DispenserBlock.registerBehavior((ItemLike)DAItems.SKYROOT_VIRULENT_QUICKSAND_BUCKET.get(), AetherDispenseBehaviors.SKYROOT_BUCKET_DISPENSE_BEHAVIOR);
      DispenserBlock.registerBehavior((ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get(), DADispenseBehaviors.SKYROOT_POISON_BUCKET_DISPENSE_BEHAVIOR);
   }

   public void addAetherAdditionalResourcesPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES) {
         setupCompatPack(
            "overrides/deep_aether_additional_assets", "Deep Aether Additional Assets", event, PackType.CLIENT_RESOURCES, PackSource.BUILT_IN, false
         );
         setupCompatPack("client/deep_aether_tooltips", "Deep Aether Ability Tooltips", event, PackType.CLIENT_RESOURCES, PackSource.BUILT_IN, false);
         if (ModList.get().isLoaded("aether_emissivity")) {
            setupCompatPack("overrides/deep_aether_emissivity", "Deep Aether Emissivity", event, PackType.CLIENT_RESOURCES, PackSource.BUILT_IN, true);
         }

         if (ModList.get().isLoaded("aether_genesis") || ModList.get().isLoaded("aether_redux")) {
            setupCompatPack(
               "overrides/golden_swet_ball/DAGoldenSwetBallFixClient",
               "Deep Aether Golden Swet Ball Texture Fix",
               event,
               PackType.CLIENT_RESOURCES,
               PackSource.DEFAULT,
               true
            );
         }
      }

      if (event.getPackType() == PackType.SERVER_DATA) {
         if (ModList.get().isLoaded("aether_protect_your_moa")) {
            setupCompatPack("compat_recipes/protect_your_moa_compat", "Deep Aether Protect Your Moa Compat", event);
         }

         if (ModList.get().isLoaded("aether_genesis")) {
            setupCompatPack("overrides/golden_swet_ball/DAGoldenSwetBallAetherGenesisFixData", "Deep Aether Golden Swet Ball Aether Genesis Fix", event);
         } else if (ModList.get().isLoaded("aether_redux")) {
            setupCompatPack("overrides/golden_swet_ball/DAGoldenSwetBallAetherReduxFixData", "Deep Aether Golden Swet Ball Aether Redux Fix", event);
         }

         setupCompatPack("compat_recipes/aether_lost_content_not_compat", "Deep Aether Aerwhale Saddle Recipe", event);
         if (ModList.get().isLoaded("aether_redux")) {
            setupCompatPack("compat_recipes/aether_redux_compat", "Aether Redux Compat", event);
         }

         if (ModList.get().isLoaded("ancient_aether")) {
            setupCompatPack("compat_recipes/ancient_aether_compat", "Ancient Aether Compat", event);
         }

         if (ModList.get().isLoaded("aether_beyond_parity")) {
            setupCompatPack("compat_recipes/beyond_parity_compat", "Beyond Parity Compat", event);
         }
      }
   }

   private static void setupCompatPack(String location, String name, AddPackFindersEvent event) {
      setupCompatPack(location, name, event, PackType.SERVER_DATA, PackSource.SERVER, true);
   }

   private static void setupCompatPack(String location, String name, AddPackFindersEvent event, PackType type, PackSource source, boolean force) {
      Path resourcePath = ModList.get().getModFileById("deep_aether").getFile().findResource(new String[]{"packs/" + location});
      Pack pack = Pack.readMetaAndCreate(
         new PackLocationInfo("builtin/" + location, Component.literal(name), source, Optional.empty()),
         new PathResourcesSupplier(resourcePath),
         type,
         new PackSelectionConfig(force, Position.TOP, false)
      );
      event.addRepositorySource(consumer -> consumer.accept(pack));
   }

   public static ResourceLocation getResource(String resourceName) {
      return ResourceLocation.fromNamespaceAndPath("deep_aether", resourceName);
   }
}

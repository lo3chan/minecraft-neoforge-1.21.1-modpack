package com.aetherteam.aether;

import com.aetherteam.aether.advancement.AetherAdvancementTriggers;
import com.aetherteam.aether.api.AetherAdvancementSoundOverrides;
import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.AetherCauldronInteractions;
import com.aetherteam.aether.block.dispenser.AetherDispenseBehaviors;
import com.aetherteam.aether.block.dispenser.DispenseUsableItemBehavior;
import com.aetherteam.aether.block.dispenser.SkyrootBoatDispenseBehavior;
import com.aetherteam.aether.blockentity.AetherBlockEntityTypes;
import com.aetherteam.aether.blockentity.TreasureChestBlockEntity;
import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.CombinedPackResources;
import com.aetherteam.aether.client.TriviaGenerator;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.command.AetherCommands;
import com.aetherteam.aether.command.SunAltarWhitelist;
import com.aetherteam.aether.data.AetherData;
import com.aetherteam.aether.data.ReloadListeners;
import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.ai.attribute.AetherAttributes;
import com.aetherteam.aether.event.listeners.DimensionListener;
import com.aetherteam.aether.event.listeners.EntityListener;
import com.aetherteam.aether.event.listeners.ItemListener;
import com.aetherteam.aether.event.listeners.PerkListener;
import com.aetherteam.aether.event.listeners.RecipeListener;
import com.aetherteam.aether.event.listeners.abilities.AccessoryAbilityListener;
import com.aetherteam.aether.event.listeners.abilities.ArmorAbilityListener;
import com.aetherteam.aether.event.listeners.abilities.ToolAbilityListener;
import com.aetherteam.aether.event.listeners.abilities.WeaponAbilityListener;
import com.aetherteam.aether.event.listeners.capability.AetherPlayerListener;
import com.aetherteam.aether.event.listeners.capability.AetherTimeListener;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.inventory.AetherRecipeBookTypes;
import com.aetherteam.aether.inventory.menu.AetherMenuTypes;
import com.aetherteam.aether.item.AetherCreativeTabs;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.combat.AetherArmorMaterials;
import com.aetherteam.aether.item.combat.ZaniteSwordItem;
import com.aetherteam.aether.item.combat.loot.FlamingSwordItem;
import com.aetherteam.aether.item.combat.loot.HolySwordItem;
import com.aetherteam.aether.item.combat.loot.PigSlayerItem;
import com.aetherteam.aether.item.components.AetherDataComponents;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketWrapper;
import com.aetherteam.aether.loot.conditions.AetherLootConditions;
import com.aetherteam.aether.loot.functions.AetherLootFunctions;
import com.aetherteam.aether.loot.modifiers.AetherLootModifiers;
import com.aetherteam.aether.network.packet.AetherPlayerSyncPacket;
import com.aetherteam.aether.network.packet.AetherTimeSyncPacket;
import com.aetherteam.aether.network.packet.PhoenixArrowSyncPacket;
import com.aetherteam.aether.network.packet.clientbound.AetherTravelPacket;
import com.aetherteam.aether.network.packet.clientbound.BossInfoPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientDeveloperGlowPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientGrabItemPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientHaloPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientMoaSkinPacket;
import com.aetherteam.aether.network.packet.clientbound.CloudMinionPacket;
import com.aetherteam.aether.network.packet.clientbound.HealthResetPacket;
import com.aetherteam.aether.network.packet.clientbound.LeavingAetherPacket;
import com.aetherteam.aether.network.packet.clientbound.MoaInteractPacket;
import com.aetherteam.aether.network.packet.clientbound.OpenSunAltarPacket;
import com.aetherteam.aether.network.packet.clientbound.PortalInteractPacket;
import com.aetherteam.aether.network.packet.clientbound.PortalTravelSoundPacket;
import com.aetherteam.aether.network.packet.clientbound.QueenDialoguePacket;
import com.aetherteam.aether.network.packet.clientbound.RegisterMoaSkinsPacket;
import com.aetherteam.aether.network.packet.clientbound.RemountAerbunnyPacket;
import com.aetherteam.aether.network.packet.clientbound.SetInvisibilityPacket;
import com.aetherteam.aether.network.packet.clientbound.ToolDebuffPacket;
import com.aetherteam.aether.network.packet.clientbound.ZephyrSnowballHitPacket;
import com.aetherteam.aether.network.packet.serverbound.AerbunnyPuffPacket;
import com.aetherteam.aether.network.packet.serverbound.ClearItemPacket;
import com.aetherteam.aether.network.packet.serverbound.HammerProjectileLaunchPacket;
import com.aetherteam.aether.network.packet.serverbound.LoreExistsPacket;
import com.aetherteam.aether.network.packet.serverbound.NpcPlayerInteractPacket;
import com.aetherteam.aether.network.packet.serverbound.OpenAccessoriesPacket;
import com.aetherteam.aether.network.packet.serverbound.OpenInventoryPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerDeveloperGlowPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerHaloPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerMoaSkinPacket;
import com.aetherteam.aether.network.packet.serverbound.StepHeightPacket;
import com.aetherteam.aether.network.packet.serverbound.SunAltarUpdatePacket;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.world.AetherPoi;
import com.aetherteam.aether.world.feature.AetherFeatures;
import com.aetherteam.aether.world.foliageplacer.AetherFoliagePlacerTypes;
import com.aetherteam.aether.world.placementmodifier.AetherPlacementModifiers;
import com.aetherteam.aether.world.processor.AetherStructureProcessors;
import com.aetherteam.aether.world.structure.AetherStructureTypes;
import com.aetherteam.aether.world.structurepiece.AetherStructurePieceTypes;
import com.aetherteam.aether.world.treedecorator.AetherTreeDecoratorTypes;
import com.aetherteam.aether.world.trunkplacer.AetherTrunkPlacerTypes;
import com.google.common.reflect.Reflection;
import com.mojang.logging.LogUtils;
import io.wispforest.accessories.api.slot.UniqueSlotHandling;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.PathPackResources.PathResourcesSupplier;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.world.Container;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.slf4j.Logger;

@Mod("aether")
public class Aether {
   public static final String MODID = "aether";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final Path DIRECTORY = FMLPaths.CONFIGDIR.get().resolve("aether");
   public static final TriviaGenerator TRIVIA_READER = new TriviaGenerator();

   public Aether(ModContainer mod, IEventBus bus, Dist dist) {
      bus.addListener(AetherData::dataSetup);
      bus.addListener(this::commonSetup);
      bus.addListener(this::registerCapabilities);
      bus.addListener(this::registerPackets);
      bus.addListener(this::registerDataMaps);
      bus.addListener(this::packSetup);
      bus.addListener(NewRegistryEvent.class, event -> event.register(AetherAdvancementSoundOverrides.ADVANCEMENT_SOUND_OVERRIDE_REGISTRY));
      bus.addListener(NewRegistry.class, event -> event.dataPackRegistry(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY, MoaType.CODEC, MoaType.CODEC));
      DeferredRegister<?>[] registers = new DeferredRegister[]{
         AetherBlocks.BLOCKS,
         AetherItems.ITEMS,
         AetherEntityTypes.ENTITY_TYPES,
         AetherAttributes.ATTRIBUTES,
         AetherBlockEntityTypes.BLOCK_ENTITY_TYPES,
         AetherMenuTypes.MENU_TYPES,
         AetherEffects.EFFECTS,
         AetherArmorMaterials.ARMOR_MATERIALS,
         AetherParticleTypes.PARTICLES,
         AetherFeatures.FEATURES,
         AetherFoliagePlacerTypes.FOLIAGE_PLACERS,
         AetherPlacementModifiers.PLACEMENT_MODIFIERS,
         AetherTrunkPlacerTypes.TRUNK_PLACERS,
         AetherTreeDecoratorTypes.TREE_DECORATORS,
         AetherPoi.POI,
         AetherStructureTypes.STRUCTURE_TYPES,
         AetherStructurePieceTypes.STRUCTURE_PIECE_TYPES,
         AetherStructureProcessors.STRUCTURE_PROCESSOR_TYPES,
         AetherRecipeTypes.RECIPE_TYPES,
         AetherRecipeSerializers.RECIPE_SERIALIZERS,
         AetherLootFunctions.LOOT_FUNCTION_TYPES,
         AetherLootConditions.LOOT_CONDITION_TYPES,
         AetherLootModifiers.GLOBAL_LOOT_MODIFIERS,
         AetherSoundEvents.SOUNDS,
         AetherGameEvents.GAME_EVENTS,
         AetherCreativeTabs.CREATIVE_MODE_TABS,
         AetherAdvancementSoundOverrides.ADVANCEMENT_SOUND_OVERRIDES,
         AetherDataAttachments.ATTACHMENTS,
         AetherAdvancementTriggers.TRIGGERS,
         AetherDataComponents.DATA_COMPONENT_TYPES
      };

      for (DeferredRegister<?> register : registers) {
         register.register(bus);
      }

      this.eventSetup(bus);
      AetherBlocks.registerWoodTypes();
      DIRECTORY.toFile().mkdirs();
      mod.registerConfig(Type.STARTUP, AetherConfig.STARTUP_SPEC);
      mod.registerConfig(Type.SERVER, AetherConfig.SERVER_SPEC);
      mod.registerConfig(Type.COMMON, AetherConfig.COMMON_SPEC);
      mod.registerConfig(Type.CLIENT, AetherConfig.CLIENT_SPEC);
      if (dist == Dist.CLIENT) {
         mod.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
         AetherClient.clientInit(bus);
      }
   }

   public void commonSetup(FMLCommonSetupEvent event) {
      Reflection.initialize(new Class[]{SunAltarWhitelist.class});
      Reflection.initialize(new Class[]{AetherRecipeBookTypes.class});
      Reflection.initialize(new Class[]{AetherMobCategory.class});
      Reflection.initialize(new Class[]{AetherAdvancementTriggers.class});
      event.enqueueWork(() -> {
         AetherBlocks.registerPots();
         AetherBlocks.registerFlammability();
         AetherBlocks.registerFluidInteractions();
         AetherItems.registerAccessories();
         AetherItems.setupBucketReplacements();
         this.registerDispenserBehaviors();
         this.registerCauldronInteractions();
      });
      UniqueSlotHandling.EVENT.register(AetherAccessorySlots.INSTANCE);
   }

   public void registerPackets(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1.0.0").optional();
      registrar.playToClient(AetherTravelPacket.TYPE, AetherTravelPacket.STREAM_CODEC, AetherTravelPacket::execute);
      registrar.playToClient(BossInfoPacket.Display.TYPE, BossInfoPacket.Display.STREAM_CODEC, BossInfoPacket.Display::execute);
      registrar.playToClient(BossInfoPacket.Remove.TYPE, BossInfoPacket.Remove.STREAM_CODEC, BossInfoPacket.Remove::execute);
      registrar.playToClient(ClientDeveloperGlowPacket.Apply.TYPE, ClientDeveloperGlowPacket.Apply.STREAM_CODEC, ClientDeveloperGlowPacket.Apply::execute);
      registrar.playToClient(ClientDeveloperGlowPacket.Remove.TYPE, ClientDeveloperGlowPacket.Remove.STREAM_CODEC, ClientDeveloperGlowPacket.Remove::execute);
      registrar.playToClient(ClientDeveloperGlowPacket.Sync.TYPE, ClientDeveloperGlowPacket.Sync.STREAM_CODEC, ClientDeveloperGlowPacket.Sync::execute);
      registrar.playToClient(ClientGrabItemPacket.TYPE, ClientGrabItemPacket.STREAM_CODEC, ClientGrabItemPacket::execute);
      registrar.playToClient(ClientHaloPacket.Apply.TYPE, ClientHaloPacket.Apply.STREAM_CODEC, ClientHaloPacket.Apply::execute);
      registrar.playToClient(ClientHaloPacket.Remove.TYPE, ClientHaloPacket.Remove.STREAM_CODEC, ClientHaloPacket.Remove::execute);
      registrar.playToClient(ClientHaloPacket.Sync.TYPE, ClientHaloPacket.Sync.STREAM_CODEC, ClientHaloPacket.Sync::execute);
      registrar.playToClient(ClientMoaSkinPacket.Apply.TYPE, ClientMoaSkinPacket.Apply.STREAM_CODEC, ClientMoaSkinPacket.Apply::execute);
      registrar.playToClient(ClientMoaSkinPacket.Remove.TYPE, ClientMoaSkinPacket.Remove.STREAM_CODEC, ClientMoaSkinPacket.Remove::execute);
      registrar.playToClient(ClientMoaSkinPacket.Sync.TYPE, ClientMoaSkinPacket.Sync.STREAM_CODEC, ClientMoaSkinPacket.Sync::execute);
      registrar.playToClient(CloudMinionPacket.TYPE, CloudMinionPacket.STREAM_CODEC, CloudMinionPacket::execute);
      registrar.playToClient(HealthResetPacket.TYPE, HealthResetPacket.STREAM_CODEC, HealthResetPacket::execute);
      registrar.playToClient(LeavingAetherPacket.TYPE, LeavingAetherPacket.STREAM_CODEC, LeavingAetherPacket::execute);
      registrar.playToClient(MoaInteractPacket.TYPE, MoaInteractPacket.STREAM_CODEC, MoaInteractPacket::execute);
      registrar.playToClient(OpenSunAltarPacket.TYPE, OpenSunAltarPacket.STREAM_CODEC, OpenSunAltarPacket::execute);
      registrar.playToClient(PortalInteractPacket.TYPE, PortalInteractPacket.STREAM_CODEC, PortalInteractPacket::execute);
      registrar.playToClient(PortalTravelSoundPacket.TYPE, PortalTravelSoundPacket.STREAM_CODEC, PortalTravelSoundPacket::execute);
      registrar.playToClient(QueenDialoguePacket.TYPE, QueenDialoguePacket.STREAM_CODEC, QueenDialoguePacket::execute);
      registrar.playToClient(RegisterMoaSkinsPacket.TYPE, RegisterMoaSkinsPacket.STREAM_CODEC, RegisterMoaSkinsPacket::execute);
      registrar.playToClient(RemountAerbunnyPacket.TYPE, RemountAerbunnyPacket.STREAM_CODEC, RemountAerbunnyPacket::execute);
      registrar.playToClient(SetInvisibilityPacket.TYPE, SetInvisibilityPacket.STREAM_CODEC, SetInvisibilityPacket::execute);
      registrar.playToClient(ToolDebuffPacket.TYPE, ToolDebuffPacket.STREAM_CODEC, ToolDebuffPacket::execute);
      registrar.playToClient(ZephyrSnowballHitPacket.TYPE, ZephyrSnowballHitPacket.STREAM_CODEC, ZephyrSnowballHitPacket::execute);
      registrar.playToServer(AerbunnyPuffPacket.TYPE, AerbunnyPuffPacket.STREAM_CODEC, AerbunnyPuffPacket::execute);
      registrar.playToServer(ClearItemPacket.TYPE, ClearItemPacket.STREAM_CODEC, ClearItemPacket::execute);
      registrar.playToServer(HammerProjectileLaunchPacket.TYPE, HammerProjectileLaunchPacket.STREAM_CODEC, HammerProjectileLaunchPacket::execute);
      registrar.playToServer(LoreExistsPacket.TYPE, LoreExistsPacket.STREAM_CODEC, LoreExistsPacket::execute);
      registrar.playToServer(NpcPlayerInteractPacket.TYPE, NpcPlayerInteractPacket.STREAM_CODEC, NpcPlayerInteractPacket::execute);
      registrar.playToServer(OpenAccessoriesPacket.TYPE, OpenAccessoriesPacket.STREAM_CODEC, OpenAccessoriesPacket::execute);
      registrar.playToServer(OpenInventoryPacket.TYPE, OpenInventoryPacket.STREAM_CODEC, OpenInventoryPacket::execute);
      registrar.playToServer(ServerDeveloperGlowPacket.Apply.TYPE, ServerDeveloperGlowPacket.Apply.STREAM_CODEC, ServerDeveloperGlowPacket.Apply::execute);
      registrar.playToServer(ServerDeveloperGlowPacket.Remove.TYPE, ServerDeveloperGlowPacket.Remove.STREAM_CODEC, ServerDeveloperGlowPacket.Remove::execute);
      registrar.playToServer(ServerHaloPacket.Apply.TYPE, ServerHaloPacket.Apply.STREAM_CODEC, ServerHaloPacket.Apply::execute);
      registrar.playToServer(ServerHaloPacket.Remove.TYPE, ServerHaloPacket.Remove.STREAM_CODEC, ServerHaloPacket.Remove::execute);
      registrar.playToServer(ServerMoaSkinPacket.Apply.TYPE, ServerMoaSkinPacket.Apply.STREAM_CODEC, ServerMoaSkinPacket.Apply::execute);
      registrar.playToServer(ServerMoaSkinPacket.Remove.TYPE, ServerMoaSkinPacket.Remove.STREAM_CODEC, ServerMoaSkinPacket.Remove::execute);
      registrar.playToServer(StepHeightPacket.TYPE, StepHeightPacket.STREAM_CODEC, StepHeightPacket::execute);
      registrar.playToServer(SunAltarUpdatePacket.TYPE, SunAltarUpdatePacket.STREAM_CODEC, SunAltarUpdatePacket::execute);
      registrar.playBidirectional(AetherPlayerSyncPacket.TYPE, AetherPlayerSyncPacket.STREAM_CODEC, AetherPlayerSyncPacket::execute);
      registrar.playBidirectional(AetherTimeSyncPacket.TYPE, AetherTimeSyncPacket.STREAM_CODEC, AetherTimeSyncPacket::execute);
      registrar.playBidirectional(PhoenixArrowSyncPacket.TYPE, PhoenixArrowSyncPacket.STREAM_CODEC, PhoenixArrowSyncPacket::execute);
   }

   public void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlock(ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> {
         final TreasureChestBlockEntity entity = (TreasureChestBlockEntity)blockEntity;
         if (!(state.getBlock() instanceof ChestBlock)) {
            return new InvWrapper(entity) {
               public ItemStack extractItem(int slot, int amount, boolean simulate) {
                  return entity.getLocked() ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
               }
            };
         } else {
            Container inv = ChestBlock.getContainer((ChestBlock)state.getBlock(), state, level, pos, true);
            return new InvWrapper((Container)(inv == null ? entity : inv));
         }
      }, new Block[]{(Block)AetherBlocks.TREASURE_CHEST.get()});
      event.registerBlockEntity(
         ItemHandler.BLOCK,
         (BlockEntityType)AetherBlockEntityTypes.INCUBATOR.get(),
         (incubator, side) -> (IItemHandler)(side == null ? new InvWrapper(incubator) : new SidedInvWrapper(incubator, side))
      );

      for (Item item : BuiltInRegistries.ITEM) {
         if (item.getClass() == SkyrootBucketItem.class) {
            event.registerItem(FluidHandler.ITEM, (stack, ctx) -> new SkyrootBucketWrapper(stack), new ItemLike[]{item});
         }
      }

      if (NeoForgeMod.MILK.isBound()) {
         event.registerItem(FluidHandler.ITEM, (stack, ctx) -> new SkyrootBucketWrapper(stack), new ItemLike[]{AetherItems.SKYROOT_MILK_BUCKET});
      }
   }

   public void registerDataMaps(RegisterDataMapTypesEvent event) {
      event.register(AetherDataMaps.ALTAR_FUEL);
      event.register(AetherDataMaps.FREEZER_FUEL);
      event.register(AetherDataMaps.INCUBATOR_FUEL);
   }

   public void packSetup(AddPackFindersEvent event) {
      this.setupReleasePack(event);
      this.setupBetaPack(event);
      this.setupCTMFixPack(event);
      this.setupTipsPack(event);
      this.setupColorblindPack(event);
      this.setupTooltipsPack(event);
      this.setupImmersivePortalsPack(event);
      this.setupAccessoriesPack(event);
      this.setupAccessoriesOverridePack(event);
      this.setupTemporaryFreezingPack(event);
      this.setupRuinedPortalPack(event);
   }

   private void setupReleasePack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/classic_125"});
         this.createCombinedPack(event, resourcePath, "builtin/aether_125_art", "pack.aether.125.title", "pack.aether.125.description");
      }
   }

   private void setupBetaPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/classic_b173"});
         this.createCombinedPack(event, resourcePath, "builtin/aether_b173_art", "pack.aether.b173.title", "pack.aether.b173.description");
      }
   }

   private void createCombinedPack(AddPackFindersEvent event, Path sourcePath, String name, String title, String description) {
      PackLocationInfo locationInfo = new PackLocationInfo(name, Component.translatable(title), PackSource.BUILT_IN, Optional.empty());
      Path baseResourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/classic_base"});
      PathPackResources basePack = new PathPackResources(locationInfo, baseResourcePath);
      PathPackResources pack = new PathPackResources(locationInfo, sourcePath);
      List<PathPackResources> mergedPacks = List.of(pack, basePack);
      ResourcesSupplier resourcesSupplier = new CombinedPackResources.CombinedResourcesSupplier(
         new PackMetadataSection(Component.translatable(description), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)),
         mergedPacks,
         sourcePath
      );
      Metadata metadata = Pack.readPackMetadata(locationInfo, resourcesSupplier, SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
      if (metadata != null) {
         event.addRepositorySource(
            source -> source.accept(new Pack(locationInfo, resourcesSupplier, metadata, new PackSelectionConfig(false, Position.TOP, false)))
         );
      }
   }

   private void setupCTMFixPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES && ModList.get().isLoaded("ctm")) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/ctm_fix"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.ctm.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo("builtin/aether_ctm_fix", Component.translatable("pack.aether.ctm.title"), PackSource.BUILT_IN, Optional.empty()),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true),
                  new PackSelectionConfig(true, Position.TOP, false)
               )
            )
         );
      }
   }

   private void setupTipsPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES && ModList.get().isLoaded("tipsmod") && (Boolean)AetherConfig.STARTUP.enable_trivia.get()) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/tips"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.tips.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo("builtin/aether_tips", Component.translatable("pack.aether.tips.title"), PackSource.BUILT_IN, Optional.empty()),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), false),
                  new PackSelectionConfig(false, Position.TOP, false)
               )
            )
         );
      }
   }

   private void setupColorblindPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/colorblind"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.colorblind.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo(
                     "builtin/aether_colorblind", Component.translatable("pack.aether.colorblind.title"), PackSource.BUILT_IN, Optional.empty()
                  ),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), false),
                  new PackSelectionConfig(false, Position.TOP, false)
               )
            )
         );
      }
   }

   private void setupTooltipsPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/tooltips"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.tooltips.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo("builtin/aether_tooltips", Component.translatable("pack.aether.tooltips.title"), PackSource.BUILT_IN, Optional.empty()),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), false),
                  new PackSelectionConfig(false, Position.TOP, false)
               )
            )
         );
      }
   }

   private void setupImmersivePortalsPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.SERVER_DATA
         && ModList.get().isLoaded("immersive_portals_core")
         && (Boolean)AetherConfig.COMMON.enable_immersive_portals_compatibility.get()) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/imm_ptl_compat"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.imm_ptl_compat.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo(
                     "builtin/aether_imm_ptl_compat", Component.translatable("pack.aether.imm_ptl_compat.title"), PackSource.BUILT_IN, Optional.empty()
                  ),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true),
                  new PackSelectionConfig(true, Position.TOP, false)
               )
            )
         );
      }
   }

   private void setupAccessoriesPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.SERVER_DATA && !(Boolean)AetherConfig.COMMON.use_default_accessories_menu.get()) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/accessories"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.aether_accessories.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo(
                     "builtin/aether_accessories", Component.translatable("pack.aether.aether_accessories.title"), PackSource.BUILT_IN, Optional.empty()
                  ),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true),
                  new PackSelectionConfig(true, Position.TOP, false)
               )
            )
         );
      }
   }

   private void setupAccessoriesOverridePack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.SERVER_DATA && (Boolean)AetherConfig.COMMON.use_default_accessories_menu.get()) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/accessories_override"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.default_accessories.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo(
                     "builtin/aether_accessories_override",
                     Component.translatable("pack.aether.default_accessories.title"),
                     PackSource.BUILT_IN,
                     Optional.empty()
                  ),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true),
                  new PackSelectionConfig(true, Position.TOP, false)
               )
            )
         );
      }
   }

   private void setupTemporaryFreezingPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.SERVER_DATA) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/temporary_freezing"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.freezing.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo(
                     "builtin/aether_temporary_freezing",
                     Component.translatable("pack.aether.freezing.title"),
                     create(decorateWithSource("pack.source.builtin"), (Boolean)AetherConfig.COMMON.add_temporary_freezing_automatically.get()),
                     Optional.empty()
                  ),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), false),
                  new PackSelectionConfig(false, Position.TOP, false)
               )
            )
         );
      }
   }

   private void setupRuinedPortalPack(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.SERVER_DATA) {
         Path resourcePath = ModList.get().getModFileById("aether").getFile().findResource(new String[]{"packs/ruined_portal"});
         PackMetadataSection metadata = new PackMetadataSection(
            Component.translatable("pack.aether.ruined_portal.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA)
         );
         event.addRepositorySource(
            source -> source.accept(
               new Pack(
                  new PackLocationInfo(
                     "builtin/aether_ruined_portal",
                     Component.translatable("pack.aether.ruined_portal.title"),
                     create(decorateWithSource("pack.source.builtin"), (Boolean)AetherConfig.COMMON.add_ruined_portal_automatically.get()),
                     Optional.empty()
                  ),
                  new PathResourcesSupplier(resourcePath),
                  new Metadata(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), false),
                  new PackSelectionConfig(false, Position.TOP, false)
               )
            )
         );
      }
   }

   public void eventSetup(IEventBus neoBus) {
      IEventBus bus = NeoForge.EVENT_BUS;
      AccessoryAbilityListener.listen(bus);
      ArmorAbilityListener.listen(bus);
      ToolAbilityListener.listen(bus);
      WeaponAbilityListener.listen(bus);
      AetherPlayerListener.listen(bus);
      AetherTimeListener.listen(bus);
      DimensionListener.listen(bus);
      EntityListener.listen(bus);
      ItemListener.listen(bus);
      PerkListener.listen(bus);
      RecipeListener.listen(bus);
      bus.addListener(AetherCommands::registerCommands);
      bus.addListener(ReloadListeners::reloadListenerSetup);
      bus.addListener(ZaniteSwordItem::onModifyAttributes);
      bus.addListener(FlamingSwordItem::onLivingDamage);
      bus.addListener(HolySwordItem::onLivingDamage);
      bus.addListener(PigSlayerItem::onLivingDamage);
      neoBus.addListener(AetherCreativeTabs::buildCreativeModeTabs);
      neoBus.addListener(AetherEntityTypes::registerSpawnPlacements);
      neoBus.addListener(AetherEntityTypes::registerEntityAttributes);
   }

   static PackSource create(final UnaryOperator<Component> decorator, final boolean shouldAddAutomatically) {
      return new PackSource() {
         public Component decorate(Component component) {
            return decorator.apply(component);
         }

         public boolean shouldAddAutomatically() {
            return shouldAddAutomatically;
         }
      };
   }

   private static UnaryOperator<Component> decorateWithSource(String translationKey) {
      Component component = Component.translatable(translationKey);
      return name -> Component.translatable("pack.nameAndSource", new Object[]{name, component}).withStyle(ChatFormatting.GRAY);
   }

   private void registerDispenserBehaviors() {
      DispenserBlock.registerBehavior((ItemLike)AetherItems.GOLDEN_DART.get(), new ProjectileDispenseBehavior((Item)AetherItems.GOLDEN_DART.get()));
      DispenserBlock.registerBehavior((ItemLike)AetherItems.POISON_DART.get(), new ProjectileDispenseBehavior((Item)AetherItems.POISON_DART.get()));
      DispenserBlock.registerBehavior((ItemLike)AetherItems.ENCHANTED_DART.get(), new ProjectileDispenseBehavior((Item)AetherItems.ENCHANTED_DART.get()));
      DispenserBlock.registerBehavior((ItemLike)AetherItems.LIGHTNING_KNIFE.get(), new ProjectileDispenseBehavior((Item)AetherItems.LIGHTNING_KNIFE.get()));
      DispenserBlock.registerBehavior((ItemLike)AetherItems.HAMMER_OF_KINGBDOGZ.get(), AetherDispenseBehaviors.DISPENSE_KINGBDOGZ_HAMMER_BEHAVIOR);
      DispenserBlock.registerBehavior((ItemLike)AetherItems.SKYROOT_WATER_BUCKET.get(), AetherDispenseBehaviors.SKYROOT_BUCKET_DISPENSE_BEHAVIOR);
      DispenserBlock.registerBehavior((ItemLike)AetherItems.SKYROOT_BUCKET.get(), AetherDispenseBehaviors.SKYROOT_BUCKET_PICKUP_BEHAVIOR);
      DispenserBlock.registerBehavior(
         (ItemLike)AetherItems.AMBROSIUM_SHARD.get(), new DispenseUsableItemBehavior((RecipeType)AetherRecipeTypes.AMBROSIUM_ENCHANTING.get())
      );
      DispenserBlock.registerBehavior(
         (ItemLike)AetherItems.SWET_BALL.get(), new DispenseUsableItemBehavior((RecipeType)AetherRecipeTypes.SWET_BALL_CONVERSION.get())
      );
      DispenserBlock.registerBehavior((ItemLike)AetherItems.SKYROOT_BOAT.get(), new SkyrootBoatDispenseBehavior());
      DispenserBlock.registerBehavior((ItemLike)AetherItems.SKYROOT_CHEST_BOAT.get(), new SkyrootBoatDispenseBehavior(true));
   }

   private void registerCauldronInteractions() {
      CauldronInteraction.EMPTY.map().put((Item)AetherItems.SKYROOT_WATER_BUCKET.get(), AetherCauldronInteractions.FILL_WATER);
      CauldronInteraction.WATER.map().put((Item)AetherItems.SKYROOT_WATER_BUCKET.get(), AetherCauldronInteractions.FILL_WATER);
      CauldronInteraction.LAVA.map().put((Item)AetherItems.SKYROOT_WATER_BUCKET.get(), AetherCauldronInteractions.FILL_WATER);
      CauldronInteraction.POWDER_SNOW.map().put((Item)AetherItems.SKYROOT_WATER_BUCKET.get(), AetherCauldronInteractions.FILL_WATER);
      CauldronInteraction.EMPTY.map().put((Item)AetherItems.SKYROOT_POWDER_SNOW_BUCKET.get(), AetherCauldronInteractions.FILL_POWDER_SNOW);
      CauldronInteraction.WATER.map().put((Item)AetherItems.SKYROOT_POWDER_SNOW_BUCKET.get(), AetherCauldronInteractions.FILL_POWDER_SNOW);
      CauldronInteraction.LAVA.map().put((Item)AetherItems.SKYROOT_POWDER_SNOW_BUCKET.get(), AetherCauldronInteractions.FILL_POWDER_SNOW);
      CauldronInteraction.POWDER_SNOW.map().put((Item)AetherItems.SKYROOT_POWDER_SNOW_BUCKET.get(), AetherCauldronInteractions.FILL_POWDER_SNOW);
      CauldronInteraction.WATER.map().put((Item)AetherItems.SKYROOT_BUCKET.get(), AetherCauldronInteractions.EMPTY_WATER);
      CauldronInteraction.POWDER_SNOW.map().put((Item)AetherItems.SKYROOT_BUCKET.get(), AetherCauldronInteractions.EMPTY_POWDER_SNOW);
      CauldronInteraction.WATER.map().put((Item)AetherItems.LEATHER_GLOVES.get(), CauldronInteraction.DYED_ITEM);
      CauldronInteraction.WATER.map().put((Item)AetherItems.RED_CAPE.get(), AetherCauldronInteractions.CAPE);
      CauldronInteraction.WATER.map().put((Item)AetherItems.BLUE_CAPE.get(), AetherCauldronInteractions.CAPE);
      CauldronInteraction.WATER.map().put((Item)AetherItems.YELLOW_CAPE.get(), AetherCauldronInteractions.CAPE);
   }
}

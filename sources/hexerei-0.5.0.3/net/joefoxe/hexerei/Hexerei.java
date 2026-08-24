package net.joefoxe.hexerei;

import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.joefoxe.hexerei.block.CustomFlintAndSteelDispenserBehavior;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.ModWoodType;
import net.joefoxe.hexerei.client.renderer.CrowPerchRenderer;
import net.joefoxe.hexerei.client.renderer.entity.BroomType;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.compat.CurioCompat;
import net.joefoxe.hexerei.compat.GlassesCurioRender;
import net.joefoxe.hexerei.compat.LightManagerCompat;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.ModContainers;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.data.books.PaintSystemSavedData;
import net.joefoxe.hexerei.data.datagen.ModRecipeProvider;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipes;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.event.ModLootModifiers;
import net.joefoxe.hexerei.events.GlassesZoomKeyPressEvent;
import net.joefoxe.hexerei.events.SageBurningPlateEvent;
import net.joefoxe.hexerei.events.WitchArmorEvent;
import net.joefoxe.hexerei.fluid.ModFluidTypes;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.integration.HexereiModNameTooltipCompat;
import net.joefoxe.hexerei.integration.jei.HexereiJeiCompat;
import net.joefoxe.hexerei.item.ModArmorMaterial;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItemGroup;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.light.LightManager;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiSupporterBenefits;
import net.joefoxe.hexerei.util.ServerProxy;
import net.joefoxe.hexerei.util.SidedProxy;
import net.joefoxe.hexerei.world.biomemods.ModBiomeModifiers;
import net.joefoxe.hexerei.world.gen.ModFeatures;
import net.joefoxe.hexerei.world.processor.ModStructureProcessors;
import net.joefoxe.hexerei.world.structure.ModStructures;
import net.joefoxe.hexerei.world.terrablender.ModRegion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("hexerei")
public class Hexerei {
   public static final String MOD_ID = "hexerei";
   public static boolean curiosLoaded = false;
   public static SidedProxy proxy;
   public static boolean entityClicked = false;
   public static final Logger LOGGER = LogManager.getLogger();
   public static LinkedList<BlockPos> sageBurningPlateTileList = new LinkedList<>();

   public Hexerei(IEventBus eventBus, ModContainer modContainer, Dist dist) {
      proxy = (SidedProxy)(FMLEnvironment.dist.isClient() ? new ClientProxy() : new ServerProxy());
      modContainer.registerConfig(Type.CLIENT, HexConfig.CLIENT_CONFIG, "Hexerei-client.toml");
      modContainer.registerConfig(Type.COMMON, HexConfig.COMMON_CONFIG, "Hexerei-common.toml");
      if (dist.isClient()) {
         NeoForge.EVENT_BUS.addListener(ClientEvents::clientTickEvent);
         NeoForge.EVENT_BUS.addListener(ClientEvents::renderWorldLastEvent);
         eventBus.addListener(ClientEvents::registerMenu);
         eventBus.addListener(ClientEvents::onRegisterShaders);
         eventBus.addListener(ClientEvents::onRegisterClientExtensions);
         eventBus.addListener(ClientEvents::onRegisterAdditionalModels);
         NeoForge.EVENT_BUS.addListener(ClientEvents::tooltipEvent);
         ClientProxy.MODEL_SWAPPER.registerListeners(eventBus);
      }

      ModDataComponents.COMPONENTS.register(eventBus);
      ModArmorMaterial.MATERIALS.register(eventBus);
      ModItems.register(eventBus);
      ModBlocks.register(eventBus);
      ModFluids.register(eventBus);
      ModFluidTypes.register(eventBus);
      ModTileEntities.register(eventBus);
      ModContainers.register(eventBus);
      ModRecipeTypes.register(eventBus);
      ModParticleTypes.PARTICLES.register(eventBus);
      ModFeatures.register(eventBus);
      ModStructureProcessors.DEFERRED_REGISTRY_STRUCTURE_PROCESSOR.register(eventBus);
      ModStructures.DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
      ModSounds.register(eventBus);
      ModEntityTypes.register(eventBus);
      ModBiomeModifiers.register(eventBus);
      HexereiJeiCompat.init();
      ModLootModifiers.init(eventBus);
      HexereiModNameTooltipCompat.init();

      try {
         Thread thread = new Thread(HexereiSupporterBenefits::init);
         thread.setDaemon(true);
         thread.setName("supporter-lookup");
         thread.start();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      eventBus.addListener(this::loadComplete);
      eventBus.addListener(this::setup);
      eventBus.addListener(this::registerSpawnPlacementsEvent);
      eventBus.addListener(this::enqueueIMC);
      eventBus.addListener(this::doClientStuff);
      ModItemGroup.ITEM_GROUP.register(eventBus);
      NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::playerLogin);
      eventBus.addListener(EventPriority.LOWEST, this::gatherData);
      curiosLoaded = ModList.get().isLoaded("curios");
   }

   public void gatherData(GatherDataEvent event) {
      DataGenerator gen = event.getGenerator();
      PackOutput output = gen.getPackOutput();
      CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
      gen.addProvider(true, new ModRecipeProvider(output, lookupProvider));
   }

   public void playerLogin(PlayerLoggedInEvent event) {
      ServerPlayer player = (ServerPlayer)event.getEntity();
      OwlCourierDepotSavedData.get().syncToClient(player);
      BookManager.sendBookPagesToClient(player);
      BookManager.sendBookEntriesToClient(player);
      PaintSystemSavedData.sendToClients();
      WoodcutterRecipes.sendToClient(player);
   }

   public void setupCrowPerchRenderer() {
      NeoForge.EVENT_BUS.register(CrowPerchRenderer.class);
   }

   private void registerSpawnPlacementsEvent(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)ModEntityTypes.CROW.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, Operation.OR
      );
      event.register((EntityType)ModEntityTypes.OWL.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules, Operation.OR);
   }

   private void setup(FMLCommonSetupEvent event) {
      event.enqueueWork(
         () -> {
            DispenserBlock.registerBehavior(
               Items.FLINT_AND_STEEL,
               new CustomFlintAndSteelDispenserBehavior((DispenseItemBehavior)DispenserBlock.DISPENSER_REGISTRY.get(Items.FLINT_AND_STEEL))
            );
            AxeItem.STRIPPABLES = new Builder()
               .putAll(AxeItem.STRIPPABLES)
               .put((Block)ModBlocks.MAHOGANY_LOG.get(), (Block)ModBlocks.STRIPPED_MAHOGANY_LOG.get())
               .put((Block)ModBlocks.MAHOGANY_WOOD.get(), (Block)ModBlocks.STRIPPED_MAHOGANY_WOOD.get())
               .put((Block)ModBlocks.WILLOW_LOG.get(), (Block)ModBlocks.STRIPPED_WILLOW_LOG.get())
               .put((Block)ModBlocks.WILLOW_WOOD.get(), (Block)ModBlocks.STRIPPED_WILLOW_WOOD.get())
               .put((Block)ModBlocks.WITCH_HAZEL_LOG.get(), (Block)ModBlocks.STRIPPED_WITCH_HAZEL_LOG.get())
               .put((Block)ModBlocks.WITCH_HAZEL_WOOD.get(), (Block)ModBlocks.STRIPPED_WITCH_HAZEL_WOOD.get())
               .build();
            WoodType.register(ModWoodType.MAHOGANY);
            WoodType.register(ModWoodType.WILLOW);
            WoodType.register(ModWoodType.WITCH_HAZEL);
            WoodType.register(ModWoodType.POLISHED_MAHOGANY);
            WoodType.register(ModWoodType.POLISHED_WILLOW);
            WoodType.register(ModWoodType.POLISHED_WITCH_HAZEL);
            BroomType.create("mahogany", (Item)ModItems.MAHOGANY_BROOM.get(), 0.8F);
            BroomType.create("willow", (Item)ModItems.WILLOW_BROOM.get(), 0.4F);
            BroomType.create("witch_hazel", (Item)ModItems.WITCH_HAZEL_BROOM.get(), 0.6F);
            LightManager.init();
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.MANDRAKE_PLANT.getId(), ModBlocks.POTTED_MANDRAKE_PLANT);
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.BELLADONNA_PLANT.getId(), ModBlocks.POTTED_BELLADONNA_PLANT);
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.YELLOW_DOCK_BUSH.getId(), ModBlocks.POTTED_YELLOW_DOCK_BUSH);
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.MUGWORT_BUSH.getId(), ModBlocks.POTTED_MUGWORT_BUSH);
         }
      );
      if (ModList.get().isLoaded("terrablender") && (Integer)HexConfig.WILLOW_SWAMP_RARITY.get() > 0) {
         event.enqueueWork(ModRegion::init);
      }
   }

   public static List<String> buildResourceLocations(String resourcePath) throws IOException {
      ResourceLocation loc = ResourceLocation.parse(resourcePath);
      String resourceDirectory = loc.getPath();
      Path basePath = Paths.get("src/main/resources/assets", loc.getNamespace(), resourceDirectory).getParent();

      List var5;
      try (Stream<Path> paths = Files.walk(basePath)) {
         var5 = paths.filter(x$0 -> Files.isRegularFile(x$0)).filter(path -> {
            String fileName = path.getFileName().toString().toLowerCase();
            return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
         }).map(path -> {
            String relativeImagePath = basePath.relativize(path).toString().replace("\\", "/");
            return loc.getNamespace() + ":textures/" + relativeImagePath;
         }).toList();
      }

      return var5;
   }

   private void doClientStuff(FMLClientSetupEvent event) {
      this.setupCrowPerchRenderer();
      event.enqueueWork(
         () -> {
            PageDrawing.pageTextureLocs = new ArrayList<>(
               Minecraft.getInstance()
                  .getResourceManager()
                  .listResources("textures/book/pages", p_345740_ -> p_345740_.getPath().endsWith(".png"))
                  .keySet()
                  .stream()
                  .filter(loc -> loc.getNamespace().equals("hexerei"))
                  .toList()
            );
            PageDrawing.overlayTextureLocs = new ArrayList<>(
               Minecraft.getInstance()
                  .getResourceManager()
                  .listResources("textures/book/page_overlays", p_345740_ -> p_345740_.getPath().endsWith(".png"))
                  .keySet()
                  .stream()
                  .filter(loc -> loc.getNamespace().equals("hexerei"))
                  .toList()
            );
            PageDrawing.overlayTextureLocs.add(null);
            PageDrawing.overlayTextureLocs.add(null);
            PageDrawing.overlayTextureLocs.add(null);
            PageDrawing.overlayTextureLocs.add(null);
            PageDrawing.overlayTextureLocs.add(null);
            PageDrawing.overlayTextureLocs.add(null);
            Sheets.addWoodType(ModWoodType.MAHOGANY);
            Sheets.addWoodType(ModWoodType.WILLOW);
            Sheets.addWoodType(ModWoodType.WITCH_HAZEL);
            Sheets.addWoodType(ModWoodType.POLISHED_MAHOGANY);
            Sheets.addWoodType(ModWoodType.POLISHED_WILLOW);
            Sheets.addWoodType(ModWoodType.POLISHED_WITCH_HAZEL);
            ItemBlockRenderTypes.setRenderLayer((Fluid)ModFluids.QUICKSILVER_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer((Fluid)ModFluids.QUICKSILVER_FLOWING.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer((Fluid)ModFluids.BLOOD_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer((Fluid)ModFluids.BLOOD_FLOWING.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer((Fluid)ModFluids.TALLOW_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer((Fluid)ModFluids.TALLOW_FLOWING.get(), RenderType.translucent());
         }
      );
      if (curiosLoaded) {
         GlassesCurioRender.register();
      }
   }

   private void enqueueIMC(InterModEnqueueEvent event) {
      if (curiosLoaded) {
         CurioCompat.sendIMC();
      }
   }

   private void loadComplete(FMLLoadCompleteEvent event) {
      NeoForge.EVENT_BUS.register(new SageBurningPlateEvent());
      NeoForge.EVENT_BUS.register(new WitchArmorEvent());
      if (FMLEnvironment.dist.isClient()) {
         NeoForge.EVENT_BUS.register(new GlassesZoomKeyPressEvent());
         if (ModList.get().isLoaded("ars_nouveau")) {
            LightManagerCompat.fallbackToArs();
         }
      }
   }

   public static class DynamicRegistries {
      public static RegistryAccess get() {
         return (RegistryAccess)(EffectiveSide.get().isClient()
            ? Hexerei.DynamicRegistries.ClientDynamicRegistries.get()
            : ServerLifecycleHooks.getCurrentServer().registryAccess());
      }

      private static class ClientDynamicRegistries {
         public static RegistryAccess get() {
            return (RegistryAccess)(Minecraft.getInstance().hasSingleplayerServer()
               ? Minecraft.getInstance().getSingleplayerServer().registryAccess()
               : Minecraft.getInstance().level.registryAccess());
         }
      }
   }
}

package vectorwing.farmersdelight;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vectorwing.farmersdelight.client.event.ClientSetupEvents;
import vectorwing.farmersdelight.common.CommonSetup;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.registry.ModAdvancements;
import vectorwing.farmersdelight.common.registry.ModBiomeFeatures;
import vectorwing.farmersdelight.common.registry.ModBiomeModifiers;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModConditionCodecs;
import vectorwing.farmersdelight.common.registry.ModCreativeTabs;
import vectorwing.farmersdelight.common.registry.ModDataComponents;
import vectorwing.farmersdelight.common.registry.ModEffects;
import vectorwing.farmersdelight.common.registry.ModEntityTypes;
import vectorwing.farmersdelight.common.registry.ModIngredientTypes;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModLootFunctions;
import vectorwing.farmersdelight.common.registry.ModLootModifiers;
import vectorwing.farmersdelight.common.registry.ModMenuTypes;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.registry.ModPlacementModifiers;
import vectorwing.farmersdelight.common.registry.ModRecipeSerializers;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.registry.RegistryAliases;
import vectorwing.farmersdelight.common.world.VillageStructures;

@Mod("farmersdelight")
public class FarmersDelight {
   public static final String MODID = "farmersdelight";
   public static final Logger LOGGER = LogManager.getLogger();

   public FarmersDelight(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(CommonSetup::init);
      if (FMLEnvironment.dist.isClient()) {
         modEventBus.addListener(ClientSetupEvents::init);
         modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
      }

      modContainer.registerConfig(Type.COMMON, Configuration.COMMON_CONFIG);
      modContainer.registerConfig(Type.CLIENT, Configuration.CLIENT_CONFIG);
      ModSounds.SOUNDS.register(modEventBus);
      ModBlocks.BLOCKS.register(modEventBus);
      ModEffects.EFFECTS.register(modEventBus);
      ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
      ModItems.ITEMS.register(modEventBus);
      ModDataComponents.DATA_COMPONENTS.register(modEventBus);
      ModDataComponents.ENCHANTMENT_EFFECT_COMPONENTS.register(modEventBus);
      ModEntityTypes.ENTITIES.register(modEventBus);
      ModBlockEntityTypes.TILES.register(modEventBus);
      ModMenuTypes.MENU_TYPES.register(modEventBus);
      ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
      ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
      ModBiomeFeatures.FEATURES.register(modEventBus);
      ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
      ModPlacementModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
      ModBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
      ModLootFunctions.LOOT_FUNCTIONS.register(modEventBus);
      ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
      ModConditionCodecs.CONDITION_CODECS.register(modEventBus);
      ModIngredientTypes.INGREDIENT_TYPES.register(modEventBus);
      ModAdvancements.TRIGGERS.register(modEventBus);
      RegistryAliases.addRegistryAliases();
      NeoForge.EVENT_BUS.addListener(VillageStructures::addNewVillageBuilding);
   }
}

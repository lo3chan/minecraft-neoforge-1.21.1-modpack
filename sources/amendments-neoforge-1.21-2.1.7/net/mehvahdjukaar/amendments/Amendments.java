package net.mehvahdjukaar.amendments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.mehvahdjukaar.amendments.common.FlowerPotHandler;
import net.mehvahdjukaar.amendments.common.WallLanternServerResources;
import net.mehvahdjukaar.amendments.common.network.ModNetwork;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.events.behaviors.CauldronConversion;
import net.mehvahdjukaar.amendments.events.behaviors.InteractEvents;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.SuppCompat;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.FluidContainerList.Category;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper.ExtraPOIStatesEvent;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper.ItemToTabEvent;
import net.mehvahdjukaar.moonlight.api.util.DispenserHelper.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Amendments {
   public static final String MOD_ID = "amendments";
   public static final Logger LOGGER = LogManager.getLogger("Amendments");
   public static final List<String> OLD_MODS = List.of("supplementaries", "carpeted", "betterlily", "betterjukebox");

   public static ResourceLocation res(String name) {
      return ResourceLocation.fromNamespaceAndPath("amendments", name);
   }

   public static void init() {
      Dummy.MOD_LOADED = true;
      CommonConfigs.init();
      ModRegistry.init();
      ModNetwork.init();
      if (PlatHelper.getPhysicalSide().isClient()) {
         ClientConfigs.init();
         AmendmentsClient.init();
      }

      PlatHelper.addCommonSetupAsync(Amendments::setupAsync);
      PlatHelper.addCommonSetup(Amendments::setup);
      PlatHelper.addReloadableCommonSetup(Amendments::onReload);
      RegHelper.registerDynamicResourceProvider(new WallLanternServerResources());
      RegHelper.addDynamicDispenserBehaviorRegistration(Amendments::registerDispenserBehaviors);
      RegHelper.registerSimpleRecipeCondition(res("flag"), CommonConfigs::isFlagOn);
      RegHelper.addItemsToTabsRegistration(Amendments::addItemsToTabs);
      RegHelper.addExtraPOIStatesRegistration(Amendments::addExtraPoiStates);
   }

   private static void addItemsToTabs(ItemToTabEvent itemToTabEvent) {
      if (CommonConfigs.THROWABLE_FIRE_CHARGES.get()) {
         itemToTabEvent.addBefore(CreativeModeTabs.COMBAT, i -> i.is(Items.SNOWBALL), new ItemLike[]{Items.FIRE_CHARGE});
         if (CommonConfigs.DRAGON_CHARGE.get()) {
            itemToTabEvent.addBefore(CreativeModeTabs.COMBAT, i -> i.is(Items.SNOWBALL), new ItemLike[]{(ItemLike)ModRegistry.DRAGON_CHARGE.get()});
         }
      }
   }

   private static void addExtraPoiStates(ExtraPOIStatesEvent event) {
      event.addBlocks(PoiTypes.LEATHERWORKER, List.of((Block)ModRegistry.LIQUID_CAULDRON.get(), ModRegistry.DYE_CAULDRON.get()));
   }

   private static void setup() {
      if (CommonConfigs.INVERSE_POTIONS.get() == null) {
         throw new IllegalStateException("Inverse potions config is null. How??");
      } else {
         if (CompatHandler.SUPPLEMENTARIES) {
            SuppCompat.setup();
         }

         ModRegistry.registerAdditionalPlacements();
      }
   }

   private static void setupAsync() {
      FlowerPotHandler.setup();
      ClientConfigs.setup();
   }

   public static void onReload(RegistryAccess registryAccess, boolean client) {
      InteractEvents.setupOverrides();
      if (client) {
         AmendmentsClient.afterTagSetup();
      }
   }

   private static void registerDispenserBehaviors(Event event) {
      for (SoftFluid f : SoftFluidRegistry.get(event.getRegistryAccess())) {
         registerFluidBehavior(f, event);
      }

      if (CommonConfigs.FIRE_CHARGE_DISPENSER.get() && CommonConfigs.THROWABLE_FIRE_CHARGES.get()) {
         event.register(Items.FIRE_CHARGE, new ProjectileDispenseBehavior(Items.FIRE_CHARGE) {
            protected void playSound(BlockSource source) {
               source.level().levelEvent(1018, source.pos(), 0);
            }
         });
      }

      if (CommonConfigs.DRAGON_CHARGE.get()) {
         event.register(ModRegistry.DRAGON_CHARGE.get(), new ProjectileDispenseBehavior(ModRegistry.DRAGON_CHARGE.get()) {
            protected void playSound(BlockSource source) {
               source.level().levelEvent(1018, source.pos(), 0);
            }
         });
      }
   }

   public static void registerFluidBehavior(SoftFluid f, Event event) {
      Set<Item> itemSet = new HashSet<>();

      for (Category c : f.getContainerList().getCategories()) {
         for (Item full : c.getFilledItems()) {
            if (full != Items.AIR && !itemSet.contains(full)) {
               event.register(new CauldronConversion.DispenserBehavior(full));
               itemSet.add(full);
            }
         }
      }
   }

   public static boolean isSupportingCeiling(BlockPos pos, LevelReader world) {
      return isSupportingCeiling(world.getBlockState(pos), pos, world);
   }

   public static boolean isSupportingCeiling(BlockState upState, BlockPos pos, LevelReader world) {
      return CompatHandler.SUPPLEMENTARIES ? SuppCompat.isSupportingCeiling(upState, pos, world) : Block.canSupportCenter(world, pos, Direction.DOWN);
   }

   public static boolean canConnectDown(BlockState neighborState, LevelAccessor level, BlockPos pos) {
      return CompatHandler.SUPPLEMENTARIES
         ? SuppCompat.canConnectDown(neighborState)
         : neighborState.isFaceSturdy(level, pos, Direction.UP, SupportType.CENTER);
   }
}

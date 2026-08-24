package net.cibernet.alchemancy.registries;

import java.util.Comparator;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class AlchemancyCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "alchemancy");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GENERAL = REGISTRY.register(
      "alchemancy",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("itemGroup.alchemancy"))
         .icon(AlchemancyItems.BLAZEBLOOM::toStack)
         .displayItems((parameters, output) -> {
            output.accept((ItemLike)AlchemancyItems.BLAZEBLOOM.get());
            output.accept((ItemLike)AlchemancyItems.ALCHEMICAL_EXTRACT.get());
            output.accept((ItemLike)AlchemancyItems.ALCHEMANCY_FORGE.get());
            output.accept((ItemLike)AlchemancyItems.INFUSION_PEDESTAL.get());
            output.accept((ItemLike)AlchemancyItems.ALCHEMANCY_CATALYST.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_INGOT.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_SWORD.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_SHOVEL.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_PICKAXE.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_AXE.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_HOE.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_HELMET.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_CHESTPLATE.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_LEGGINGS.get());
            output.accept((ItemLike)AlchemancyItems.LEAD_BOOTS.get());
            output.accept((ItemLike)AlchemancyItems.DREAMSTEEL_INGOT.get());
            output.accept((ItemLike)AlchemancyItems.DREAMSTEEL_NUGGET.get());
            output.accept((ItemLike)AlchemancyItems.DREAMSTEEL_SWORD.get());
            output.accept((ItemLike)AlchemancyItems.DREAMSTEEL_SHOVEL.get());
            output.accept((ItemLike)AlchemancyItems.DREAMSTEEL_PICKAXE.get());
            output.accept((ItemLike)AlchemancyItems.DREAMSTEEL_AXE.get());
            output.accept((ItemLike)AlchemancyItems.DREAMSTEEL_HOE.get());
            output.accept((ItemLike)AlchemancyItems.DREAMSTEEL_BOW.get());
            output.accept((ItemLike)AlchemancyItems.INFUSION_FLASK.get());
            output.accept((ItemLike)AlchemancyItems.BLANK_PEARL.get());
            output.accept((ItemLike)AlchemancyItems.REVEALING_PEARL.get());
            output.accept((ItemLike)AlchemancyItems.PARADOX_PEARL.get());
            output.accept((ItemLike)AlchemancyItems.VOID_PEARL.get());
            output.accept((ItemLike)AlchemancyItems.ENTANGLED_SINGULARITY.get());
            output.accept((ItemLike)AlchemancyItems.MICROSCOPIC_LENS.get());
            output.accept((ItemLike)AlchemancyItems.MACROSCOPIC_LENS.get());
            output.accept((ItemLike)AlchemancyItems.CHROMA_LENS.get());
            output.accept((ItemLike)AlchemancyItems.GLOWING_ORB.get());
            output.accept((ItemLike)AlchemancyItems.IRON_RING.get());
            output.accept((ItemLike)AlchemancyItems.ETERNAL_GLOW_RING.get());
            output.accept((ItemLike)AlchemancyItems.PHASING_RING.get());
            output.accept((ItemLike)AlchemancyItems.UNDYING_RING.get());
            output.accept((ItemLike)AlchemancyItems.FRIENDSHIP_RING.get());
            output.accept((ItemLike)AlchemancyItems.ATTRACTION_RING.get());
            output.accept((ItemLike)AlchemancyItems.VOIDLESS_RING.get());
            output.accept((ItemLike)AlchemancyItems.SPARKLING_BAND.get());
            output.accept((ItemLike)AlchemancyItems.PROPERTY_VISOR.get());
            output.accept((ItemLike)AlchemancyItems.TINTED_GLASSES.get());
            output.accept((ItemLike)AlchemancyItems.NIMBUS_BELT.get());
            output.accept((ItemLike)AlchemancyItems.CRYSTAL_STORM_BELT.get());
            output.accept((ItemLike)AlchemancyItems.SHIFTING_LIGHTNING_BELT.get());
            output.accept((ItemLike)AlchemancyItems.FLAMEWAKERS.get());
            output.accept((ItemLike)AlchemancyItems.TIDEWALKER_TREADS.get());
            output.accept((ItemLike)AlchemancyItems.HARDLIGHT_STEPS.get());
            output.accept((ItemLike)AlchemancyItems.MECHANICAL_BOOTS.get());
            output.accept((ItemLike)AlchemancyItems.LEADEN_APPLE.get());
            output.accept((ItemLike)AlchemancyItems.LEADEN_CLOTH.get());
            output.accept((ItemLike)AlchemancyItems.DIVINE_CLOTH.get());
            output.accept((ItemLike)AlchemancyItems.WAYWARD_MEDALLION.get());
            output.accept((ItemLike)AlchemancyItems.VAULT_LOCKPICK.get());
            output.accept((ItemLike)AlchemancyItems.BINDING_KEY.get());
            output.accept((ItemLike)AlchemancyItems.SPINNER_SPANNER.get());
            output.accept((ItemLike)AlchemancyItems.QUANTUM_SPANNER.get());
            output.accept((ItemLike)AlchemancyItems.PHANTOM_MEMBRANE_BLOCK.get());
            output.accept((ItemLike)AlchemancyItems.GUST_BASKET.get());
            output.accept((ItemLike)AlchemancyItems.FLAT_HOPPER.get());
            output.accept((ItemLike)AlchemancyItems.CHROMACHINE.get());
            output.accept((ItemLike)AlchemancyItems.ROCKET_POWERED_HAMMER.get());
            output.accept((ItemLike)AlchemancyItems.TELEKINETIC_GLOVE.get());
            output.accept((ItemLike)AlchemancyItems.HOME_RUN_BAT.get());
            output.accept((ItemLike)AlchemancyItems.FERAL_BLADE.get());
            output.accept((ItemLike)AlchemancyItems.HOT_ROD.get());
            output.accept((ItemLike)AlchemancyItems.MOLTEN_CORE_PERFORATOR.get());
            output.accept((ItemLike)AlchemancyItems.BLACK_HOLE_PICKAXE.get());
            output.accept((ItemLike)AlchemancyItems.BLACK_HOLE_AXE.get());
            output.accept((ItemLike)AlchemancyItems.BLACK_HOLE_SHOVEL.get());
            output.accept((ItemLike)AlchemancyItems.BLACK_HOLE_HOE.get());
            output.accept((ItemLike)AlchemancyItems.POCKET_BLACK_HOLE.get());
            output.accept((ItemLike)AlchemancyItems.INFUSION_CODEX.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PROPERTIES = REGISTRY.register(
      "alchemancy_properties",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("itemGroup.alchemancy_properties"))
         .withTabsBefore(new ResourceKey[]{GENERAL.getKey()})
         .icon(AlchemancyItems.PROPERTY_CAPSULE::toStack)
         .displayItems(
            (parameters, output) -> {
               for (DeferredHolder<Property, ? extends Property> entry : AlchemancyProperties.REGISTRY
                  .getEntries()
                  .stream()
                  .sorted(Comparator.comparing(DeferredHolder::getKey))
                  .toList()) {
                  output.acceptAll(((Property)entry.value()).populateCreativeTab(AlchemancyItems.PROPERTY_CAPSULE, entry));
               }
            }
         )
         .build()
   );

   @SubscribeEvent
   public static void addCreative(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey().equals(CreativeModeTabs.FOOD_AND_DRINKS)) {
         event.insertAfter(Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), AlchemancyItems.LEADEN_APPLE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
         event.insertAfter(Items.GOLDEN_SWORD.getDefaultInstance(), AlchemancyItems.LEAD_SWORD.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(Items.NETHERITE_SWORD.getDefaultInstance(), AlchemancyItems.DREAMSTEEL_SWORD.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.DREAMSTEEL_SWORD.toStack(), AlchemancyItems.FERAL_BLADE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.FERAL_BLADE.toStack(), AlchemancyItems.HOT_ROD.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(Items.BOW.getDefaultInstance(), AlchemancyItems.DREAMSTEEL_BOW.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(Items.MACE.getDefaultInstance(), AlchemancyItems.ROCKET_POWERED_HAMMER.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.ROCKET_POWERED_HAMMER.toStack(), AlchemancyItems.HOME_RUN_BAT.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(Items.GOLDEN_BOOTS.getDefaultInstance(), AlchemancyItems.LEAD_HELMET.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.LEAD_HELMET.toStack(), AlchemancyItems.LEAD_CHESTPLATE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.LEAD_CHESTPLATE.toStack(), AlchemancyItems.LEAD_LEGGINGS.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.LEAD_LEGGINGS.toStack(), AlchemancyItems.LEAD_BOOTS.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
      } else if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
         event.insertAfter(Items.GOLDEN_HOE.getDefaultInstance(), AlchemancyItems.LEAD_SHOVEL.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.LEAD_SHOVEL.toStack(), AlchemancyItems.LEAD_PICKAXE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.LEAD_PICKAXE.toStack(), AlchemancyItems.LEAD_AXE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.LEAD_AXE.toStack(), AlchemancyItems.LEAD_HOE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(Items.NETHERITE_HOE.getDefaultInstance(), AlchemancyItems.DREAMSTEEL_SHOVEL.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.DREAMSTEEL_SHOVEL.toStack(), AlchemancyItems.DREAMSTEEL_PICKAXE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.DREAMSTEEL_PICKAXE.toStack(), AlchemancyItems.DREAMSTEEL_AXE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.DREAMSTEEL_AXE.toStack(), AlchemancyItems.DREAMSTEEL_HOE.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(AlchemancyItems.DREAMSTEEL_HOE.toStack(), AlchemancyItems.MOLTEN_CORE_PERFORATOR.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
      } else if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) {
         event.insertAfter(Items.GOLD_INGOT.getDefaultInstance(), AlchemancyItems.LEAD_INGOT.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(Items.NETHERITE_INGOT.getDefaultInstance(), AlchemancyItems.DREAMSTEEL_INGOT.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(Items.GOLD_NUGGET.getDefaultInstance(), AlchemancyItems.DREAMSTEEL_NUGGET.toStack(), TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }
}

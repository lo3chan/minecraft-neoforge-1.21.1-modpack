package vazkii.psi.common.item.component;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import vazkii.psi.api.cad.CADStatEvent;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.common.item.base.ModItems;

@EventBusSubscriber(
   modid = "psi"
)
public class DefaultStats {
   public static void registerStats() {
      registerAssemblyStats();
      registerCoreStats();
      registerSocketStats();
      registerBatteryStats();
   }

   public static void registerAssemblyStats() {
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyIron.get(), EnumCADStat.EFFICIENCY, 70);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyIron.get(), EnumCADStat.POTENCY, 100);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyGold.get(), EnumCADStat.EFFICIENCY, 75);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyGold.get(), EnumCADStat.POTENCY, 175);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyPsimetal.get(), EnumCADStat.EFFICIENCY, 85);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyPsimetal.get(), EnumCADStat.POTENCY, 250);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyEbony.get(), EnumCADStat.EFFICIENCY, 90);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyEbony.get(), EnumCADStat.POTENCY, 350);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyIvory.get(), EnumCADStat.EFFICIENCY, 95);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyIvory.get(), EnumCADStat.POTENCY, 320);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyCreative.get(), EnumCADStat.EFFICIENCY, -1);
      ItemCADComponent.addStatToStack((Item)ModItems.cadAssemblyCreative.get(), EnumCADStat.POTENCY, -1);
   }

   public static void registerCoreStats() {
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreBasic.get(), EnumCADStat.COMPLEXITY, 14);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreBasic.get(), EnumCADStat.PROJECTION, 1);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreOverclocked.get(), EnumCADStat.COMPLEXITY, 24);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreOverclocked.get(), EnumCADStat.PROJECTION, 3);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreConductive.get(), EnumCADStat.COMPLEXITY, 20);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreConductive.get(), EnumCADStat.PROJECTION, 4);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreHyperClocked.get(), EnumCADStat.COMPLEXITY, 36);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreHyperClocked.get(), EnumCADStat.PROJECTION, 6);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreRadiative.get(), EnumCADStat.COMPLEXITY, 30);
      ItemCADComponent.addStatToStack((Item)ModItems.cadCoreRadiative.get(), EnumCADStat.PROJECTION, 7);
   }

   public static void registerSocketStats() {
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketBasic.get(), EnumCADStat.BANDWIDTH, 5);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketBasic.get(), EnumCADStat.SOCKETS, 4);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketBasic.get(), EnumCADStat.SAVED_VECTORS, 7);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketSignaling.get(), EnumCADStat.BANDWIDTH, 7);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketSignaling.get(), EnumCADStat.SOCKETS, 6);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketSignaling.get(), EnumCADStat.SAVED_VECTORS, 14);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketLarge.get(), EnumCADStat.BANDWIDTH, 6);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketLarge.get(), EnumCADStat.SOCKETS, 8);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketLarge.get(), EnumCADStat.SAVED_VECTORS, 14);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketTransmissive.get(), EnumCADStat.BANDWIDTH, 9);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketTransmissive.get(), EnumCADStat.SOCKETS, 10);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketTransmissive.get(), EnumCADStat.SAVED_VECTORS, 18);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketHuge.get(), EnumCADStat.BANDWIDTH, 8);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketHuge.get(), EnumCADStat.SOCKETS, 12);
      ItemCADComponent.addStatToStack((Item)ModItems.cadSocketHuge.get(), EnumCADStat.SAVED_VECTORS, 21);
   }

   public static void registerBatteryStats() {
      ItemCADComponent.addStatToStack((Item)ModItems.cadBatteryBasic.get(), EnumCADStat.OVERFLOW, 100);
      ItemCADComponent.addStatToStack((Item)ModItems.cadBatteryExtended.get(), EnumCADStat.OVERFLOW, 200);
      ItemCADComponent.addStatToStack((Item)ModItems.cadBatteryUltradense.get(), EnumCADStat.OVERFLOW, 400);
   }

   @SubscribeEvent
   public static void modifyCreativeAssemblyStats(CADStatEvent event) {
      ItemStack cad = event.getCad();
      ICAD cadItem = (ICAD)cad.getItem();
      ItemStack assembly = cadItem.getComponentInSlot(cad, EnumCADComponent.ASSEMBLY);
      if (!assembly.isEmpty() && assembly.getItem() == ModItems.cadAssemblyCreative.get()) {
         switch (event.getStat()) {
            case BANDWIDTH:
               event.setStatValue(9);
               break;
            case SOCKETS:
               event.setStatValue(12);
               break;
            default:
               event.setStatValue(-1);
         }
      }
   }
}

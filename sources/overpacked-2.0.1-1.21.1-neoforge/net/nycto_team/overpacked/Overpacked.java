package net.nycto_team.overpacked;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.nycto_team.overpacked.core.CuriosRenderers;
import net.nycto_team.overpacked.registry.ModDDItems;
import net.nycto_team.overpacked.registry.ModEntities;
import net.nycto_team.overpacked.registry.ModItems;
import net.nycto_team.overpacked.registry.ModMenus;
import net.nycto_team.overpacked.registry.ModRecipes;
import net.nycto_team.overpacked.registry.ModTabs;
import net.nycto_team.overpacked.util.Utils;

@Mod("overpacked")
public class Overpacked {
   public static final String id = "overpacked";

   public Overpacked(IEventBus bus, ModContainer container) {
      ModTabs.Register(bus);
      ModItems.Register(bus);
      if (Utils.dye_depot) {
         ModDDItems.Register(bus);
      }

      ModEntities.Register(bus);
      ModMenus.Register(bus);
      ModRecipes.Register(bus);
      bus.addListener(this::Setup);
   }

   private void Setup(FMLCommonSetupEvent event) {
      CuriosRenderers.Setup(event);
   }
}

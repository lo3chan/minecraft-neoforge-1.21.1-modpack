package net.nycto_team.overpacked.core;

import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.nycto_team.overpacked.entity.renderer.GiantBackpackOnPlayerRenderer;
import net.nycto_team.overpacked.registry.ModDDItems;
import net.nycto_team.overpacked.registry.ModItems;
import net.nycto_team.overpacked.util.Utils;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRenderers {
   public static void Setup(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {
         CuriosRendererRegistry.register((Item)ModItems.giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.white_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.orange_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.magenta_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.light_blue_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.yellow_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.lime_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.pink_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.gray_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.light_gray_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.cyan_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.purple_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.blue_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.brown_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.green_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.red_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.black_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         if (Utils.dye_depot) {
            CuriosRendererRegistry.register((Item)ModDDItems.maroon_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.rose_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.coral_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.indigo_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.navy_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.slate_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.olive_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.amber_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.beige_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.teal_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.mint_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.aqua_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.verdant_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.forest_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.ginger_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
            CuriosRendererRegistry.register((Item)ModDDItems.tan_giant_backpack.get(), GiantBackpackOnPlayerRenderer::new);
         }
      });
   }
}

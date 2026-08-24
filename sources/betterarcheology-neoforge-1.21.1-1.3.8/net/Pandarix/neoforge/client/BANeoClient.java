package net.Pandarix.neoforge.client;

import net.Pandarix.block.ModBlocks;
import net.Pandarix.block.entity.ModBlockEntities;
import net.Pandarix.block.entity.client.ArcheologyTableBlockEntityRenderer;
import net.Pandarix.block.entity.client.SusBlockEntityRenderer;
import net.Pandarix.block.entity.client.VillagerFossilBlockEntityRenderer;
import net.Pandarix.entity.ModEntityTypes;
import net.Pandarix.screen.FossilInventoryScreen;
import net.Pandarix.screen.IdentifyingScreen;
import net.Pandarix.screen.ModMenuTypes;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   modid = "betterarcheology",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class BANeoClient {
   @SubscribeEvent
   public static void registerRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntities.ARCHEOLOGY_TABLE.get(), ArcheologyTableBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntities.SUSBLOCK.get(), SusBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntities.VILLAGER_FOSSIL.get(), VillagerFossilBlockEntityRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BOMB_ENTITY.get(), ThrownItemRenderer::new);
   }

   @SubscribeEvent
   public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
      event.register((MenuType)ModMenuTypes.FOSSIL_MENU.get(), FossilInventoryScreen::new);
      event.register((MenuType)ModMenuTypes.IDENTIFYING_MENU.get(), IdentifyingScreen::new);
   }

   @SubscribeEvent
   public static void clientSetup(FMLClientSetupEvent event) {
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.ROTTEN_DOOR.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.ROTTEN_TRAPDOOR.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.VILLAGER_FOSSIL.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.VILLAGER_FOSSIL_BODY.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.OCELOT_FOSSIL.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.OCELOT_FOSSIL_BODY.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.OCELOT_FOSSIL_HEAD.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.SHEEP_FOSSIL.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.SHEEP_FOSSIL_BODY.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.SHEEP_FOSSIL_HEAD.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.CHICKEN_FOSSIL.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.CHICKEN_FOSSIL_HEAD.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.CHICKEN_FOSSIL_BODY.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.CREEPER_FOSSIL.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.CREEPER_FOSSIL_BODY.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.CREEPER_FOSSIL_HEAD.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.WOLF_FOSSIL.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.WOLF_FOSSIL_BODY.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.WOLF_FOSSIL_HEAD.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.GUARDIAN_FOSSIL.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.GUARDIAN_FOSSIL_BODY.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.GUARDIAN_FOSSIL_HEAD.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.GROWTH_TOTEM.get(), RenderType.cutout());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.RADIANCE_TOTEM.get(), RenderType.cutout());
   }
}

package net.joefoxe.hexerei.event;

import java.util.List;
import net.joefoxe.hexerei.client.renderer.CrowWhitelistRenderer;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(
   modid = "hexerei",
   bus = Bus.MOD
)
public class ModEventBusEvents {
   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void registerOverlays(RegisterGuiLayersEvent event) {
      event.registerBelowAll(HexereiUtil.getResource("crow_whitelist"), new CrowWhitelistRenderer());
   }

   @SubscribeEvent
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<? extends RandomizableContainerBlockEntity>> container : List.of(
         ModTileEntities.MIXING_CAULDRON_TILE,
         ModTileEntities.DRYING_RACK_TILE,
         ModTileEntities.PESTLE_AND_MORTAR_TILE,
         ModTileEntities.CANDLE_DIPPER_TILE,
         ModTileEntities.CHEST_TILE,
         ModTileEntities.SAGE_BURNING_PLATE_TILE,
         ModTileEntities.COURIER_PACKAGE_TILE
      )) {
         event.registerBlockEntity(ItemHandler.BLOCK, (BlockEntityType)container.get(), (c, side) -> new InvWrapper(c));
      }

      event.registerBlockEntity(ItemHandler.BLOCK, (BlockEntityType)ModTileEntities.HERB_JAR_TILE.get(), (c, side) -> c.itemHandler);
      event.registerBlockEntity(ItemHandler.BLOCK, (BlockEntityType)ModTileEntities.COFFER_TILE.get(), (c, side) -> new CofferTile.CofferInvWrapper(c));
      event.registerBlockEntity(FluidHandler.BLOCK, (BlockEntityType)ModTileEntities.MIXING_CAULDRON_TILE.get(), (c, side) -> c);
      event.registerItem(
         ItemHandler.ITEM,
         (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, 5),
         new ItemLike[]{(ItemLike)ModItems.COURIER_PACKAGE.get()}
      );
      event.registerEntity(ItemHandler.ENTITY, (EntityType)ModEntityTypes.OWL.get(), (owl, v) -> owl.itemHandler);
      event.registerEntity(ItemHandler.ENTITY, (EntityType)ModEntityTypes.CROW.get(), (crow, v) -> crow.itemHandler);
      event.registerEntity(ItemHandler.ENTITY, (EntityType)ModEntityTypes.BROOM.get(), (broom, v) -> broom.itemHandler);
   }
}

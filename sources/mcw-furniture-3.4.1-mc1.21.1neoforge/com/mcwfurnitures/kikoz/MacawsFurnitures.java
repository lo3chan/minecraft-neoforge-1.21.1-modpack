package com.mcwfurnitures.kikoz;

import com.mcwfurnitures.kikoz.init.BlockEntityInit;
import com.mcwfurnitures.kikoz.init.BlockInit;
import com.mcwfurnitures.kikoz.init.ContainerInit;
import com.mcwfurnitures.kikoz.init.EntityInit;
import com.mcwfurnitures.kikoz.init.ItemInit;
import com.mcwfurnitures.kikoz.init.SoundsInit;
import com.mcwfurnitures.kikoz.init.TabInit;
import com.mcwfurnitures.kikoz.storage.ChairRenderer;
import com.mcwfurnitures.kikoz.storage.CouchRenderer;
import com.mcwfurnitures.kikoz.storage.FurnitureCapabilities;
import com.mcwfurnitures.kikoz.storage.FurnitureStorageScreeen;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod("mcwfurnitures")
public class MacawsFurnitures {
   public static final String MOD_ID = "mcwfurnitures";

   public MacawsFurnitures(IEventBus bus) {
      bus.addListener(this::onFMLCLientSetup);
      bus.addListener(this::onClientSetup);
      bus.addListener(this::commonSetup);
      bus.addListener(this::registerScreens);
      bus.addListener(FurnitureCapabilities::register);
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      TabInit.CREATIVE_TABS.register(bus);
      SoundsInit.SOUNDS.register(bus);
      EntityInit.REGISTER.register(bus);
      BlockEntityInit.REGISTER.register(bus);
      ContainerInit.CONTAINERS.register(bus);
   }

   @SubscribeEvent
   public void onFMLCLientSetup(FMLClientSetupEvent event) {
      EntityRenderers.register((EntityType)EntityInit.CHAIR.get(), ChairRenderer::new);
      EntityRenderers.register((EntityType)EntityInit.COUCH.get(), CouchRenderer::new);
   }

   @SubscribeEvent
   private void commonSetup(FMLCommonSetupEvent event) {
   }

   @SubscribeEvent
   public void registerScreens(RegisterMenuScreensEvent event) {
      event.register((MenuType)ContainerInit.EXAMPLE_CHEST.get(), FurnitureStorageScreeen::new);
   }

   @SubscribeEvent
   private void onClientSetup(FMLClientSetupEvent event) {
      EntityRenderers.register((EntityType)EntityInit.CHAIR.get(), ChairRenderer::new);
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   public static VoxelShape calculateShapes(Direction to, VoxelShape shape) {
      VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
      int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;

      for (int i = 0; i < times; i++) {
         buffer[0]
            .forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX)));
         buffer[0] = buffer[1];
         buffer[1] = Shapes.empty();
      }

      return buffer[0];
   }

   public static class Entity {
      public static final String SITTABLE_BLOCK = "mcwfurnitures:sittable_block";
   }
}

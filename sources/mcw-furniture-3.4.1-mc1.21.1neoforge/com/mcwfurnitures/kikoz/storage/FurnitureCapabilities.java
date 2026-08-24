package com.mcwfurnitures.kikoz.storage;

import com.mcwfurnitures.kikoz.init.BlockEntityInit;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;

public class FurnitureCapabilities {
   public static void register(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(
         ItemHandler.BLOCK,
         BlockEntityInit.FURNITURE_STORAGE.get(),
         (blockEntity, side) -> blockEntity instanceof StorageTileEntity ? blockEntity.getItemHandler() : null
      );
   }
}

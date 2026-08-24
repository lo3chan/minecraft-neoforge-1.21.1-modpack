package org.dimdev.limlib.api.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public interface ModelLoadingRegistry {
   void replaceBlockStates(Block var1);

   void replaceModel(ModelResourceLocation var1);

   default void replaceItem(ResourceLocation itemId) {
      this.replaceModel(ModelResourceLocation.inventory(itemId));
   }
}

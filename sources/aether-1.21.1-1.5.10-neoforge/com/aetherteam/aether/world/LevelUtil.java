package com.aetherteam.aether.world;

import com.aetherteam.aether.AetherConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class LevelUtil {
   public static ResourceKey<Level> destinationDimension() {
      return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse((String)AetherConfig.SERVER.portal_destination_dimension_ID.get()));
   }

   public static ResourceKey<Level> returnDimension() {
      return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse((String)AetherConfig.SERVER.portal_return_dimension_ID.get()));
   }
}

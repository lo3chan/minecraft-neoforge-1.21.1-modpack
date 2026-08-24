package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMinecraftSharedWrapper_neoforge implements IMinecraftSharedWrapper {
   @Nullable
   protected ResourceKey<Level> deserializeDimensionResourceKey(String dimensionResourceLocation) {
      ResourceLocation dimResourceLocation = ResourceLocation.tryParse(dimensionResourceLocation);
      return dimResourceLocation == null ? null : ResourceKey.create(Registries.DIMENSION, dimResourceLocation);
   }
}

package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMinecraftSharedWrapper_fabric implements IMinecraftSharedWrapper {
   @Nullable
   protected class_5321<class_1937> deserializeDimensionResourceKey(String dimensionResourceLocation) {
      class_2960 dimResourceLocation = class_2960.method_12829(dimensionResourceLocation);
      return dimResourceLocation == null ? null : class_5321.method_29179(class_7924.field_41223, dimResourceLocation);
   }
}

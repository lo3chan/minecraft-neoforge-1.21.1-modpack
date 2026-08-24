package com.seibel.distanthorizons.common.wrappers.misc;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public interface IMixinServerPlayer_neoforge {
   @Nullable
   ServerLevel distantHorizons$getDimensionChangeDestination();
}

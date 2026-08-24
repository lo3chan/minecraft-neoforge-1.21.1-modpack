package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride;

import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public abstract class AquiferOverride {
   public static final AquiferOverride NONE = new NoneAquiferOverride();

   public abstract AquiferOverrideType<?> type();

   public abstract BlockState getBlockState(BlockState var1);

   AquiferOverride() {
   }
}

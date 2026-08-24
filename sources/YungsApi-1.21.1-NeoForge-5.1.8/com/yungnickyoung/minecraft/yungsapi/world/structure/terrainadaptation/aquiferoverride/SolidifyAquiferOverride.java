package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;

public class SolidifyAquiferOverride extends AquiferOverride {
   private static final SolidifyAquiferOverride INSTANCE = new SolidifyAquiferOverride();
   public static final MapCodec<SolidifyAquiferOverride> CODEC = MapCodec.unit(() -> INSTANCE);
   private BlockState solidBlockState;

   @Override
   public AquiferOverrideType<?> type() {
      return AquiferOverrideType.SOLIDIFY;
   }

   @Override
   public BlockState getBlockState(BlockState defaultBlockState) {
      return this.solidBlockState == null ? defaultBlockState : this.solidBlockState;
   }

   public void setSolidBlockState(BlockState solidBlockState) {
      this.solidBlockState = solidBlockState;
   }
}

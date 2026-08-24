package io.github.razordevs.aeroblender.mixin;

import io.github.razordevs.aeroblender.AeroBlenderConfig;
import io.github.razordevs.aeroblender.aether.DefaultAetherRegion;
import org.spongepowered.asm.mixin.Mixin;
import terrablender.api.Regions;

@Mixin(
   value = {Regions.class},
   remap = false
)
public abstract class RegionMixin {
   static {
      Regions.register(new DefaultAetherRegion((Integer)AeroBlenderConfig.COMMON.vanillaAetherRegionWeight.get()));
   }
}

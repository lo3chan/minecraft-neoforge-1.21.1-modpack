package dev.worldgen.lithostitched.platform.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(
   value = "lithostitched",
   dist = {Dist.CLIENT}
)
public final class LithostitchedNeoforgeClient {
   public LithostitchedNeoforgeClient(IEventBus bus) {
   }
}

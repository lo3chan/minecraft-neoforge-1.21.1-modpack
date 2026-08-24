package com.anthonyhilyard.legendarytooltips.neoforge.client;

import com.anthonyhilyard.legendarytooltips.LegendaryTooltips;
import com.anthonyhilyard.legendarytooltips.client.LegendaryTooltipsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(
   value = "legendarytooltips",
   dist = {Dist.CLIENT}
)
public class LegendaryTooltipsNeoForgeClient {
   public LegendaryTooltipsNeoForgeClient() {
      LegendaryTooltips.init();
      LegendaryTooltipsClient.init();
   }
}

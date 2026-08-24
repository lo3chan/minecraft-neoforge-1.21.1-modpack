package dev.worldgen.deeper.oceans.platform.neoforge;

import dev.worldgen.deeper.oceans.config.ConfigScreenBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(
   value = "deeper_oceans",
   dist = {Dist.CLIENT}
)
public class DeeperOceansNeoforgeClient {
   public DeeperOceansNeoforgeClient(ModContainer container) {
      container.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(modContainer, parent) -> ConfigScreenBuilder.build(parent));
   }
}

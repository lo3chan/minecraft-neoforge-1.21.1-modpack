package malte0811.ferritecore;

import malte0811.ferritecore.impl.Deduplicator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.RegisterStageEvent;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "ferritecore"
)
public class ModClientForge {
   @SubscribeEvent
   public static void registerReloadListener(RegisterStageEvent ev) {
      Deduplicator.registerReloadListener();
   }
}

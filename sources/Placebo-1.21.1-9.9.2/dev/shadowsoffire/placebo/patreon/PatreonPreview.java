package dev.shadowsoffire.placebo.patreon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "placebo"
)
public class PatreonPreview {
   public static final boolean PARTICLES = false;
   public static final boolean WINGS = false;
   private static int counter = 0;

   @SubscribeEvent
   public static void tick(Post e) {
      Player player = e.getEntity();
      if (player.level().isClientSide && player.tickCount >= 200 && player.tickCount % 150 == 0) {
         Minecraft var2 = Minecraft.getInstance();
      }
   }
}

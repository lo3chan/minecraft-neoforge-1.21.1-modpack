package io.github.razordevs.deep_aether.event;

import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.screen.SnapshotScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ScreenEvent.Opening;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;

@EventBusSubscriber(
   modid = "deep_aether",
   value = {Dist.CLIENT},
   bus = Bus.GAME
)
public class DAClientGameBusEvents {
   private static boolean hasShownScreen = false;

   @SubscribeEvent
   public static void fogDensityEvent(RenderFog event) {
      Minecraft mc = Minecraft.getInstance();
      Player player = mc.player;
      if (player != null && mc.level != null) {
         BlockState state = mc.level.getBlockState(new BlockPos(new Vec3i(player.getBlockX(), (int)player.getEyeY(), player.getBlockZ())));
         if (state.is((Block)DABlocks.VIRULENT_QUICKSAND.get())) {
            event.setNearPlaneDistance(0.5F);
            event.setFarPlaneDistance(1.8F);
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onGuiOpen(Opening event) {
      if ("1.1.5.1".contains("snapshot") && event.getScreen() instanceof TitleScreen title && !hasShownScreen) {
         event.setNewScreen(new SnapshotScreen(title));
         hasShownScreen = true;
      }
   }
}

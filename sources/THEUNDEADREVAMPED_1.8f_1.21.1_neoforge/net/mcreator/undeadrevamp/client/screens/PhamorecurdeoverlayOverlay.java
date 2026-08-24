package net.mcreator.undeadrevamp.client.screens;

import net.mcreator.undeadrevamp.procedures.PhamorecurdeoverlayDisplayOverlayIngameProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent.Pre;

@EventBusSubscriber({Dist.CLIENT})
public class PhamorecurdeoverlayOverlay {
   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public static void eventHandler(Pre event) {
      int w = event.getGuiGraphics().guiWidth();
      int h = event.getGuiGraphics().guiHeight();
      Level world = null;
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      Player entity = Minecraft.getInstance().player;
      if (entity != null) {
         world = entity.level();
         x = entity.getX();
         y = entity.getY();
         z = entity.getZ();
      }

      if (PhamorecurdeoverlayDisplayOverlayIngameProcedure.execute(entity)) {
         event.getGuiGraphics()
            .drawString(
               Minecraft.getInstance().font,
               Component.translatable("gui.undead_revamp2.phamorecurdeoverlay.label_you_dare_touched_my_treasures"),
               w / 2 + -90,
               h / 2 + 74,
               -10658816,
               false
            );
      }
   }
}

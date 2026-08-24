package com.mrcrayfish.configured.client;

import com.mojang.datafixers.util.Either;
import com.mrcrayfish.configured.client.screen.TooltipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.Color;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.GatherComponents;
import net.neoforged.neoforge.client.event.ScreenEvent.Opening;
import net.neoforged.neoforge.client.gui.ModListScreen;

@EventBusSubscriber(
   modid = "configured",
   value = {Dist.CLIENT}
)
public class NeoForgeClientEvents {
   @SubscribeEvent
   private static void onKeyPress(Key event) {
      if (event.getAction() == 1 && ClientHandler.KEY_OPEN_MOD_LIST.isDown()) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player == null) {
            return;
         }

         Screen oldScreen = minecraft.screen;
         minecraft.setScreen(new ModListScreen(oldScreen));
      }
   }

   @SubscribeEvent
   private static void onGatherTooltipComponents(GatherComponents event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen instanceof TooltipScreen screen) {
         if (screen.tooltipText != null) {
            event.getTooltipElements().clear();

            for (FormattedCharSequence text : screen.tooltipText) {
               event.getTooltipElements().add(Either.right(new TooltipScreen.ListMenuTooltipComponent(text)));
            }
         }
      }
   }

   @SubscribeEvent
   private static void onGetTooltipColor(Color event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen instanceof TooltipScreen screen) {
         if (screen.tooltipText != null) {
            if (screen.tooltipOutlineColour != null) {
               event.setBorderStart(screen.tooltipOutlineColour);
               event.setBorderEnd(screen.tooltipOutlineColour);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onScreenOpen(Opening event) {
      EditingTracker.instance().onScreenOpen(event.getScreen());
   }
}

package com.aetherteam.aether.client.event.listeners;

import com.aetherteam.aether.client.event.hooks.GuiHooks;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Tuple;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.BossEventProgress;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Post;

public class GuiListener {
   public static void listen(IEventBus bus) {
      bus.addListener(GuiListener::onGuiInitialize);
      bus.addListener(GuiListener::onGuiDraw);
      bus.addListener(GuiListener::onClientTick);
      bus.addListener(GuiListener::onKeyPress);
      bus.addListener(GuiListener::onRenderBossBar);
   }

   public static void onGuiInitialize(Post event) {
      Screen screen = event.getScreen();
      Tuple<Integer, Integer> offsets = AetherAccessoriesScreen.getButtonOffset(screen);
      AccessoryButton inventoryAccessoryButton = GuiHooks.setupAccessoryButton(screen, offsets);
      if (inventoryAccessoryButton != null && GuiHooks.isAccessoryButtonEnabled()) {
         event.addListener(inventoryAccessoryButton);
      }

      GridLayout layout = GuiHooks.setupPerksButtons(screen);
      if (layout != null && !GuiHooks.isAccessoryButtonEnabled()) {
         layout.visitWidgets(event::addListener);
      }
   }

   public static void onGuiDraw(net.neoforged.neoforge.client.event.ScreenEvent.Render.Post event) {
      Screen screen = event.getScreen();
      GuiGraphics guiGraphics = event.getGuiGraphics();
      if (!ModList.get().isLoaded("tipsmod")) {
         GuiHooks.drawTrivia(screen, guiGraphics);
      }

      GuiHooks.drawAetherTravelMessage(screen, guiGraphics);
   }

   public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
      GuiHooks.handlePatreonRefreshRebound();
   }

   public static void onKeyPress(Key event) {
      GuiHooks.openAccessoryMenu();
      GuiHooks.closeContainerMenu(event.getKey(), event.getAction());
   }

   public static void onRenderBossBar(BossEventProgress event) {
      GuiGraphics guiGraphics = event.getGuiGraphics();
      LerpingBossEvent bossEvent = event.getBossEvent();
      UUID bossUUID = bossEvent.getId();
      if (GuiHooks.isAetherBossBar(bossUUID)) {
         GuiHooks.drawBossHealthBar(guiGraphics, event.getX(), event.getY(), bossEvent);
         event.setIncrement(event.getIncrement() + 13);
      }
   }
}

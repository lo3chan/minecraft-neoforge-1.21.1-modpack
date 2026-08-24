package com.aetherteam.cumulus.client.event.listeners;

import com.aetherteam.cumulus.Cumulus;
import com.aetherteam.cumulus.client.event.hooks.WorldPreviewHooks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Pre;
import net.neoforged.neoforge.client.event.ScreenEvent.Opening;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   modid = "cumulus_menus",
   value = {Dist.CLIENT}
)
public class WorldPreviewListener {
   @SubscribeEvent
   public static void onServerAboutToStart(ServerAboutToStartEvent event) {
      Cumulus.SERVER_INSTANCE = ServerLifecycleHooks.getCurrentServer();
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      Cumulus.SERVER_INSTANCE = null;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onGuiOpenLowest(Opening event) {
      Screen newScreen = event.getNewScreen();
      WorldPreviewHooks.setupWorldPreview(newScreen);
   }

   @SubscribeEvent
   public static void onScreenRender(net.neoforged.neoforge.client.event.ScreenEvent.Render.Pre event) {
      Screen screen = event.getScreen();
      if (WorldPreviewHooks.hideScreen(screen)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onRenderLevelLast(RenderLevelStageEvent event) {
      Stage stage = event.getStage();
      if (stage == Stage.AFTER_WEATHER) {
         WorldPreviewHooks.renderMenuWithWorld();
      }
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      WorldPreviewHooks.tickMenuWhenPaused();
   }

   @SubscribeEvent
   public static void onCameraView(ComputeCameraAngles event) {
      double partialTick = event.getPartialTick();
      WorldPreviewHooks.angleCamera(partialTick);
   }

   @SubscribeEvent
   public static void onRenderOverlay(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Pre event) {
      if (WorldPreviewHooks.hideOverlays()) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onRenderPlayer(net.neoforged.neoforge.client.event.RenderPlayerEvent.Pre event) {
      PlayerRenderer renderer = event.getRenderer();
      boolean hide = WorldPreviewHooks.shouldHidePlayer();
      if (hide) {
         event.setCanceled(true);
      }

      WorldPreviewHooks.adjustShadow(renderer, hide);
   }

   @SubscribeEvent
   public static void onRenderEntity(Pre<?, ?> event) {
      Entity entity = event.getEntity();
      EntityRenderer<?> renderer = event.getRenderer();
      boolean hide = WorldPreviewHooks.shouldHideEntity(entity);
      if (hide) {
         event.setCanceled(true);
      }

      WorldPreviewHooks.adjustShadow(renderer, hide);
   }
}

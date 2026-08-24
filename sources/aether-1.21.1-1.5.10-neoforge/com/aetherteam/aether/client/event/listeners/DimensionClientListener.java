package com.aetherteam.aether.client.event.listeners;

import com.aetherteam.aether.client.event.hooks.DimensionClientHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;
import org.apache.commons.lang3.tuple.Triple;

public class DimensionClientListener {
   public static void listen(IEventBus bus) {
      bus.addListener(DimensionClientListener::onRenderFog);
      bus.addListener(DimensionClientListener::onRenderFogColor);
      bus.addListener(DimensionClientListener::onClientTick);
   }

   public static void onRenderFog(RenderFog event) {
      Camera camera = event.getCamera();
      FogMode fogMode = event.getMode();
      Float renderNearFog = DimensionClientHooks.renderNearFog(camera, fogMode, event.getFarPlaneDistance());
      if (!event.isCanceled() && renderNearFog != null) {
         event.setNearPlaneDistance(renderNearFog);
         event.setCanceled(true);
      }

      Float reduceLavaFog = DimensionClientHooks.reduceLavaFog(camera, event.getNearPlaneDistance());
      if (!event.isCanceled() && reduceLavaFog != null) {
         event.setNearPlaneDistance(reduceLavaFog);
         event.setFarPlaneDistance(reduceLavaFog * 4.0F);
         event.setCanceled(true);
      }
   }

   public static void onRenderFogColor(ComputeFogColor event) {
      Camera camera = event.getCamera();
      Triple<Float, Float, Float> renderFogColors = DimensionClientHooks.renderFogColors(camera, event.getRed(), event.getGreen(), event.getBlue());
      if (renderFogColors != null) {
         event.setRed((Float)renderFogColors.getLeft());
         event.setGreen((Float)renderFogColors.getMiddle());
         event.setBlue((Float)renderFogColors.getRight());
      }

      Triple<Float, Float, Float> adjustWeatherFogColors = DimensionClientHooks.adjustWeatherFogColors(
         camera, event.getRed(), event.getGreen(), event.getBlue()
      );
      if (adjustWeatherFogColors != null) {
         event.setRed((Float)adjustWeatherFogColors.getLeft());
         event.setGreen((Float)adjustWeatherFogColors.getMiddle());
         event.setBlue((Float)adjustWeatherFogColors.getRight());
      }
   }

   public static void onClientTick(Post event) {
      DimensionClientHooks.tickTime();
   }
}

package com.aetherteam.cumulus.client.event.hooks;

import com.aetherteam.cumulus.CumulusConfig;
import com.aetherteam.cumulus.client.WorldDisplayHelper;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.EntityRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldPreviewHooks {
   private static final MutableBoolean setupLockout = new MutableBoolean();

   public static void setupWorldPreview(Screen screen) {
      if (screen instanceof TitleScreen && (Boolean)CumulusConfig.CLIENT.enable_world_preview.get()) {
         if (!setupLockout.getValue()) {
            setupLockout.setValue(true);
            WorldDisplayHelper.enableWorldPreview();
            setupLockout.setValue(false);
         }
      } else if (screen instanceof TitleScreen && !(Boolean)CumulusConfig.CLIENT.enable_world_preview.get()) {
         WorldDisplayHelper.resetActive();
      }
   }

   public static boolean hideScreen(Screen screen) {
      return screen instanceof TitleScreen && (Boolean)CumulusConfig.CLIENT.enable_world_preview.get() && Minecraft.getInstance().level == null;
   }

   public static void renderMenuWithWorld() {
      Minecraft minecraft = Minecraft.getInstance();
      if (WorldDisplayHelper.isActive() && (minecraft.screen == null || minecraft.screen instanceof PauseScreen)) {
         WorldDisplayHelper.setupLevelForDisplay();
      }
   }

   public static void tickMenuWhenPaused() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null && minecraft.player != null && WorldDisplayHelper.isActive() && minecraft.isPaused()) {
         minecraft.gameRenderer.tick();
         minecraft.levelRenderer.tick();
         minecraft.getMusicManager().tick();
         minecraft.getSoundManager().tick(false);
         minecraft.level.animateTick(minecraft.player.getBlockX(), minecraft.player.getBlockY(), minecraft.player.getBlockZ());
         Minecraft.getInstance().particleEngine.tick();
      }
   }

   public static void angleCamera(double partialTick) {
      Minecraft minecraft = Minecraft.getInstance();
      Player player = minecraft.player;
      if (WorldDisplayHelper.isActive() && player != null) {
         float f = (float)(partialTick * (Double)minecraft.options.panoramaSpeed().get());
         float spin = wrapDegrees(player.getViewYRot((float)partialTick) + f * 0.2F);
         player.setYRot(spin);
         player.setXRot(0.0F);
      }
   }

   private static float wrapDegrees(float value) {
      return value > 360.0F ? value - 360.0F : value;
   }

   public static boolean hideOverlays() {
      return WorldDisplayHelper.isActive();
   }

   public static boolean shouldHidePlayer() {
      return WorldDisplayHelper.isActive();
   }

   public static boolean shouldHideEntity(Entity entity) {
      return WorldDisplayHelper.isActive()
         && Minecraft.getInstance().player != null
         && Minecraft.getInstance().player.getVehicle() != null
         && Minecraft.getInstance().player.getVehicle().is(entity);
   }

   public static void adjustShadow(EntityRenderer<?> renderer, boolean flag) {
      EntityRendererAccessor entityRendererAccessor = (EntityRendererAccessor)renderer;
      if (flag) {
         entityRendererAccessor.cumulus$setShadowRadius(0.0F);
      } else if (entityRendererAccessor.cumulus$getShadowRadius() == 0.0F) {
         entityRendererAccessor.cumulus$setShadowRadius(0.5F);
      }
   }
}

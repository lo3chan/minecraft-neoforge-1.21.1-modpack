package com.aetherteam.aether.client.event.listeners;

import com.aetherteam.aether.client.event.hooks.LevelClientHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

public class LevelClientListener {
   public static void listen(IEventBus bus) {
      bus.addListener(LevelClientListener::onRenderLevelLast);
   }

   public static void onRenderLevelLast(RenderLevelStageEvent event) {
      Stage stage = event.getStage();
      PoseStack poseStack = event.getPoseStack();
      Camera camera = event.getCamera();
      Frustum frustum = event.getFrustum();
      Minecraft minecraft = Minecraft.getInstance();
      LevelClientHooks.renderDungeonBlockOverlays(stage, poseStack, camera, frustum, minecraft);
   }
}

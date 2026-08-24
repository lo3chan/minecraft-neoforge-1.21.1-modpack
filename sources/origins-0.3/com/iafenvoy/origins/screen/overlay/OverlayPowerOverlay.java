package com.iafenvoy.origins.screen.overlay;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.builtin.regular.OverlayPower;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber({Dist.CLIENT})
public class OverlayPowerOverlay implements Layer {
   private final Minecraft minecraft = Minecraft.getInstance();
   private final OverlayPower.DrawPhase phase;

   public OverlayPowerOverlay(OverlayPower.DrawPhase phase) {
      this.phase = phase;
   }

   public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
      Entity cameraEntity = this.minecraft.getCameraEntity();
      if (cameraEntity != null) {
         boolean hideGui = this.minecraft.options.hideGui;
         boolean isFirstPerson = this.minecraft.options.getCameraType().isFirstPerson();
         int width = graphics.guiWidth();
         int height = graphics.guiHeight();
         PowerHelper.get(cameraEntity)
            .execute(
               OverlayPower.class,
               p -> p.getDrawPhase() == this.phase && (!p.shouldHideWithHud() || !hideGui) && (p.isVisibleInThirdPerson() || isFirstPerson),
               (h, p) -> this.renderPower(p, graphics, width, height)
            );
      }
   }

   private void renderPower(OverlayPower power, GuiGraphics graphics, int width, int height) {
      float strength = power.getStrength();
      ColorSettings color = power.getColor();
      OverlayPower.DrawMode mode = power.getDrawMode();
      graphics.pose().pushPose();
      graphics.pose().translate(width / 2.0F, height / 2.0F, 1.0F);
      if (mode == OverlayPower.DrawMode.NAUSEA) {
         float s = Mth.lerp(strength, 2.0F, 1.0F);
         graphics.pose().scale(s, s, s);
      }

      graphics.pose().translate(-width / 2.0F, -height / 2.0F, 0.0F);
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      float alpha = 1.0F;
      switch (mode) {
         case NAUSEA:
            RenderSystem.blendFuncSeparate(SourceFactor.ONE, DestFactor.ONE, SourceFactor.ONE, DestFactor.ONE);
            break;
         case TEXTURE:
            RenderSystem.defaultBlendFunc();
            alpha = strength;
      }

      graphics.setColor(color.r().orElse(1.0F), color.g().orElse(1.0F), color.b().orElse(1.0F), alpha);
      graphics.blit(power.getTexture(), 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      graphics.pose().popPose();
   }

   @SubscribeEvent
   public static void registerOverlay(RegisterGuiLayersEvent event) {
      event.registerAboveAll(ResourceLocation.fromNamespaceAndPath("origins", "above_overlay"), new OverlayPowerOverlay(OverlayPower.DrawPhase.ABOVE_HUD));
      event.registerBelowAll(ResourceLocation.fromNamespaceAndPath("origins", "below_overlay"), new OverlayPowerOverlay(OverlayPower.DrawPhase.BELOW_HUD));
   }
}

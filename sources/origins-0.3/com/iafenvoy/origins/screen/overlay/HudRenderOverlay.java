package com.iafenvoy.origins.screen.overlay;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data._common.HudRender;
import com.iafenvoy.origins.data.power.HudRenderable;
import java.util.Comparator;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber({Dist.CLIENT})
public enum HudRenderOverlay implements Layer {
   INSTANCE;

   public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
      Minecraft minecraft = Minecraft.getInstance();
      Player player = minecraft.player;
      if (!minecraft.options.hideGui && player != null) {
         OriginDataHolder holder = OriginDataHolder.get(player);
         int x = minecraft.getWindow().getGuiScaledWidth() / 2 + 20 + (Integer)OriginsConfig.INSTANCE.general.hudOffsetX.getValue();
         int y = minecraft.getWindow().getGuiScaledHeight() - 47 + (Integer)OriginsConfig.INSTANCE.general.hudOffsetY.getValue();
         if (player.getVehicle() instanceof LivingEntity vehicle) {
            y -= 8 * (int)(vehicle.getMaxHealth() / 20.0F);
         }

         if (player.isEyeInFluidType((FluidType)NeoForgeMod.WATER_TYPE.value()) || player.getAirSupply() < player.getMaxAirSupply()) {
            y -= 8;
         }

         int barWidth = 71;
         int barHeight = 8;
         int iconSize = 8;

         for (HudRenderable h : holder.streamPowers(HudRenderable.class)
            .filter(hx -> hx.getHudRenderData().isPresent())
            .sorted(Comparator.comparingInt(hx -> hx.getHudRenderData().get().order()))
            .toList()) {
            HudRender render = h.getHudRenderData().orElse(null);
            if (render != null && (render.shouldRenderInActive() || h.shouldRender(holder)) && render.condition().test(player)) {
               ResourceLocation currentLocation = render.spriteLocation();
               graphics.blit(currentLocation, x, y, 0, 0, barWidth, 5);
               int v = 8 + render.barIndex() * 10;
               float fill = h.getRenderPercentage(holder);
               if (render.inverted()) {
                  fill = 1.0F - fill;
               }

               int w = (int)(fill * barWidth);
               graphics.blit(currentLocation, x, y - 2, 0, v, w, barHeight);
               graphics.blit(currentLocation, x - iconSize - 2, y - 2, 73, v, iconSize, iconSize);
               y -= 8;
            }
         }
      }
   }

   @SubscribeEvent
   public static void registerOverlay(RegisterGuiLayersEvent event) {
      event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath("origins", "overlay"), INSTANCE);
   }
}

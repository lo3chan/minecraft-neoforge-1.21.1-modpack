package io.github.razordevs.deep_aether.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.item.gear.stratus.StratusAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class DAOverlays {
   public static final ResourceLocation STRATUS_COOLDOWN_1 = ResourceLocation.fromNamespaceAndPath("deep_aether", "hud/stratus/stratus_cooldown_1");
   public static final ResourceLocation STRATUS_COOLDOWN_2 = ResourceLocation.fromNamespaceAndPath("deep_aether", "hud/stratus/stratus_cooldown_2");
   public static final ResourceLocation STRATUS_COOLDOWN_3 = ResourceLocation.fromNamespaceAndPath("deep_aether", "hud/stratus/stratus_cooldown_3");

   @SubscribeEvent
   public static void registerOverlays(RegisterGuiLayersEvent event) {
      event.registerAboveAll(ResourceLocation.fromNamespaceAndPath("deep_aether", "stratus"), (guiGraphics, deltaTracker) -> {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer player = minecraft.player;
         if (player != null && !player.isSpectator() && (Integer)DeepAetherConfig.SERVER.stratus_dash_cooldown.get() > 0 && StratusAbility.coolDown > 0.0F) {
            renderStratusCooldown(guiGraphics);
         }
      });
   }

   private static void renderStratusCooldown(GuiGraphics guiGraphics) {
      RenderSystem.enableBlend();
      stratusCooldown(
         guiGraphics,
         guiGraphics.guiWidth() - (Integer)DeepAetherConfig.CLIENT.stratus_cooldown_indicator_x_position.get(),
         guiGraphics.guiHeight() - (Integer)DeepAetherConfig.CLIENT.stratus_colldown_indicator_y_position.get()
      );
      RenderSystem.disableBlend();
   }

   private static void stratusCooldown(GuiGraphics guiGraphics, int pX, int pY) {
      float m = StratusAbility.coolDown / ((Integer)DeepAetherConfig.SERVER.stratus_dash_cooldown.get()).intValue();
      ResourceLocation sprite;
      if (m > 0.667) {
         sprite = STRATUS_COOLDOWN_1;
      } else if (m > 0.333) {
         sprite = STRATUS_COOLDOWN_2;
      } else {
         sprite = STRATUS_COOLDOWN_3;
      }

      guiGraphics.blitSprite(sprite, pX, pY, 16, 16);
   }
}

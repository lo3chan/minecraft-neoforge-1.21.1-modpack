package traben.entity_texture_features.config.screens.skin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.mixin.mixins.accessor.TooltipAccessor;
import traben.tconfig.gui.TConfigScreen;

public abstract class ETFScreenOldCompat extends TConfigScreen {
   protected ETFScreenOldCompat(String title, Screen parent, boolean showBackButton) {
      super(title, parent, showBackButton);
   }

   public static void renderGUITexture(GuiGraphics context, ResourceLocation texture, double x1, double y1, double x2, double y2) {
      RenderSystem.setShaderTexture(0, texture);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.enableBlend();
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bufferBuilder.addVertex((float)x1, (float)y2, 0.0F).setUv(0.0F, 1.0F).setColor(255, 255, 255, 255);
      bufferBuilder.addVertex((float)x2, (float)y2, 0.0F).setUv(1.0F, 1.0F).setColor(255, 255, 255, 255);
      bufferBuilder.addVertex((float)x2, (float)y1, 0.0F).setUv(1.0F, 0.0F).setColor(255, 255, 255, 255);
      bufferBuilder.addVertex((float)x1, (float)y1, 0.0F).setUv(0.0F, 0.0F).setColor(255, 255, 255, 255);
      BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
      RenderSystem.disableBlend();
   }

   public static String booleanAsOnOff(boolean bool) {
      return CommonComponents.optionStatus(bool).getString();
   }

   public Button getETFButton(int x, int y, int width, int height, Component buttonText, OnPress onPress) {
      return this.getETFButton(x, y, width, height, buttonText, onPress, Component.nullToEmpty(""));
   }

   public Button getETFButton(int x, int y, int width, int height, Component buttonText, OnPress onPress, Component toolTipText) {
      int nudgeLeftEdge;
      if (width > 384) {
         nudgeLeftEdge = (width - 384) / 2;
         width = 384;
      } else {
         nudgeLeftEdge = 0;
      }

      boolean tooltipIsEmpty = toolTipText.getString().isBlank();
      if (tooltipIsEmpty) {
         return Button.builder(buttonText, onPress).bounds(x + nudgeLeftEdge, y, width, height).build();
      } else {
         Tooltip bob = Tooltip.create(toolTipText);
         if (!ETF.isThisModLoaded("adaptive-tooltips")) {
            String[] strings = toolTipText.getString().split("\n");
            List<FormattedCharSequence> texts = new ArrayList<>();

            for (String str : strings) {
               texts.add(Component.nullToEmpty(str).getVisualOrderText());
            }

            ((TooltipAccessor)bob).setCachedTooltip(texts);
         }

         return Button.builder(buttonText, onPress).bounds(x + nudgeLeftEdge, y, width, height).tooltip(bob).build();
      }
   }
}

package io.wispforest.owo.ui.core;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.GlStateManager.Viewport;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.wispforest.owo.client.OwoClient;
import io.wispforest.owo.mixin.ScreenAccessor;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.NinePatchTexture;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.w3c.dom.Element;

public interface Surface {
   Surface PANEL = (context, component) -> context.drawPanel(component.x(), component.y(), component.width(), component.height(), false);
   Surface DARK_PANEL = (context, component) -> context.drawPanel(component.x(), component.y(), component.width(), component.height(), true);
   Surface PANEL_INSET = (context, component) -> NinePatchTexture.draw(OwoUIDrawContext.PANEL_INSET_NINE_PATCH_TEXTURE, context, component);
   Surface VANILLA_TRANSLUCENT = (context, component) -> context.drawGradientRect(
      component.x(), component.y(), component.width(), component.height(), -1072689136, -1072689136, -804253680, -804253680
   );
   Surface OPTIONS_BACKGROUND = panorama(ScreenAccessor.owo$ROTATING_PANORAMA_RENDERER(), false).and(blur(5.0F, 10.0F));
   Surface TOOLTIP = (context, component) -> context.drawManaged(() -> {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      TooltipRenderUtil.renderTooltipBackground(context, component.x() + 4, component.y() + 4, component.width() - 8, component.height() - 8, 0);
   });
   Surface BLANK = (context, component) -> {};

   static Surface blur(float quality, float size) {
      return (context, component) -> {
         BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION);
         Matrix4f matrix = context.pose().last().pose();
         buffer.addVertex(matrix, component.x(), component.y(), 0.0F);
         buffer.addVertex(matrix, component.x(), component.y() + component.height(), 0.0F);
         buffer.addVertex(matrix, component.x() + component.width(), component.y() + component.height(), 0.0F);
         buffer.addVertex(matrix, component.x() + component.width(), component.y(), 0.0F);
         OwoClient.BLUR_PROGRAM.setParameters(16, quality, size);
         OwoClient.BLUR_PROGRAM.use();
         BufferUploader.drawWithShader(buffer.buildOrThrow());
      };
   }

   static Surface vanillaPanorama(boolean alwaysVisible) {
      return panorama(new PanoramaRenderer(ScreenAccessor.owo$PANORAMA_RENDERER()), alwaysVisible);
   }

   static Surface panorama(PanoramaRenderer renderer, boolean alwaysVisible) {
      return (context, component) -> {
         if (alwaysVisible || Minecraft.getInstance().level == null) {
            Minecraft client = Minecraft.getInstance();
            int prevX = Viewport.x();
            int prevY = Viewport.y();
            int prevWidth = Viewport.width();
            int prevHeight = Viewport.height();
            Window window = client.getWindow();
            double scale = window.getGuiScale();
            int x = component.x();
            int y = component.y();
            int width = component.width();
            int height = component.height();
            RenderSystem.viewport(
               (int)(x * scale),
               (int)(window.getHeight() - y * scale - height * scale),
               Mth.clamp((int)(width * scale), 0, window.getWidth()),
               Mth.clamp((int)(height * scale), 0, window.getHeight())
            );
            float delta = client.getTimer().getRealtimeDeltaTicks();
            RenderSystem.disableDepthTest();
            renderer.render(context, width, height, 1.0F, delta);
            RenderSystem.enableDepthTest();
            RenderSystem.viewport(prevX, prevY, prevWidth, prevHeight);
         }
      };
   }

   static Surface flat(int color) {
      return (context, component) -> context.fill(component.x(), component.y(), component.x() + component.width(), component.y() + component.height(), color);
   }

   static Surface outline(int color) {
      return (context, component) -> context.drawRectOutline(component.x(), component.y(), component.width(), component.height(), color);
   }

   static Surface tiled(ResourceLocation texture, int textureWidth, int textureHeight) {
      return (context, component) -> context.blit(
         texture, component.x(), component.y(), 0.0F, 0.0F, component.width(), component.height(), textureWidth, textureHeight
      );
   }

   static Surface panelWithInset(int insetWidth) {
      return PANEL.and(
         (context, component) -> NinePatchTexture.draw(
            OwoUIDrawContext.PANEL_INSET_NINE_PATCH_TEXTURE,
            context,
            component.x() + insetWidth,
            component.y() + insetWidth,
            component.width() - insetWidth * 2,
            component.height() - insetWidth * 2
         )
      );
   }

   void draw(OwoUIDrawContext var1, ParentComponent var2);

   default Surface and(Surface surface) {
      return (context, component) -> {
         this.draw(context, component);
         surface.draw(context, component);
      };
   }

   static Surface parse(Element surfaceElement) {
      List<Element> children = UIParsing.allChildrenOfType(surfaceElement, (short)1);
      Surface surface = BLANK;

      for (Element child : children) {
         String var5 = child.getNodeName();

         surface = switch (var5) {
            case "panel" -> surface.and(child.getAttribute("dark").equalsIgnoreCase("true") ? DARK_PANEL : PANEL);
            case "tiled" -> {
               UIParsing.expectAttributes(child, "texture-width", "texture-height");
               yield surface.and(
                  tiled(
                     UIParsing.parseIdentifier(child),
                     UIParsing.parseUnsignedInt(child.getAttributeNode("texture-width")),
                     UIParsing.parseUnsignedInt(child.getAttributeNode("texture-height"))
                  )
               );
            }
            case "blur" -> {
               UIParsing.expectAttributes(child, "size", "quality");
               yield surface.and(blur(UIParsing.parseFloat(child.getAttributeNode("quality")), UIParsing.parseFloat(child.getAttributeNode("size"))));
            }
            case "panel-with-inset" -> surface.and(panelWithInset(UIParsing.parseUnsignedInt(child)));
            case "options-background" -> surface.and(OPTIONS_BACKGROUND);
            case "vanilla-translucent" -> surface.and(VANILLA_TRANSLUCENT);
            case "panel-inset" -> surface.and(PANEL_INSET);
            case "tooltip" -> surface.and(TOOLTIP);
            case "outline" -> surface.and(outline(Color.parseAndPack(child)));
            case "flat" -> surface.and(flat(Color.parseAndPack(child)));
            default -> throw new UIModelParsingException("Unknown surface type '" + child.getNodeName() + "'");
         };
      }

      return surface;
   }
}

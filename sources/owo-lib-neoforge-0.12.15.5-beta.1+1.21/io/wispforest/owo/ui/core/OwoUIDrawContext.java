package io.wispforest.owo.ui.core;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.wispforest.owo.client.OwoClient;
import io.wispforest.owo.mixin.ui.DrawContextInvoker;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import io.wispforest.owo.ui.util.NinePatchTexture;
import io.wispforest.owo.util.pond.OwoTessellatorExtension;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import org.joml.Vector2d;

public class OwoUIDrawContext extends GuiGraphics {
   @Deprecated
   public static final ResourceLocation PANEL_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "textures/gui/panel.png");
   @Deprecated
   public static final ResourceLocation DARK_PANEL_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "textures/gui/dark_panel.png");
   @Deprecated
   public static final ResourceLocation PANEL_INSET_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "textures/gui/panel_inset.png");
   public static final ResourceLocation PANEL_NINE_PATCH_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "panel/default");
   public static final ResourceLocation DARK_PANEL_NINE_PATCH_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "panel/dark");
   public static final ResourceLocation PANEL_INSET_NINE_PATCH_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "panel/inset");
   private boolean recording = false;

   private OwoUIDrawContext(Minecraft client, BufferSource vertexConsumers) {
      super(client, vertexConsumers);
   }

   public static OwoUIDrawContext of(GuiGraphics context) {
      OwoUIDrawContext owoContext = new OwoUIDrawContext(Minecraft.getInstance(), context.bufferSource());
      ((DrawContextInvoker)owoContext).owo$setScissorStack(((DrawContextInvoker)context).owo$getScissorStack());
      ((DrawContextInvoker)owoContext).owo$setMatrices(((DrawContextInvoker)context).owo$getMatrices());
      return owoContext;
   }

   public static OwoUIDrawContext.UtilityScreen utilityScreen() {
      return OwoUIDrawContext.UtilityScreen.get();
   }

   public void recordQuads() {
      this.recording = true;
   }

   public boolean recording() {
      return this.recording;
   }

   public void submitQuads() {
      this.recording = false;
      OwoTessellatorExtension extension = (OwoTessellatorExtension)Tesselator.getInstance();
      BufferBuilder buffer = extension.owo$getStoredBuilder();
      extension.owo$setStoredBuilder(null);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
   }

   public void drawRectOutline(int x, int y, int width, int height, int color) {
      this.fill(x, y, x + width, y + 1, color);
      this.fill(x, y + height - 1, x + width, y + height, color);
      this.fill(x, y + 1, x + 1, y + height - 1, color);
      this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
   }

   public void drawGradientRect(int x, int y, int width, int height, int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor) {
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = this.pose().last().pose();
      buffer.addVertex(matrix, x + width, y, 0.0F).setColor(topRightColor);
      buffer.addVertex(matrix, x, y, 0.0F).setColor(topLeftColor);
      buffer.addVertex(matrix, x, y + height, 0.0F).setColor(bottomLeftColor);
      buffer.addVertex(matrix, x + width, y + height, 0.0F).setColor(bottomRightColor);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
      RenderSystem.disableBlend();
   }

   public void drawPanel(int x, int y, int width, int height, boolean dark) {
      NinePatchTexture.draw(dark ? DARK_PANEL_NINE_PATCH_TEXTURE : PANEL_NINE_PATCH_TEXTURE, this, x, y, width, height);
   }

   public void drawSpectrum(int x, int y, int width, int height, boolean vertical) {
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = this.pose().last().pose();
      buffer.addVertex(matrix, x, y, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
      buffer.addVertex(matrix, x, y + height, 0.0F).setColor(vertical ? 0.0F : 1.0F, 1.0F, 1.0F, 1.0F);
      buffer.addVertex(matrix, x + width, y + height, 0.0F).setColor(0.0F, 1.0F, 1.0F, 1.0F);
      buffer.addVertex(matrix, x + width, y, 0.0F).setColor(vertical ? 1.0F : 0.0F, 1.0F, 1.0F, 1.0F);
      OwoClient.HSV_PROGRAM.use();
      BufferUploader.drawWithShader(buffer.buildOrThrow());
   }

   public void drawText(net.minecraft.network.chat.Component text, float x, float y, float scale, int color) {
      this.drawText(text, x, y, scale, color, OwoUIDrawContext.TextAnchor.TOP_LEFT);
   }

   public void drawText(net.minecraft.network.chat.Component text, float x, float y, float scale, int color, OwoUIDrawContext.TextAnchor anchorPoint) {
      Font textRenderer = Minecraft.getInstance().font;
      this.pose().pushPose();
      this.pose().scale(scale, scale, 1.0F);
      switch (anchorPoint) {
         case TOP_RIGHT:
            x -= textRenderer.width(text) * scale;
            break;
         case BOTTOM_RIGHT:
            x -= textRenderer.width(text) * scale;
            y -= 9.0F * scale;
         case TOP_LEFT:
         default:
            break;
         case BOTTOM_LEFT:
            y -= 9.0F * scale;
      }

      this.drawString(textRenderer, text, (int)(x * (1.0F / scale)), (int)(y * (1.0F / scale)), color, false);
      this.pose().popPose();
   }

   public void drawLine(int x1, int y1, int x2, int y2, double thiccness, Color color) {
      Vector2d offset = new Vector2d(x2 - x1, y2 - y1).perpendicular().normalize().mul(thiccness * 0.5);
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = this.pose().last().pose();
      int vColor = color.argb();
      buffer.addVertex(matrix, (float)(x1 + offset.x), (float)(y1 + offset.y), 0.0F).setColor(vColor);
      buffer.addVertex(matrix, (float)(x1 - offset.x), (float)(y1 - offset.y), 0.0F).setColor(vColor);
      buffer.addVertex(matrix, (float)(x2 - offset.x), (float)(y2 - offset.y), 0.0F).setColor(vColor);
      buffer.addVertex(matrix, (float)(x2 + offset.x), (float)(y2 + offset.y), 0.0F).setColor(vColor);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
   }

   public void drawCircle(int centerX, int centerY, int segments, double radius, Color color) {
      this.drawCircle(centerX, centerY, 0.0, 360.0, segments, radius, color);
   }

   public void drawCircle(int centerX, int centerY, double angleFrom, double angleTo, int segments, double radius, Color color) {
      Preconditions.checkArgument(angleFrom < angleTo, "angleFrom must be less than angleTo");
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = this.pose().last().pose();
      double angleStep = Math.toRadians(angleTo - angleFrom) / segments;
      int vColor = color.argb();
      buffer.addVertex(matrix, centerX, centerY, 0.0F).setColor(vColor);

      for (int i = segments; i >= 0; i--) {
         double theta = Math.toRadians(angleFrom) + i * angleStep;
         buffer.addVertex(matrix, (float)(centerX - Math.cos(theta) * radius), (float)(centerY - Math.sin(theta) * radius), 0.0F).setColor(vColor);
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
   }

   public void drawRing(int centerX, int centerY, int segments, double innerRadius, double outerRadius, Color innerColor, Color outerColor) {
      this.drawRing(centerX, centerY, 0.0, 360.0, segments, innerRadius, outerRadius, innerColor, outerColor);
   }

   public void drawRing(
      int centerX, int centerY, double angleFrom, double angleTo, int segments, double innerRadius, double outerRadius, Color innerColor, Color outerColor
   ) {
      Preconditions.checkArgument(angleFrom < angleTo, "angleFrom must be less than angleTo");
      Preconditions.checkArgument(innerRadius < outerRadius, "innerRadius must be less than outerRadius");
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = this.pose().last().pose();
      double angleStep = Math.toRadians(angleTo - angleFrom) / segments;
      int inColor = innerColor.argb();
      int outColor = outerColor.argb();

      for (int i = 0; i <= segments; i++) {
         double theta = Math.toRadians(angleFrom) + i * angleStep;
         buffer.addVertex(matrix, (float)(centerX - Math.cos(theta) * outerRadius), (float)(centerY - Math.sin(theta) * outerRadius), 0.0F).setColor(outColor);
         buffer.addVertex(matrix, (float)(centerX - Math.cos(theta) * innerRadius), (float)(centerY - Math.sin(theta) * innerRadius), 0.0F).setColor(inColor);
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
   }

   public void drawTooltip(Font textRenderer, int x, int y, List<ClientTooltipComponent> components) {
      ((DrawContextInvoker)this).owo$renderTooltipFromComponents(textRenderer, components, x, y, DefaultTooltipPositioner.INSTANCE);
   }

   public void drawInsets(int x, int y, int width, int height, Insets insets, int color) {
      this.fill(x - insets.left(), y - insets.top(), x + width + insets.right(), y, color);
      this.fill(x - insets.left(), y + height, x + width + insets.right(), y + height + insets.bottom(), color);
      this.fill(x - insets.left(), y, x, y + height, color);
      this.fill(x + width, y, x + width + insets.right(), y + height, color);
   }

   public void drawInspector(ParentComponent root, double mouseX, double mouseY, boolean onlyHovered) {
      RenderSystem.disableDepthTest();
      Minecraft client = Minecraft.getInstance();
      Font textRenderer = client.font;
      ArrayList<Component> children = new ArrayList<>();
      if (!onlyHovered) {
         root.collectDescendants(children);
      } else if (root.childAt((int)mouseX, (int)mouseY) != null) {
         children.add(root.childAt((int)mouseX, (int)mouseY));
      }

      for (Component child : children) {
         if (child instanceof ParentComponent parentComponent) {
            this.drawInsets(
               parentComponent.x(),
               parentComponent.y(),
               parentComponent.width(),
               parentComponent.height(),
               parentComponent.padding().get().inverted(),
               -1492325155
            );
         }

         Insets margins = child.margins().get();
         this.drawInsets(child.x(), child.y(), child.width(), child.height(), margins, -1476398280);
         this.drawRectOutline(child.x(), child.y(), child.width(), child.height(), -12930817);
         if (onlyHovered) {
            int inspectorX = child.x() + 1;
            int inspectorY = child.y() + child.height() + child.margins().get().bottom() + 1;
            int inspectorHeight = 9 * 2 + 4;
            if (inspectorY > client.getWindow().getGuiScaledHeight() - inspectorHeight) {
               inspectorY -= child.fullSize().height() + inspectorHeight + 1;
               if (inspectorY < 0) {
                  inspectorY = 1;
               }

               if (child instanceof ParentComponent parentComponent) {
                  inspectorX += parentComponent.padding().get().left();
                  inspectorY += parentComponent.padding().get().top();
               }
            }

            net.minecraft.network.chat.Component nameText = net.minecraft.network.chat.Component.nullToEmpty(
               child.getClass().getSimpleName() + (child.id() != null ? " '" + child.id() + "'" : "")
            );
            MutableComponent descriptor = net.minecraft.network.chat.Component.literal(
               child.x()
                  + ","
                  + child.y()
                  + " ("
                  + child.width()
                  + ","
                  + child.height()
                  + ") <"
                  + margins.top()
                  + ","
                  + margins.bottom()
                  + ","
                  + margins.left()
                  + ","
                  + margins.right()
                  + "> "
            );
            if (child instanceof ParentComponent parentComponent) {
               Insets padding = parentComponent.padding().get();
               descriptor.append(" >" + padding.top() + "," + padding.bottom() + "," + padding.left() + "," + padding.right() + "<");
            }

            int width = Math.max(textRenderer.width(nameText), textRenderer.width(descriptor));
            this.fill(inspectorX, inspectorY, inspectorX + width + 3, inspectorY + inspectorHeight, -1493172224);
            this.drawRectOutline(inspectorX, inspectorY, width + 3, inspectorHeight, -1493172224);
            this.drawString(textRenderer, nameText, inspectorX + 2, inspectorY + 2, 16777215, false);
            this.drawString(textRenderer, descriptor, inspectorX + 2, inspectorY + 9 + 2, 16777215, false);
         }
      }

      RenderSystem.enableDepthTest();
   }

   public static enum TextAnchor {
      TOP_RIGHT,
      BOTTOM_RIGHT,
      TOP_LEFT,
      BOTTOM_LEFT;
   }

   public static class UtilityScreen extends Screen {
      private static OwoUIDrawContext.UtilityScreen INSTANCE;
      private Screen linkSourceScreen = null;

      private UtilityScreen() {
         super(net.minecraft.network.chat.Component.empty());
      }

      public static OwoUIDrawContext.UtilityScreen get() {
         if (INSTANCE == null) {
            INSTANCE = new OwoUIDrawContext.UtilityScreen();
            Minecraft client = Minecraft.getInstance();
            INSTANCE.init(client, client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight());
         }

         return INSTANCE;
      }

      public void setLinkSource(Screen screen) {
         this.linkSourceScreen = screen;
      }

      public void captureLinkSource() {
         this.setLinkSource(this.minecraft.screen);
      }

      @Internal
      @Nullable
      public Screen getAndClearLinkSource() {
         Screen source = this.linkSourceScreen;
         this.linkSourceScreen = null;
         return source;
      }

      public boolean handleComponentClicked(@Nullable Style style) {
         return super.handleComponentClicked(style);
      }

      static {
         WindowResizeCallback.EVENT.register((WindowResizeCallback)(client, window) -> {
            if (INSTANCE != null) {
               INSTANCE.init(client, window.getGuiScaledWidth(), window.getGuiScaledHeight());
            }
         });
      }
   }
}

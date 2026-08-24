package snownee.jade.overlay;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import snownee.jade.Jade;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Color;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.util.ClientProxy;
import snownee.jade.util.JadeFont;

public class DisplayHelper implements IDisplayHelper {
   public static final DisplayHelper INSTANCE = new DisplayHelper();
   private static final int TEX_WIDTH = 16;
   private static final int TEX_HEIGHT = 16;
   private static final int MIN_FLUID_HEIGHT = 1;
   private static final Pattern STRIP_COLOR = Pattern.compile("(?i)§[0-9A-F]");
   public static DecimalFormat dfCommas = new DecimalFormat("0.##");
   public static final DecimalFormat[] dfCommasArray = new DecimalFormat[]{dfCommas, new DecimalFormat("0.#"), new DecimalFormat("0")};
   private static final Supplier<JadeFont> FONT = Suppliers.memoize(() -> new JadeFont(Minecraft.getInstance().font));

   private static void renderGuiItemDecorations(GuiGraphics guiGraphics, Font font, ItemStack stack, int i, int j, @Nullable String text) {
      if (!stack.isEmpty()) {
         guiGraphics.pose().pushPose();
         if (stack.getCount() != 1 || text != null) {
            String s = text == null ? INSTANCE.humanReadableNumber(stack.getCount(), "", false, null) : text;
            boolean smaller = s.length() > 3;
            float scale = smaller ? 0.5F : 0.75F;
            int x = smaller ? 32 : 22;
            int y = smaller ? 23 : 13;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            guiGraphics.pose().scale(scale, scale, 1.0F);
            int color = IThemeHelper.get().theme().text.itemAmountColor();
            guiGraphics.drawString(font, s, i + x - font.width(s), j + y, color, true);
            guiGraphics.pose().popPose();
         }

         if (stack.isBarVisible()) {
            RenderSystem.disableDepthTest();
            int k = stack.getBarWidth();
            int l = stack.getBarColor();
            int m = i + 2;
            int n = j + 13;
            guiGraphics.fill(RenderType.guiOverlay(), m, n, m + 13, n + 2, -16777216);
            guiGraphics.fill(RenderType.guiOverlay(), m, n, m + k, n + 1, l | 0xFF000000);
         }

         guiGraphics.pose().popPose();
         ClientProxy.renderItemDecorationsExtra(guiGraphics, font, stack, i, j, text);
      }
   }

   private static void setGLColorFromInt(int color) {
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      float alpha = (color >> 24 & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(red, green, blue, alpha);
   }

   private static void drawTextureWithMasking(
      Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, float maskTop, float maskRight, float zLevel
   ) {
      float uMin = textureSprite.getU0();
      float uMax = textureSprite.getU1();
      float vMin = textureSprite.getV0();
      float vMax = textureSprite.getV1();
      uMax -= maskRight / 16.0F * (uMax - uMin);
      vMax -= maskTop / 16.0F * (vMax - vMin);
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      buffer.addVertex(matrix, xCoord, yCoord + 16.0F, zLevel).setUv(uMin, vMax);
      buffer.addVertex(matrix, xCoord + 16.0F - maskRight, yCoord + 16.0F, zLevel).setUv(uMax, vMax);
      buffer.addVertex(matrix, xCoord + 16.0F - maskRight, yCoord + maskTop, zLevel).setUv(uMax, vMin);
      buffer.addVertex(matrix, xCoord, yCoord + maskTop, zLevel).setUv(uMin, vMin);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
   }

   public static void fill(GuiGraphics guiGraphics, float minX, float minY, float maxX, float maxY, int color) {
      fill(guiGraphics, RenderType.gui(), minX, minY, maxX, maxY, color);
   }

   public static void fill(GuiGraphics guiGraphics, RenderType renderType, float minX, float minY, float maxX, float maxY, int color) {
      Matrix4f matrix = guiGraphics.pose().last().pose();
      if (minX < maxX) {
         float i = minX;
         minX = maxX;
         maxX = i;
      }

      if (minY < maxY) {
         float j = minY;
         minY = maxY;
         maxY = j;
      }

      color = IWailaConfig.IConfigOverlay.applyAlpha(color, OverlayRenderer.alpha);
      VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(renderType);
      buffer.addVertex(matrix, minX, maxY, 0.0F).setColor(color);
      buffer.addVertex(matrix, maxX, maxY, 0.0F).setColor(color);
      buffer.addVertex(matrix, maxX, minY, 0.0F).setColor(color);
      buffer.addVertex(matrix, minX, minY, 0.0F).setColor(color);
      guiGraphics.flush();
   }

   @Override
   public void drawItem(GuiGraphics guiGraphics, float x, float y, ItemStack stack, float scale, @Nullable String text) {
      if (!(this.opacity() < 0.5F)) {
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(x, y, 0.0F);
         guiGraphics.pose().scale(scale, scale, scale);
         guiGraphics.renderFakeItem(stack, 0, 0);
         renderGuiItemDecorations(guiGraphics, font(), stack, 0, 0, text);
         guiGraphics.pose().popPose();
      }
   }

   @Override
   public void drawGradientRect(GuiGraphics guiGraphics, float left, float top, float width, float height, int startColor, int endColor) {
      this.drawGradientRect(guiGraphics, left, top, width, height, startColor, endColor, false);
   }

   public void drawGradientRect(GuiGraphics guiGraphics, float left, float top, float width, float height, int startColor, int endColor, boolean horizontal) {
      if (startColor != -1 || endColor != -1) {
         float zLevel = 0.0F;
         Matrix4f matrix = guiGraphics.pose().last().pose();
         startColor = IWailaConfig.IConfigOverlay.applyAlpha(startColor, this.opacity());
         endColor = IWailaConfig.IConfigOverlay.applyAlpha(endColor, this.opacity());
         VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
         if (horizontal) {
            buffer.addVertex(matrix, left + width, top, zLevel).setColor(endColor);
            buffer.addVertex(matrix, left, top, zLevel).setColor(startColor);
            buffer.addVertex(matrix, left, top + height, zLevel).setColor(startColor);
            buffer.addVertex(matrix, left + width, top + height, zLevel).setColor(endColor);
         } else {
            buffer.addVertex(matrix, left + width, top, zLevel).setColor(startColor);
            buffer.addVertex(matrix, left, top, zLevel).setColor(startColor);
            buffer.addVertex(matrix, left, top + height, zLevel).setColor(endColor);
            buffer.addVertex(matrix, left + width, top + height, zLevel).setColor(endColor);
         }

         guiGraphics.flush();
      }
   }

   @Override
   public void drawBorder(GuiGraphics guiGraphics, float minX, float minY, float maxX, float maxY, float width, int color, boolean corner) {
      fill(guiGraphics, minX + width, minY, maxX - width, minY + width, color);
      fill(guiGraphics, minX + width, maxY - width, maxX - width, maxY, color);
      if (corner) {
         fill(guiGraphics, minX, minY, minX + width, maxY, color);
         fill(guiGraphics, maxX - width, minY, maxX, maxY, color);
      } else {
         fill(guiGraphics, minX, minY + width, minX + width, maxY - width, color);
         fill(guiGraphics, maxX - width, minY + width, maxX, maxY - width, color);
      }
   }

   public void drawFluid(GuiGraphics guiGraphics, float xPosition, float yPosition, JadeFluidObject fluid, float width, float height, long capacityMb) {
      if (!fluid.isEmpty()) {
         long amount = JadeFluidObject.bucketVolume();
         MutableFloat scaledAmount = new MutableFloat((float)amount * height / (float)capacityMb);
         if (amount > 0L && scaledAmount.floatValue() < 1.0F) {
            scaledAmount.setValue(1.0F);
         }

         if (scaledAmount.floatValue() > height) {
            scaledAmount.setValue(height);
         }

         ClientProxy.getFluidSpriteAndColor(fluid, (sprite, color) -> {
            if (sprite == null) {
               float maxY = yPosition + height;
               if (color == -1) {
                  color = -1431655766;
               }

               fill(guiGraphics, xPosition, maxY - scaledAmount.floatValue(), xPosition + width, maxY, color);
            } else {
               if (this.opacity() != 1.0F) {
                  color = IWailaConfig.IConfigOverlay.applyAlpha(color, this.opacity());
               }

               this.drawTiledSprite(guiGraphics, xPosition, yPosition, width, height, color, scaledAmount.floatValue(), sprite);
            }
         });
      }
   }

   private void drawTiledSprite(
      GuiGraphics guiGraphics, float xPosition, float yPosition, float tiledWidth, float tiledHeight, int color, float scaledAmount, TextureAtlasSprite sprite
   ) {
      if (tiledWidth != 0.0F && tiledHeight != 0.0F && scaledAmount != 0.0F) {
         RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         Matrix4f matrix = guiGraphics.pose().last().pose();
         setGLColorFromInt(color);
         RenderSystem.enableBlend();
         int xTileCount = (int)(tiledWidth / 16.0F);
         float xRemainder = tiledWidth - xTileCount * 16;
         int yTileCount = (int)(scaledAmount / 16.0F);
         float yRemainder = scaledAmount - yTileCount * 16;
         float yStart = yPosition + tiledHeight;

         for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
               float width = xTile == xTileCount ? xRemainder : 16.0F;
               float height = yTile == yTileCount ? yRemainder : 16.0F;
               float x = xPosition + xTile * 16;
               float y = yStart - (yTile + 1) * 16;
               if (width > 0.0F && height > 0.0F) {
                  float maskTop = 16.0F - height;
                  float maskRight = 16.0F - width;
                  drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 0.0F);
               }
            }
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.disableBlend();
      }
   }

   @Override
   public String humanReadableNumber(double number, String unit, boolean milli) {
      return this.humanReadableNumber(number, unit, milli, dfCommas);
   }

   @Override
   public String humanReadableNumber(double number, String unit, boolean milli, @Nullable Format formatter) {
      if (Mth.equal(number, 0.0)) {
         return "0" + unit;
      } else {
         StringBuilder sb = new StringBuilder();
         boolean n = number < 0.0;
         if (n) {
            number = -number;
            sb.append('-');
         }

         if (milli && number >= 1000.0) {
            number /= 1000.0;
            milli = false;
         }

         int exp = formatter == null && number < 10000.0 ? 0 : (int)Math.log10(number) / 3;
         if (exp > 7) {
            exp = 7;
         }

         if (exp > 0) {
            number /= Math.pow(1000.0, exp);
         }

         if (formatter == null) {
            if (number < 10.0) {
               formatter = dfCommasArray[0];
            } else if (number < 100.0) {
               formatter = dfCommasArray[1];
            } else {
               formatter = dfCommasArray[2];
            }
         }

         if (formatter instanceof NumberFormat numberFormat) {
            sb.append(numberFormat.format(number));
         } else {
            sb.append(formatter.format(new Object[]{number}));
         }

         if (exp == 0) {
            if (milli) {
               sb.append('m');
            }
         } else {
            char pre = "kMGTPEZ".charAt(exp - 1);
            sb.append(pre);
         }

         sb.append(unit);
         return sb.toString();
      }
   }

   @Override
   public void drawText(GuiGraphics guiGraphics, String text, float x, float y, int color) {
      this.drawText(guiGraphics, Component.literal(text), x, y, color);
   }

   @Override
   public void drawText(GuiGraphics guiGraphics, FormattedText text, float x, float y, int color) {
      FormattedCharSequence sequence;
      if (text instanceof Component component) {
         sequence = component.getVisualOrderText();
      } else {
         sequence = Language.getInstance().getVisualOrder(text);
      }

      this.drawText(guiGraphics, sequence, x, y, color);
   }

   @Override
   public void drawText(GuiGraphics guiGraphics, FormattedCharSequence text, float x, float y, int color) {
      boolean shadow = Jade.CONFIG.get().getOverlay().getTheme().text.shadow();
      if (this.opacity() != 1.0F) {
         color = IWailaConfig.IConfigOverlay.applyAlpha(color, this.opacity());
      }

      guiGraphics.drawString(font(), text, (int)x, (int)y, color, shadow);
   }

   public void drawGradientProgress(GuiGraphics guiGraphics, float left, float top, float width, float height, float progress, int progressColor) {
      Color color = Color.rgb(progressColor);
      Color highlight = Color.hsl(color.getHue(), color.getSaturation(), Math.min(color.getLightness() + 0.2, 1.0), color.getOpacity());
      if (progress < 0.1F) {
         this.drawGradientRect(guiGraphics, left, top, width * progress, height, progressColor, highlight.toInt(), true);
      } else {
         float hlWidth = width * 0.1F;
         float normalWidth = width * progress - hlWidth;
         fill(guiGraphics, left, top, left + normalWidth, top + height, progressColor);
         this.drawGradientRect(guiGraphics, left + normalWidth, top, hlWidth, height, progressColor, highlight.toInt(), true);
      }
   }

   @Override
   public MutableComponent stripColor(Component component) {
      MutableComponent mutableComponent = Component.empty();
      component.visit((style, string) -> {
         if (!string.isEmpty()) {
            MutableComponent literal = Component.literal(STRIP_COLOR.matcher(string).replaceAll(""));
            literal.withStyle(style.withColor((TextColor)null));
            mutableComponent.append(literal);
         }

         return Optional.empty();
      }, Style.EMPTY);
      return mutableComponent;
   }

   @Override
   public void blitSprite(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int i, int j, int k, int l) {
      RenderSystem.enableBlend();
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.opacity());
      guiGraphics.blitSprite(resourceLocation, i, j, k, l);
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void blitSprite(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int i, int j, int k, int l, int m) {
      RenderSystem.enableBlend();
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.opacity());
      guiGraphics.blitSprite(resourceLocation, i, j, k, l, m);
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void blitSprite(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p) {
      RenderSystem.enableBlend();
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.opacity());
      guiGraphics.blitSprite(resourceLocation, i, j, k, l, m, n, o, p);
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void blitSprite(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
      RenderSystem.enableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.opacity());
      guiGraphics.blitSprite(resourceLocation, i, j, k, l, m, n, o, p, q);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public float opacity() {
      return OverlayRenderer.alpha;
   }

   public static Font font() {
      return FONT.get();
   }

   static {
      for (DecimalFormat format : dfCommasArray) {
         format.setRoundingMode(RoundingMode.DOWN);
      }
   }
}

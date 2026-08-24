package snownee.jade.impl.ui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice.Border;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.Jade;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.theme.Theme;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.IBoxElement;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.MessageType;
import snownee.jade.api.ui.ScreenDirection;
import snownee.jade.api.ui.TooltipRect;
import snownee.jade.gui.PreviewOptionsScreen;
import snownee.jade.impl.Tooltip;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.overlay.OverlayRenderer;
import snownee.jade.overlay.WailaTickHandler;
import snownee.jade.track.ProgressTrackInfo;
import snownee.jade.util.ClientProxy;

public class BoxElement extends Element implements IBoxElement {
   private final Tooltip tooltip;
   private final BoxStyle style;
   private int[] padding;
   private IElement icon;
   private float boxProgress;
   private MessageType boxProgressType;
   private ProgressTrackInfo track;
   private Vec2 contentSize = Vec2.ZERO;

   public BoxElement(Tooltip tooltip, BoxStyle style) {
      this.tooltip = Objects.requireNonNull(tooltip);
      this.style = Objects.requireNonNull(style);
   }

   private static void chase(TooltipRect rect, ToIntFunction<Rect2i> getter, IntConsumer setter) {
      if (Jade.CONFIG.get().getOverlay().getAnimation()) {
         int source = getter.applyAsInt(rect.rect);
         int target = getter.applyAsInt(rect.expectedRect);
         float diff = target - source;
         if (diff == 0.0F) {
            return;
         }

         float delta = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks() * 2.0F;
         if (delta == 0.0F) {
            diff = diff > 0.0F ? 1.0F : -1.0F;
         } else {
            if (delta < 1.0F) {
               diff *= delta;
            }

            if (Mth.abs(diff) < 1.0F) {
               diff = diff > 0.0F ? 1.0F : -1.0F;
            }
         }

         setter.accept((int)(source + diff));
      } else {
         setter.accept(getter.applyAsInt(rect.expectedRect));
      }
   }

   private static int calculateMargin(int margin1, int margin2) {
      if (margin1 >= 0 && margin2 >= 0) {
         return Math.max(margin1, margin2);
      } else {
         return margin1 < 0 && margin2 < 0 ? Math.min(margin1, margin2) : margin1 + margin2;
      }
   }

   @Override
   public Vec2 getSize() {
      if (this.tooltip.isEmpty()) {
         return Vec2.ZERO;
      } else {
         float width = 0.0F;
         float height = 0.0F;
         int lineCount = this.tooltip.lines.size();
         Tooltip.Line line = (Tooltip.Line)this.tooltip.lines.getFirst();

         for (int i = 0; i < lineCount; i++) {
            Vec2 size = line.size();
            width = Math.max(width, size.x);
            height += size.y;
            if (i < lineCount - 1) {
               int marginBottom = line.marginBottom;
               line = this.tooltip.lines.get(i + 1);
               height += calculateMargin(marginBottom, line.marginTop);
            }
         }

         this.contentSize = new Vec2(width, height);
         if (this.icon != null) {
            Vec2 size = this.icon.getCachedSize();
            width += size.x + 3.0F;
            height = Math.max(height, size.y);
         }

         width += this.padding(ScreenDirection.LEFT) + this.padding(ScreenDirection.RIGHT);
         height += this.padding(ScreenDirection.UP) + this.padding(ScreenDirection.DOWN);
         width = Math.max(width, 0.0F);
         height = Math.max(height, 0.0F);
         if (this.icon != null && this.icon.getCachedSize().y > this.contentSize.y) {
            this.setPadding(ScreenDirection.UP, this.padding(ScreenDirection.UP) + (int)(this.icon.getCachedSize().y - this.contentSize.y) / 2);
         }

         return new Vec2(width, height);
      }
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
      if (!this.tooltip.isEmpty()) {
         RenderSystem.enableBlend();
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(x, y, 0.0F);
         float alpha = IDisplayHelper.get().opacity();
         if (JadeIds.ROOT.equals(this.getTag())) {
            alpha *= IWailaConfig.get().getOverlay().getAlpha();
         }

         if (alpha > 0.0F) {
            this.style.render(guiGraphics, this, 0.0F, 0.0F, maxX - x, maxY - y, alpha);
         }

         if (this.boxProgressType != null) {
            float left = this.style.boxProgressOffset(ScreenDirection.LEFT);
            float width = maxX - x - left;
            float top = maxY - y - 1.0F + this.style.boxProgressOffset(ScreenDirection.UP) + this.style.borderWidth();
            float height = 1.0F + this.style.boxProgressOffset(ScreenDirection.DOWN);
            float progress = this.boxProgress;
            if (this.track == null && this.tag != null) {
               this.track = WailaTickHandler.instance()
                  .progressTracker
                  .getOrCreate(this.tag, ProgressTrackInfo.class, () -> new ProgressTrackInfo(false, this.boxProgress, 0.0F));
            }

            if (this.track != null) {
               this.track.setProgress(progress);
               this.track.update(Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
               progress = this.track.getSmoothProgress();
            }

            ((DisplayHelper)IDisplayHelper.get())
               .drawGradientProgress(guiGraphics, left, top, width, height, progress, this.style.boxProgressColors.get(this.boxProgressType));
         }

         float contentLeft = this.padding(ScreenDirection.LEFT);
         float contentTop = this.padding(ScreenDirection.UP);
         if (this.icon != null) {
            Vec2 iconSize = this.icon.getCachedSize();
            Vec2 offset = this.icon.getTranslation();
            float offsetY = offset.y;
            float min = contentTop + this.padding(ScreenDirection.DOWN) + iconSize.y;
            IWailaConfig.IconMode iconMode = IWailaConfig.get().getOverlay().getIconMode();
            if (iconMode == IWailaConfig.IconMode.TOP && min < this.getCachedSize().y) {
               offsetY += contentTop;
            } else {
               offsetY += (this.size.y - iconSize.y) / 2.0F;
            }

            float offsetX = contentLeft + offset.x;
            Tooltip.drawDebugBorder(guiGraphics, offsetX, offsetY, this.icon);
            this.icon.render(guiGraphics, offsetX, offsetY, offsetX + iconSize.x, offsetY + iconSize.y);
            contentLeft += iconSize.x + 3.0F;
         }

         float lineTop = contentTop;
         int lineCount = this.tooltip.lines.size();
         Tooltip.Line line = (Tooltip.Line)this.tooltip.lines.getFirst();

         for (int i = 0; i < lineCount; i++) {
            Vec2 lineSize = line.size();
            line.render(guiGraphics, contentLeft, lineTop, maxX - x - this.padding(ScreenDirection.RIGHT), lineTop + lineSize.y);
            if (i < lineCount - 1) {
               int marginBottom = line.marginBottom;
               line = this.tooltip.lines.get(i + 1);
               lineTop += lineSize.y + calculateMargin(marginBottom, line.marginTop);
            }
         }

         if (this.tooltip.sneakyDetails) {
            lineTop = OverlayRenderer.ticks / 5.0F % 8.0F - 2.0F;
            if (lineTop <= 4.0F) {
               alpha = 1.0F - Math.abs(lineTop) / 2.0F;
               if (alpha > 0.016) {
                  guiGraphics.pose().pushPose();
                  lineTop += this.size.y - 6.0F;
                  float arrowLeft = contentLeft + (this.contentSize.x - DisplayHelper.font().width("▾") + 1.0F) / 2.0F;
                  guiGraphics.pose().translate(arrowLeft, lineTop, 0.0F);
                  int color = IWailaConfig.IConfigOverlay.applyAlpha(IThemeHelper.get().theme().text.colors().info(), alpha);
                  DisplayHelper.INSTANCE.drawText(guiGraphics, "▾", 0.0F, 0.0F, color);
                  guiGraphics.pose().popPose();
               }
            }
         }

         Tooltip.drawDebugBorder(guiGraphics, 0.0F, 0.0F, this);
         guiGraphics.pose().popPose();
      }
   }

   @Nullable
   @Override
   public String getMessage() {
      return this.tooltip.isEmpty() ? null : this.tooltip.getMessage();
   }

   public Tooltip getTooltip() {
      return this.tooltip;
   }

   @Override
   public void setBoxProgress(MessageType type, float progress) {
      this.boxProgress = progress;
      this.boxProgressType = type;
   }

   @Override
   public float getBoxProgress() {
      return this.boxProgressType == null ? 0.0F / 0.0F : this.boxProgress;
   }

   @Override
   public void clearBoxProgress() {
      this.boxProgress = 0.0F;
      this.boxProgressType = null;
   }

   @Nullable
   @Override
   public IElement getIcon() {
      return this.icon;
   }

   @Override
   public void setIcon(@Nullable IElement icon) {
      this.icon = icon;
   }

   public void setThemeIcon(@Nullable IElement icon, Theme theme) {
      IWailaConfig.IConfigOverlay overlay = IWailaConfig.get().getOverlay();
      if (overlay.shouldShowIcon()) {
         if (icon == null) {
            this.setIcon(null);
         } else {
            IWailaConfig.IconMode iconMode = overlay.getIconMode();
            if (iconMode == IWailaConfig.IconMode.INLINE) {
               if (icon instanceof ItemStackElement itemStackElement) {
                  IElement newIcon = IElementHelper.get().smallItem(itemStackElement.getItem()).tag(JadeIds.CORE_ROOT_ICON);
                  newIcon.size(new Vec2(newIcon.getCachedSize().x + 1.0F, newIcon.getCachedSize().y - 1.0F));
                  this.tooltip.replace(JadeIds.CORE_OBJECT_NAME, (UnaryOperator<List<List<IElement>>>)(list -> {
                     if (!list.isEmpty()) {
                        ((List)list.getFirst()).addFirst(newIcon);
                     }

                     return list;
                  }));
               }
            } else {
               if (theme.iconSlotSprite != null) {
                  if (theme.iconSlotSpriteCache == null) {
                     GuiSpriteManager guiSprites = Minecraft.getInstance().getGuiSprites();
                     TextureAtlasSprite textureAtlasSprite = guiSprites.getSprite(theme.iconSlotSprite);
                     GuiSpriteScaling scaling = guiSprites.getSpriteScaling(textureAtlasSprite);
                     int[] padding = new int[4];
                     Arrays.fill(padding, theme.iconSlotInflation);
                     if (scaling instanceof NineSlice nineSlice) {
                        Border border = nineSlice.border();
                        padding[0] += border.top();
                        padding[1] += border.right();
                        padding[2] += border.bottom();
                        padding[3] += border.left();
                     }

                     theme.iconSlotSpriteCache = new BoxElement(new Tooltip(), BoxStyle.getSprite(theme.iconSlotSprite, padding));
                  }

                  ITooltip tooltip1 = theme.iconSlotSpriteCache.getTooltip();
                  tooltip1.clear();
                  tooltip1.add(icon);
                  icon = theme.iconSlotSpriteCache.size(null);
               }

               icon.tag(JadeIds.CORE_ROOT_ICON);
               this.setIcon(icon);
            }
         }
      }
   }

   public void updateExpectedRect(TooltipRect rect) {
      Window window = Minecraft.getInstance().getWindow();
      IWailaConfig.IConfigOverlay overlay = Jade.CONFIG.get().getOverlay();
      Vec2 size = this.getCachedSize();
      float x = window.getGuiScaledWidth() * overlay.tryFlip(overlay.getOverlayPosX());
      float y = window.getGuiScaledHeight() * (1.0F - overlay.getOverlayPosY());
      float width = size.x;
      float height = size.y;
      if (this.style.hasRoundCorner()) {
         x++;
         y++;
         width += 2.0F;
         height += 2.0F;
      }

      rect.scale = overlay.getOverlayScale();
      float thresholdHeight = window.getGuiScaledHeight() * overlay.getAutoScaleThreshold();
      if (size.y * rect.scale > thresholdHeight) {
         rect.scale = Math.max(rect.scale * 0.5F, thresholdHeight / size.y);
      }

      Rect2i expectedRect = rect.expectedRect;
      expectedRect.setWidth((int)(width * rect.scale));
      expectedRect.setHeight((int)(height * rect.scale));
      expectedRect.setX((int)(x - expectedRect.getWidth() * overlay.tryFlip(overlay.getAnchorX())));
      expectedRect.setY((int)(y - expectedRect.getHeight() * overlay.getAnchorY()));
      if (!PreviewOptionsScreen.isAdjustingPosition()) {
         IWailaConfig.BossBarOverlapMode mode = Jade.CONFIG.get().getGeneral().getBossBarOverlapMode();
         if (mode == IWailaConfig.BossBarOverlapMode.PUSH_DOWN) {
            Rect2i bossBarRect = ClientProxy.getBossBarRect();
            if (bossBarRect != null) {
               width = expectedRect.getWidth();
               height = expectedRect.getHeight();
               int rw = bossBarRect.getWidth();
               int rh = bossBarRect.getHeight();
               x = expectedRect.getX();
               y = expectedRect.getY();
               int rx = bossBarRect.getX();
               int ry = bossBarRect.getY();
               rw += rx;
               rh += ry;
               width += x;
               height += y;
               if (rw > x && rh > y && width > rx && height > ry) {
                  expectedRect.setY(bossBarRect.getHeight());
               }
            }
         }
      }
   }

   public void updateRect(TooltipRect rect) {
      Rect2i src = rect.rect;
      if (src.getWidth() == 0) {
         src.setX(rect.expectedRect.getX());
         src.setY(rect.expectedRect.getY());
         src.setWidth(rect.expectedRect.getWidth());
         src.setHeight(rect.expectedRect.getHeight());
      } else {
         chase(rect, Rect2i::getX, src::setX);
         chase(rect, Rect2i::getY, src::setY);
         chase(rect, Rect2i::getWidth, src::setWidth);
         chase(rect, Rect2i::getHeight, src::setHeight);
      }
   }

   @Override
   public int padding(ScreenDirection direction) {
      return this.padding != null ? this.padding[direction.ordinal()] : this.style.padding(direction);
   }

   @Override
   public void setPadding(ScreenDirection direction, int value) {
      if (this.padding == null) {
         this.padding = (int[])this.style.padding.clone();
      }

      this.padding[direction.ordinal()] = value;
   }

   @Override
   public BoxStyle getStyle() {
      return this.style;
   }
}

package snownee.jade.gui;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import snownee.jade.Jade;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.gui.config.OptionsList;
import snownee.jade.overlay.OverlayRenderer;

public abstract class PreviewOptionsScreen extends BaseOptionsScreen {
   private boolean adjustingPosition;
   private boolean adjustDragging;
   private double dragOffsetX;
   private double dragOffsetY;

   public PreviewOptionsScreen(Screen parent, Component title) {
      super(parent, title);
   }

   public static boolean isAdjustingPosition() {
      return Minecraft.getInstance().screen instanceof PreviewOptionsScreen screen && screen.adjustingPosition;
   }

   private static float calculateAnchor(float center, float size, int rectSize) {
      float anchor = center / size;
      if (anchor < 0.25F) {
         return 0.0F;
      } else if (anchor > 0.75F) {
         return 1.0F;
      } else {
         float halfRectSize = rectSize / 2.0F;
         float tolerance = Math.min(15.0F, halfRectSize / 2.0F - 3.0F);
         if (Math.abs(center + halfRectSize - size / 2.0F) < tolerance) {
            return 1.0F;
         } else {
            return Math.abs(center - halfRectSize - size / 2.0F) < tolerance ? 0.0F : 0.5F;
         }
      }
   }

   private static float maybeSnap(float value) {
      return !Screen.hasControlDown() && value > 0.475F && value < 0.525F ? 0.5F : value;
   }

   @Override
   protected void init() {
      Objects.requireNonNull(this.minecraft);
      super.init();
      if (this.minecraft.level != null) {
         CycleButton<Boolean> previewButton = CycleButton.booleanBuilder(OptionsList.OPTION_ON, OptionsList.OPTION_OFF)
            .create(10, this.saveButton.getY(), 85, 20, Component.translatable("gui.jade.preview"), (button, value) -> {
               Jade.CONFIG.get().getGeneral().previewOverlay = value;
               this.saver.run();
            });
         previewButton.setValue(Jade.CONFIG.get().getGeneral().previewOverlay);
         this.addRenderableWidget(previewButton);
      }
   }

   public boolean forcePreviewOverlay() {
      Objects.requireNonNull(this.minecraft);
      if (this.adjustingPosition) {
         return true;
      } else if (this.isDragging() && this.options != null) {
         OptionsList.Entry entry = (OptionsList.Entry)this.options.getSelected();
         return entry != null && entry.getFirstWidget() != null ? this.options.forcePreview.contains(entry) : false;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int p_94697_) {
      if (this.adjustingPosition) {
         Objects.requireNonNull(this.minecraft);
         Rect2i rect = OverlayRenderer.rect.expectedRect;
         if (rect.contains((int)mouseX, (int)mouseY)) {
            this.setDragging(true);
            this.adjustDragging = true;
            float centerX = rect.getX() + rect.getWidth() / 2.0F;
            float centerY = rect.getY() + rect.getHeight() / 2.0F;
            this.dragOffsetX = mouseX - centerX;
            this.dragOffsetY = mouseY - centerY;
         } else {
            this.adjustingPosition = false;
            this.adjustDragging = false;
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, p_94697_);
      }
   }

   public boolean mouseReleased(double d, double e, int i) {
      if (this.adjustingPosition) {
         this.setDragging(false);
         this.adjustDragging = false;
         return true;
      } else {
         return super.mouseReleased(d, e, i);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
      return this.adjustingPosition ? true : super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
   }

   public boolean keyPressed(int i, int j, int k) {
      return this.adjustingPosition ? true : super.keyPressed(i, j, k);
   }

   public boolean keyReleased(int i, int j, int k) {
      Objects.requireNonNull(this.minecraft);
      if (this.adjustingPosition) {
         if (i == 256) {
            this.adjustingPosition = false;
            this.adjustDragging = false;
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

         return true;
      } else {
         return super.keyReleased(i, j, k);
      }
   }

   public boolean mouseDragged(double d, double e, int i, double f, double g) {
      if (this.adjustingPosition && this.adjustDragging) {
         float centerX = (float)d - (float)this.dragOffsetX;
         float centerY = (float)e - (float)this.dragOffsetY;
         Rect2i rect = OverlayRenderer.rect.expectedRect;
         int rectWidth = rect.getWidth();
         int rectHeight = rect.getHeight();
         float anchorX = calculateAnchor(centerX, this.width, rectWidth);
         float anchorY = calculateAnchor(centerY, this.height, rectHeight);
         float posX = (centerX + rectWidth * (anchorX - 0.5F)) / this.width;
         float posY = 1.0F - (centerY + rectHeight * (anchorY - 0.5F)) / this.height;
         IWailaConfig.IConfigOverlay config = IWailaConfig.get().getOverlay();
         config.setOverlayPosX(config.tryFlip(maybeSnap(posX)));
         config.setOverlayPosY(maybeSnap(posY));
         config.setAnchorX(config.tryFlip(anchorX));
         config.setAnchorY(anchorY);
         return true;
      } else {
         return super.mouseDragged(d, e, i, f, g);
      }
   }

   @Override
   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      if (this.adjustingPosition) {
         super.render(guiGraphics, 2147483647, 2147483647, partialTicks);
         guiGraphics.fill(0, 0, this.width, this.height, 50, -2136298838);
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(0.0F, 0.0F, 50.0F);
         guiGraphics.drawCenteredString(this.font, Component.translatable("config.jade.overlay_pos.exit"), this.width / 2, this.height / 2 - 7, 16777215);
         guiGraphics.pose().popPose();
         IWailaConfig.IConfigOverlay config = IWailaConfig.get().getOverlay();
         Rect2i rect = OverlayRenderer.rect.expectedRect;
         if (IWailaConfig.get().getGeneral().isDebug()) {
            int anchorX = (int)(rect.getX() + rect.getWidth() * config.getAnchorX());
            int anchorY = (int)(rect.getY() + rect.getHeight() * config.getAnchorY());
            guiGraphics.fill(anchorX - 2, anchorY - 2, anchorX + 1, anchorY + 1, 1000, -65536);
         }

         if (config.getOverlayPosX() == 0.5F) {
            guiGraphics.fill(this.width / 2, rect.getY() - 5, this.width / 2 + 1, rect.getY() + rect.getHeight() + 4, 1000, -16776961);
         }

         if (config.getOverlayPosY() == 0.5F) {
            guiGraphics.fill(rect.getX() - 5, this.height / 2, rect.getX() + rect.getWidth() + 4, this.height / 2 + 1, 1000, -16776961);
         }

         this.deferredTooltipRendering = null;
      } else {
         super.render(guiGraphics, mouseX, mouseY, partialTicks);
      }
   }

   public void startAdjustingPosition() {
      this.adjustingPosition = true;
   }

   protected void updateNarratedWidget(NarrationElementOutput narrationElementOutput) {
      if (this.adjustingPosition) {
         narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.jade.adjusting_position"));
      } else {
         super.updateNarratedWidget(narrationElementOutput);
      }
   }

   protected boolean shouldNarrateNavigation() {
      return !this.adjustingPosition;
   }
}

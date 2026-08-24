package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.Blaze3D;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class OptionDescriptionWidget extends net.minecraft.client.gui.components.AbstractWidget {
   private static final int AUTO_SCROLL_TIMER = 1500;
   private static final float AUTO_SCROLL_SPEED = 1.0F;
   @Nullable
   private DescriptionWithName description;
   private List<FormattedCharSequence> wrappedText;
   private static final Minecraft minecraft = Minecraft.getInstance();
   private static final Font font;
   private Supplier<ScreenRectangle> dimensions;
   private float targetScrollAmount;
   private float currentScrollAmount;
   private int maxScrollAmount;
   private int descriptionY;
   private int lastInteractionTime;
   private boolean scrollingBackward;

   public OptionDescriptionWidget(Supplier<ScreenRectangle> dimensions, @Nullable DescriptionWithName description) {
      super(0, 0, 0, 0, (Component)(description == null ? Component.empty() : description.name()));
      this.dimensions = dimensions;
      this.setOptionDescription(description);
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      if (this.description != null) {
         this.currentScrollAmount = Mth.lerp(delta * 0.5F, this.currentScrollAmount, this.targetScrollAmount);
         ScreenRectangle dimensions = this.dimensions.get();
         this.setX(dimensions.left());
         this.setY(dimensions.top());
         this.width = dimensions.width();
         this.height = dimensions.height();
         int y = this.getY();
         int nameWidth = font.width(this.description.name());
         if (nameWidth > this.getWidth()) {
            renderScrollingString(graphics, font, this.description.name(), this.getX(), y, this.getX() + this.getWidth(), y + 9, -1);
         } else {
            graphics.drawString(font, this.description.name(), this.getX(), y, -1);
         }

         y += 5 + 9;
         graphics.enableScissor(this.getX(), y, this.getX() + this.getWidth(), this.getY() + this.getHeight());
         y -= (int)this.currentScrollAmount;
         if (this.description.description().image().isDone()) {
            Optional<ImageRenderer> image = this.description.description().image().join();
            if (image.isPresent()) {
               y += image.get().render(graphics, this.getX(), y, this.getWidth(), delta) + 5;
            }
         }

         if (this.wrappedText == null) {
            this.wrappedText = font.split(this.description.description().text(), this.getWidth());
         }

         this.descriptionY = y;

         for (FormattedCharSequence line : this.wrappedText) {
            graphics.drawString(font, line, this.getX(), y, -1);
            y += 9;
         }

         graphics.disableScissor();
         this.maxScrollAmount = Math.max(0, y + (int)this.currentScrollAmount - this.getY() - this.getHeight());
         if (this.isHoveredOrFocused()) {
            this.lastInteractionTime = this.currentTimeMS();
         }

         Style hoveredStyle = this.getDescStyle(mouseX, mouseY);
         if (hoveredStyle != null && hoveredStyle.getHoverEvent() != null) {
            graphics.renderComponentHoverEffect(font, hoveredStyle, mouseX, mouseY);
         }

         if (this.isFocused()) {
            graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), -1);
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.onMouseClicked(mouseX, mouseY);
   }

   protected boolean onMouseClicked(double mouseX, double mouseY) {
      Style clickedStyle = this.getDescStyle((int)mouseX, (int)mouseY);
      if (clickedStyle == null || clickedStyle.getClickEvent() == null) {
         return false;
      } else if (minecraft.screen.handleComponentClicked(clickedStyle)) {
         this.playDownSound(minecraft.getSoundManager());
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
      if (this.isMouseOver(mouseX, mouseY)) {
         this.targetScrollAmount = Mth.clamp(this.targetScrollAmount - (int)vertical * 10, 0.0F, this.maxScrollAmount);
         this.lastInteractionTime = this.currentTimeMS();
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.onKeyPressed(keyCode);
   }

   protected boolean onKeyPressed(int keyCode) {
      if (this.isFocused()) {
         switch (keyCode) {
            case 264:
               this.targetScrollAmount = Mth.clamp(this.targetScrollAmount + 10.0F, 0.0F, this.maxScrollAmount);
               break;
            case 265:
               this.targetScrollAmount = Mth.clamp(this.targetScrollAmount - 10.0F, 0.0F, this.maxScrollAmount);
               break;
            default:
               return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public void tick() {
      if (this.description != null) {
         this.description.description().image().getNow(Optional.empty()).ifPresent(ImageRenderer::tick);
      }

      float pxPerTick = 0.05F * 9.0F;
      if (this.maxScrollAmount > 0 && this.currentTimeMS() - this.lastInteractionTime > 1500) {
         if (this.scrollingBackward) {
            pxPerTick *= -1.0F;
            if (this.targetScrollAmount + pxPerTick < 0.0F) {
               this.scrollingBackward = false;
               this.lastInteractionTime = this.currentTimeMS();
            }
         } else if (this.targetScrollAmount + pxPerTick > this.maxScrollAmount) {
            this.scrollingBackward = true;
            this.lastInteractionTime = this.currentTimeMS();
         }

         this.targetScrollAmount = Mth.clamp(this.targetScrollAmount + pxPerTick, 0.0F, this.maxScrollAmount);
      }
   }

   private Style getDescStyle(int mouseX, int mouseY) {
      boolean clicked = this.clicked(mouseX, mouseY);
      if (!clicked) {
         return null;
      } else {
         int x = mouseX - this.getX();
         int y = mouseY - this.descriptionY;
         if (x < 0 || x > this.getX() + this.getWidth()) {
            return null;
         } else if (y >= 0 && y <= this.getY() + this.getHeight()) {
            int line = y / 9;
            return line >= this.wrappedText.size() ? null : font.getSplitter().componentStyleAtWidth(this.wrappedText.get(line), x);
         } else {
            return null;
         }
      }
   }

   protected void updateWidgetNarration(NarrationElementOutput builder) {
      if (this.description != null) {
         builder.add(NarratedElementType.TITLE, this.description.name());
         builder.add(NarratedElementType.HINT, this.description.description().text());
      }
   }

   public void setOptionDescription(DescriptionWithName description) {
      this.description = description;
      this.wrappedText = null;
      this.targetScrollAmount = 0.0F;
      this.currentScrollAmount = 0.0F;
      this.lastInteractionTime = this.currentTimeMS();
   }

   private int currentTimeMS() {
      return (int)(Blaze3D.getTime() * 1000.0);
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent event) {
      return null;
   }

   static {
      font = minecraft.font;
   }
}

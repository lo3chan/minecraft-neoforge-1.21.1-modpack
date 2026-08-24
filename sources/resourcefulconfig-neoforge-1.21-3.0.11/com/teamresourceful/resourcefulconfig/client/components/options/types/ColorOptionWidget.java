package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.components.options.types.color.AlphaSelector;
import com.teamresourceful.resourcefulconfig.client.components.options.types.color.EyedropperButton;
import com.teamresourceful.resourcefulconfig.client.components.options.types.color.HsbColor;
import com.teamresourceful.resourcefulconfig.client.components.options.types.color.HsbState;
import com.teamresourceful.resourcefulconfig.client.components.options.types.color.HueSelector;
import com.teamresourceful.resourcefulconfig.client.components.options.types.color.PresetsSelector;
import com.teamresourceful.resourcefulconfig.client.components.options.types.color.RecentColorStorage;
import com.teamresourceful.resourcefulconfig.client.components.options.types.color.SaturationBrightnessSelector;
import com.teamresourceful.resourcefulconfig.client.screens.base.CloseableScreen;
import com.teamresourceful.resourcefulconfig.client.screens.base.OverlayScreen;
import com.teamresourceful.resourcefulconfig.client.utils.State;
import java.util.Locale;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class ColorOptionWidget extends BaseWidget {
   private static final int SIZE = 16;
   private static final int PADDING = 4;
   private static final int SPACING = 2;
   private final int[] presets;
   private final boolean hasAlpha;
   private final IntSupplier getter;
   private final IntConsumer setter;

   public ColorOptionWidget(int[] presets, boolean hasAlpha, IntSupplier getter, IntConsumer setter) {
      super(16, 16);
      this.presets = presets;
      this.hasAlpha = hasAlpha;
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.BUTTON, this.getX(), this.getY(), this.height, this.height);
      graphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.height - 1, this.getY() + this.height - 1, this.getter.getAsInt());
   }

   public void onClick(double d, double e) {
      Minecraft.getInstance().setScreen(new ColorOptionWidget.PresetsOverlay(this));
   }

   public static enum PresetType implements Translatable {
      RECENTS,
      DEFAULTS,
      MC_COLORS;

      private static final ColorOptionWidget.PresetType[] VALUES = values();
      private static final ColorOptionWidget.PresetType[] WITHOUT_DEFAULT = new ColorOptionWidget.PresetType[]{RECENTS, MC_COLORS};

      @Override
      public String getTranslationKey() {
         return "rconfig.color.preset." + this.name().toLowerCase(Locale.ROOT);
      }
   }

   private static class PresetsOverlay extends OverlayScreen implements CloseableScreen {
      private final ColorOptionWidget widget;
      private final HsbState state;
      private final State<ColorOptionWidget.PresetType> type;
      private int x;
      private int y;
      private int width;
      private int height;

      protected PresetsOverlay(ColorOptionWidget widget) {
         super(Minecraft.getInstance().screen);
         this.widget = widget;
         this.state = new HsbState(HsbColor.fromRgb(widget.getter.getAsInt()), color -> widget.setter.accept(color.toRgba()));
         this.type = State.of(
            widget.presets.length == 0
               ? (RecentColorStorage.hasValues() ? ColorOptionWidget.PresetType.RECENTS : ColorOptionWidget.PresetType.MC_COLORS)
               : ColorOptionWidget.PresetType.DEFAULTS
         );
      }

      @Override
      protected void init() {
         GridLayout layout = new GridLayout().spacing(2);
         layout.addChild(new SaturationBrightnessSelector(100, 50, this.state), 0, 0);
         layout.addChild(new HueSelector(100, 10, this.state), 1, 0);
         if (this.widget.hasAlpha) {
            layout.addChild(new AlphaSelector(100, 10, this.state), 2, 0);
         }

         LinearLayout presets = LinearLayout.horizontal().spacing(4);
         presets.addChild(new EyedropperButton(this.state));
         presets.addChild(
            DropdownWidget.of(
               this.widget.presets.length == 0 ? ColorOptionWidget.PresetType.WITHOUT_DEFAULT : ColorOptionWidget.PresetType.VALUES, this.type, this.type
            )
         );
         layout.addChild(presets, 3, 0);
         layout.addChild(new PresetsSelector(100, this.widget.presets, this.type, this.state, this.widget.hasAlpha), 4, 0);
         layout.arrangeElements();
         int windowHeight = this.getRectangle().height();
         int y = this.widget.getY() + this.widget.getHeight() + 2 + layout.getHeight() + 8 > windowHeight
            ? this.widget.getY() - layout.getHeight() - 8 - 2
            : this.widget.getY() + this.widget.getHeight() + 2;
         layout.setPosition(this.widget.getX() + 4, y + 4);
         layout.visitWidgets(x$0 -> {
            AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
         });
         this.x = this.widget.getX();
         this.y = y;
         this.width = layout.getWidth() + 8;
         this.height = layout.getHeight() + 8;
      }

      @Override
      public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         super.renderBackground(graphics, mouseX, mouseY, partialTicks);
         graphics.blitSprite(ModSprites.ACCENT, this.x, this.y, this.width, this.height);
         graphics.blitSprite(ModSprites.BUTTON, this.x + 1, this.y + 1, this.width - 2, this.height - 2);
      }

      @Override
      public void onClosed(@Nullable Screen replacement) {
         if (!(replacement instanceof OverlayScreen)) {
            if (this.state.hasChanged()) {
               RecentColorStorage.add(this.state.get());
            }
         }
      }

      public boolean isMouseOver(double mouseX, double mouseY) {
         return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (button == 0 && !this.isMouseOver(mouseX, mouseY)) {
            this.onClose();
            return false;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }
}

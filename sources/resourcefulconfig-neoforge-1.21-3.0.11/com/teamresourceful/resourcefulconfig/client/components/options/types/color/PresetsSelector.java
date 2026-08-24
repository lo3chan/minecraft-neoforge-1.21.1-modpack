package com.teamresourceful.resourcefulconfig.client.components.options.types.color;

import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.components.options.types.ColorOptionWidget;
import com.teamresourceful.resourcefulconfig.client.utils.State;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PresetsSelector extends BaseWidget {
   private static final List<HsbColor> MINECRAFT_COLORS = List.of(
      HsbColor.fromRgb(-43691),
      HsbColor.fromRgb(-22016),
      HsbColor.fromRgb(-171),
      HsbColor.fromRgb(-11141291),
      HsbColor.fromRgb(5636095),
      HsbColor.fromRgb(-11184641),
      HsbColor.fromRgb(-43521),
      HsbColor.fromRgb(-1),
      HsbColor.fromRgb(-5592406),
      HsbColor.fromRgb(-5636096),
      HsbColor.fromRgb(-16733696),
      HsbColor.fromRgb(-16733526),
      HsbColor.fromRgb(-16777046),
      HsbColor.fromRgb(-5635926),
      HsbColor.fromRgb(-11184811),
      HsbColor.fromRgb(-16777216)
   );
   private final int[] presets;
   private final State<ColorOptionWidget.PresetType> type;
   private final HsbState state;
   private final boolean withAlpha;
   private Collection<HsbColor> colors = new ArrayList<>();
   private ColorOptionWidget.PresetType lastType;

   public PresetsSelector(int width, int[] presets, State<ColorOptionWidget.PresetType> type, HsbState state, boolean withAlpha) {
      super(width, width / 8 * 2 + 4);
      this.presets = presets;
      this.type = type;
      this.state = state;
      this.getColors();
      this.withAlpha = withAlpha;
   }

   private Collection<HsbColor> getColors() {
      if (this.lastType != this.type.get()) {
         this.colors = (Collection<HsbColor>)(switch ((ColorOptionWidget.PresetType)this.type.get()) {
            case DEFAULTS -> {
               List<HsbColor> colors = new ArrayList<>();

               for (int preset : this.presets) {
                  int color = this.withAlpha ? preset : preset | 0xFF000000;
                  colors.add(HsbColor.fromRgb(color));
               }

               yield colors;
            }
            case RECENTS -> RecentColorStorage.getRecentColors(this.withAlpha);
            case MC_COLORS -> MINECRAFT_COLORS;
         });
         this.lastType = this.type.get();
      }

      return this.colors;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.ACCENT, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      graphics.blitSprite(ModSprites.BUTTON, this.getX() + 1, this.getX() + 1, this.getWidth() - 2, this.getHeight() - 2);
      int size = (this.getWidth() - 18) / 8;
      int i = 0;

      for (HsbColor color : this.getColors()) {
         if (i >= 16) {
            break;
         }

         int j = i % 8;
         int k = i / 8;
         int x = this.getX() + 3 + j * size + 2 * j;
         int y = this.getY() + 3 + k * size + 2 * k;
         int rgba = color.toRgba();
         graphics.fill(x, y, x + size, y + size, rgba);
         graphics.renderOutline(x, y, size, size, -2236963);
         if (mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
            graphics.renderOutline(x, y, size, size, -16777216);
            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
               if (!this.withAlpha) {
                  rgba &= 16777215;
               }

               MutableComponent text = Component.literal("[")
                  .withColor(rgba | 0xFF000000)
                  .append(Component.literal(String.format(Locale.ROOT, "#%06X", rgba)).withColor(-1))
                  .append(Component.literal("]"))
                  .withColor(rgba | 0xFF000000);
               screen.setTooltipForNextRenderPass(text);
            }
         }

         i++;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return false;
      } else {
         int size = (this.getWidth() - 18) / 8;
         int i = 0;

         for (HsbColor color : this.getColors()) {
            if (i >= 16) {
               break;
            }

            int j = i % 8;
            int k = i / 8;
            int x = this.getX() + 3 + j * size + 2 * j;
            int y = this.getY() + 3 + k * size + 2 * k;
            if (mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
               RecentColorStorage.add(this.state.get());
               this.lastType = null;
               this.state.set(color);
               return true;
            }

            i++;
         }

         return false;
      }
   }
}

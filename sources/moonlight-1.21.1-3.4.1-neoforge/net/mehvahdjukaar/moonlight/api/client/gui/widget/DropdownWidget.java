package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.OverlayLayer;
import net.mehvahdjukaar.moonlight.api.client.gui.Popup;
import net.mehvahdjukaar.moonlight.api.client.gui.PopupHost;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DropdownWidget extends AbstractWidget implements Popup {
   private static final int MAX_VISIBLE = 8;
   private final List<String> options;
   @Nullable
   private final Function<String, ItemStack> icon;
   private final Map<String, ItemStack> iconCache = new HashMap<>();
   private final int itemHeight;
   private String value;
   private final Consumer<String> onChange;
   private boolean open;
   private int scrollOffset;
   private List<String> filtered;
   @Nullable
   private OverlayLayer layer;
   private final EditBox searchBox;

   public DropdownWidget(int width, int height, List<String> options, @Nullable Function<String, ItemStack> icon, String value, Consumer<String> onChange) {
      super(0, 0, width, height, Component.literal(value));
      this.options = options;
      this.icon = icon;
      this.value = value;
      this.onChange = onChange;
      this.filtered = options;
      this.itemHeight = icon != null ? 18 : 14;
      this.searchBox = new EditBox(font(), 0, 0, width, height, Component.empty());
      this.searchBox.setBordered(false);
      this.searchBox.setTextColor(ConfigGuiColors.TEXT);
      this.searchBox.setResponder(query -> {
         String q = query.trim().toLowerCase(Locale.ROOT);
         this.filtered = q.isEmpty() ? options : options.stream().filter(o -> o.toLowerCase(Locale.ROOT).contains(q)).toList();
         this.scrollOffset = 0;
      });
   }

   public void setValue(String v) {
      this.value = v;
   }

   public String getValue() {
      return this.value;
   }

   private static Font font() {
      return Minecraft.getInstance().font;
   }

   private ItemStack iconFor(String id) {
      return this.iconCache.computeIfAbsent(id, this.icon);
   }

   private int valueAreaWidth() {
      return this.getWidth() - this.getHeight();
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int x = this.getX();
      int y = this.getY();
      int w = this.getWidth();
      int h = this.getHeight();
      int border = !this.open && !this.isFocused() ? -6250336 : -1;
      Font font = font();
      graphics.fill(x, y, x + w, y + h, -16777216);
      int sepX = x + w - h;
      if (this.open) {
         this.searchBox.setPosition(x + 4, y + (h - 9) / 2 + 1);
         this.searchBox.setWidth(sepX - 2 - (x + 4));
         this.searchBox.setHeight(9);
         this.searchBox.render(graphics, mouseX, mouseY, partialTick);
      } else {
         int textX = x + 4;
         if (this.icon != null) {
            graphics.renderFakeItem(this.iconFor(this.value), x + 2, y + (h - 16) / 2);
            textX = x + 2 + 18;
         }

         GuiHelper.renderScrollingText(graphics, font, Component.literal(this.value), textX, sepX - 2, y, h, ConfigGuiColors.TEXT);
      }

      graphics.fill(sepX, y, sepX + 1, y + h, border);
      graphics.drawCenteredString(font, this.open ? "▲" : "▼", sepX + h / 2, y + (h - 9) / 2 + 1, ConfigGuiColors.TEXT);
      graphics.renderOutline(x, y, w, h, border);
   }

   public void onClick(double mouseX, double mouseY) {
      if (this.open) {
         this.close();
      } else if (Minecraft.getInstance().screen instanceof PopupHost h) {
         this.layer = h.getOverlayLayer();
         this.open = true;
         this.filtered = this.options;
         this.searchBox.setValue("");
         this.searchBox.setHint(Component.literal(this.value));
         this.searchBox.setFocused(true);
         this.layer.open(this);
         int selected = this.filtered.indexOf(this.value);
         this.scrollOffset = selected < 0 ? 0 : Mth.clamp(selected - this.visibleCount() + 1, 0, this.maxScroll());
      }
   }

   public void close() {
      if (this.layer != null) {
         this.layer.close(this);
      } else {
         this.onPopupClosed();
      }
   }

   @Override
   public void onPopupClosed() {
      this.open = false;
      this.searchBox.setFocused(false);
      this.layer = null;
   }

   private int visibleCount() {
      return this.popupRect()[4];
   }

   private int maxScroll() {
      return Math.max(0, this.filtered.size() - this.visibleCount());
   }

   private int[] popupRect() {
      int w = this.getWidth();
      int x = this.getX();
      int desired = Mth.clamp(this.filtered.size(), 1, 8);
      int below = this.getY() + this.getHeight();
      int screenH = Minecraft.getInstance().getWindow().getGuiScaledHeight();
      int margin = 2;
      int fitBelow = Math.max(1, (screenH - margin - below) / this.itemHeight);
      int fitAbove = Math.max(1, (this.getY() - margin) / this.itemHeight);
      boolean down;
      if (desired <= fitBelow) {
         down = true;
      } else if (desired <= fitAbove) {
         down = false;
      } else {
         down = fitBelow >= fitAbove;
      }

      int visible = Math.min(desired, down ? fitBelow : fitAbove);
      int h = visible * this.itemHeight;
      int y = down ? below : this.getY() - h;
      return new int[]{x, y, w, h, visible};
   }

   @Override
   public void renderPopup(GuiGraphics graphics, int mouseX, int mouseY) {
      if (this.open) {
         int[] r = this.popupRect();
         int x = r[0];
         int y = r[1];
         int w = r[2];
         int h = r[3];
         Font font = font();
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 200.0F);
         graphics.fill(x, y, x + w, y + h, -15724528);
         if (this.filtered.isEmpty()) {
            graphics.drawString(
               font, Component.translatable("gui.moonlight.config.no_matches"), x + 4, y + (this.itemHeight - 9) / 2 + 1, ConfigGuiColors.DESCRIPTION
            );
         }

         int visible = r[4];
         boolean hasScrollbar = this.filtered.size() > visible;
         int textRight = x + w - (hasScrollbar ? 6 : 4);

         for (int i = 0; i < visible; i++) {
            int idx = this.scrollOffset + i;
            if (idx >= this.filtered.size()) {
               break;
            }

            String opt = this.filtered.get(idx);
            int iy = y + i * this.itemHeight;
            boolean hover = mouseX >= x && mouseX < x + w && mouseY >= iy && mouseY < iy + this.itemHeight;
            if (hover) {
               graphics.fill(x + 1, iy, x + w - 1, iy + this.itemHeight, 1090519039);
            }

            int textX = x + 4;
            if (this.icon != null) {
               graphics.renderFakeItem(this.iconFor(opt), x + 2, iy + (this.itemHeight - 16) / 2);
               textX = x + 2 + 18;
            }

            int color = opt.equals(this.value) ? ConfigGuiColors.SELECTED : ConfigGuiColors.TEXT;
            if (hover) {
               GuiHelper.renderScrollingText(graphics, font, Component.literal(opt), textX, textRight, iy, this.itemHeight, color);
            } else {
               graphics.enableScissor(textX, iy, textRight, iy + this.itemHeight);
               graphics.drawString(font, opt, textX, iy + (this.itemHeight - 9) / 2 + 1, color);
               graphics.disableScissor();
            }
         }

         if (hasScrollbar) {
            int trackX = x + w - 4;
            int thumbH = Math.max(8, h * visible / this.filtered.size());
            int thumbY = y + (h - thumbH) * this.scrollOffset / this.maxScroll();
            graphics.fill(trackX, y, trackX + 2, y + h, -16777216);
            graphics.fill(trackX, thumbY, trackX + 2, thumbY + thumbH, -5197648);
         }

         graphics.renderOutline(x, y, w, h, -1);
         graphics.pose().popPose();
      }
   }

   @Override
   public boolean popupMouseClicked(double mouseX, double mouseY, int button) {
      if (!this.open) {
         return false;
      } else if (mouseX >= this.getX() && mouseX < this.getX() + this.valueAreaWidth() && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight()) {
         this.searchBox.mouseClicked(mouseX, mouseY, button);
         return true;
      } else {
         int[] r = this.popupRect();
         int x = r[0];
         int y = r[1];
         int w = r[2];
         int h = r[3];
         if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
            int idx = this.scrollOffset + (int)((mouseY - y) / this.itemHeight);
            if (idx >= 0 && idx < this.filtered.size()) {
               this.select(this.filtered.get(idx));
            }
         }

         this.close();
         return true;
      }
   }

   @Override
   public boolean popupMouseScrolled(double mouseX, double mouseY, double delta) {
      if (this.open && this.filtered.size() > this.visibleCount()) {
         int[] r = this.popupRect();
         if (!(mouseX < r[0]) && !(mouseX >= r[0] + r[2]) && !(mouseY < r[1]) && !(mouseY >= r[1] + r[3])) {
            this.scrollOffset = Mth.clamp(this.scrollOffset - (int)Math.signum(delta), 0, this.maxScroll());
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean popupKeyPressed(int key, int scan, int mods) {
      if (!this.open) {
         return false;
      } else if (key == 256) {
         this.close();
         return true;
      } else if (key != 257 && key != 335) {
         return this.searchBox.keyPressed(key, scan, mods);
      } else {
         if (!this.filtered.isEmpty()) {
            this.select(this.filtered.contains(this.value) ? this.value : (String)this.filtered.getFirst());
         }

         this.close();
         return true;
      }
   }

   @Override
   public boolean popupCharTyped(char c, int mods) {
      return this.open && this.searchBox.charTyped(c, mods);
   }

   private void select(String v) {
      GuiHelper.playClickSound();
      this.value = v;
      this.onChange.accept(v);
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }
}

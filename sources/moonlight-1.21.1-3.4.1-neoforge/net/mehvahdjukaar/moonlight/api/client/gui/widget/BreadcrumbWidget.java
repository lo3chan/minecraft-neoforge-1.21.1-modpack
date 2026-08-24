package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class BreadcrumbWidget extends AbstractWidget {
   private static final String SEP = " › ";
   private static final String ELLIPSIS = "…";
   private final Font font;
   private final List<BreadcrumbWidget.Crumb> crumbs;
   private final Consumer<Screen> onNavigate;
   private final int[] crumbX0;
   private final int[] crumbX1;

   public BreadcrumbWidget(int x, int y, int width, int height, Font font, List<BreadcrumbWidget.Crumb> crumbs, Consumer<Screen> onNavigate) {
      super(x, y, width, height, Component.empty());
      this.font = font;
      this.crumbs = crumbs;
      this.onNavigate = onNavigate;
      this.crumbX0 = new int[crumbs.size()];
      this.crumbX1 = new int[crumbs.size()];
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      Arrays.fill(this.crumbX0, -1);
      Arrays.fill(this.crumbX1, -1);
      int x = this.getX();
      int y = this.getY();
      boolean first = true;

      for (int i : this.computeVisibleCrumbs(this.getWidth())) {
         if (!first) {
            graphics.drawString(this.font, " › ", x, y, ConfigGuiColors.CRUMB_SEPARATOR);
            x += this.font.width(" › ");
         }

         first = false;
         if (i < 0) {
            graphics.drawString(this.font, "…", x, y, ConfigGuiColors.CRUMB_SEPARATOR);
            x += this.font.width("…");
         } else {
            BreadcrumbWidget.Crumb c = this.crumbs.get(i);
            int w = this.font.width(c.label());
            this.crumbX0[i] = x;
            this.crumbX1[i] = x + w;
            boolean hover = !c.current() && this.inside(mouseX, mouseY, x, w);
            int color = c.current() ? ConfigGuiColors.CRUMB_CURRENT : (hover ? ConfigGuiColors.CRUMB_HOVER : ConfigGuiColors.CRUMB);
            graphics.drawString(this.font, c.label(), x, y, color);
            x += w;
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.active && this.visible && button == 0) {
         Screen target = this.crumbAt(mouseX, mouseY);
         if (target != null) {
            GuiHelper.playClickSound();
            this.onNavigate.accept(target);
            return true;
         }
      }

      return false;
   }

   private boolean inside(double mouseX, double mouseY, int x, int w) {
      return mouseX >= x && mouseX <= x + w && mouseY >= this.getY() - 2 && mouseY <= this.getY() + 9;
   }

   @Nullable
   private Screen crumbAt(double mouseX, double mouseY) {
      for (int i = 0; i < this.crumbs.size(); i++) {
         BreadcrumbWidget.Crumb c = this.crumbs.get(i);
         if (!c.current() && this.crumbX0[i] >= 0 && this.inside(mouseX, mouseY, this.crumbX0[i], this.crumbX1[i] - this.crumbX0[i])) {
            return c.target();
         }
      }

      return null;
   }

   private List<Integer> computeVisibleCrumbs(int maxWidth) {
      int n = this.crumbs.size();
      List<Integer> full = new ArrayList<>(n);

      for (int i = 0; i < n; i++) {
         full.add(i);
      }

      if (n > 2 && this.trailWidth(full) > maxWidth) {
         for (int tailCount = n - 2; tailCount >= 1; tailCount--) {
            List<Integer> display = new ArrayList<>();
            display.add(0);
            display.add(-1);

            for (int i = n - tailCount; i < n; i++) {
               display.add(i);
            }

            if (this.trailWidth(display) <= maxWidth) {
               return display;
            }
         }

         return List.of(0, -1, n - 1);
      } else {
         return full;
      }
   }

   private int trailWidth(List<Integer> display) {
      int sep = this.font.width(" › ");
      int total = 0;

      for (int k = 0; k < display.size(); k++) {
         if (k > 0) {
            total += sep;
         }

         int i = display.get(k);
         total += i < 0 ? this.font.width("…") : this.font.width(this.crumbs.get(i).label());
      }

      return total;
   }

   protected void updateWidgetNarration(NarrationElementOutput narration) {
   }

   public record Crumb(Component label, Screen target, boolean current) {
   }
}

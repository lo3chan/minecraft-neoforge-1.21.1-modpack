package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.List;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.BooleanToggleWidget;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

class CategoryRow extends ConfigListRow {
   private final ConfigScreenAccess view;
   private final ConfigCategory category;
   private final Button button;
   @Nullable
   private final ConfigOption.BooleanValue gate;
   @Nullable
   private final BooleanToggleWidget toggle;
   private final List<AbstractWidget> children;
   @Nullable
   private final Component tooltip;
   private final ConfigScreenIcons.Anim iconAnim = new ConfigScreenIcons.Anim();

   CategoryRow(ConfigScreenAccess view, ConfigCategory category) {
      this.view = view;
      this.category = category;
      this.tooltip = category.description();
      this.gate = category.gate();
      this.button = Button.builder(Component.empty(), b -> view.openCategory(category)).bounds(0, 0, 280, 24).build();
      if (this.gate != null) {
         this.toggle = new BooleanToggleWidget(20, 20, MoonlightIcons.YES, MoonlightIcons.NO, Boolean.TRUE.equals(view.session().current(this.gate)), val -> {
            view.session().put(this.gate, val);
            view.onValueEdited();
         });
         this.children = List.of(this.button, this.toggle);
      } else {
         this.toggle = null;
         this.children = List.of(this.button);
      }
   }

   public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
      Font font = this.view.font();
      int cy = top + (height - 20) / 2;
      boolean enabled = this.view.isCategoryEnabled(this.category);
      int buttonWidth = this.toggle != null ? width - 20 - 4 : width;
      this.button.setMessage(Component.empty());
      this.button.setX(left);
      this.button.setWidth(buttonWidth);
      this.button.setY(top);
      this.button.setHeight(height);
      this.button.render(graphics, mouseX, mouseY, partialTick);
      int iconX = left + 6;
      int textLeft = iconX + 16 + 6;
      int textRight = left + buttonWidth - 4;
      int titleColor = enabled ? ConfigGuiColors.LABEL : ConfigGuiColors.DESCRIPTION;
      int iconY = top + (height - 16) / 2;
      if (ConfigScreenIcons.has(this.category.icon())) {
         this.iconAnim.update(hovering);
         ConfigScreenIcons.renderAnimated(graphics, this.category.icon(), iconX, iconY, this.iconAnim.phase(), enabled);
      } else {
         graphics.blitSprite(MoonlightIcons.FOLDER, iconX, iconY, 16, 16);
      }

      Component title = this.category.title().copy().withStyle(ChatFormatting.BOLD);
      GuiHelper.renderScrollingText(graphics, font, title, textLeft, textRight, top, height, titleColor);
      if (this.toggle != null && this.gate != null) {
         this.toggle.set(Boolean.TRUE.equals(this.view.session().current(this.gate)));
         this.toggle.active = this.view.areAncestorsEnabled(this.category);
         this.toggle.setX(left + width - 20);
         this.toggle.setY(cy);
         this.toggle.render(graphics, mouseX, mouseY, partialTick);
      }
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   public List<? extends NarratableEntry> narratables() {
      return this.children;
   }

   @Nullable
   @Override
   Component getTooltip(int mouseX, int mouseY) {
      return this.tooltip;
   }
}

package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.List;
import java.util.Objects;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigControl;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigEditSession;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.IconButton;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

class OptionRow extends ConfigListRow {
   private final ConfigScreenAccess view;
   private final ConfigEditSession session;
   private final ConfigOption<?> value;
   @Nullable
   private final ConfigCategory owner;
   private final boolean isGate;
   private final boolean asToggle;
   private final Component title;
   @Nullable
   private final Component description;
   private final ConfigControl<?> control;
   private final IconButton resetButton;
   private final boolean editable;
   private final List<AbstractWidget> children;
   private final ConfigScreenIcons.Anim iconAnim = new ConfigScreenIcons.Anim();
   private int toggleX0;
   private int toggleX1;
   private int rowY0;
   private int rowY1;
   private int reloadIconX0 = -1;
   private int reloadIconX1 = -1;

   OptionRow(ConfigScreenAccess view, ConfigOption<?> value) {
      this(view, value, null);
   }

   OptionRow(ConfigScreenAccess view, ConfigOption<?> value, @Nullable Component categoryPath) {
      this.view = view;
      this.session = view.session();
      this.value = value;
      this.owner = value.parent();
      this.isGate = this.owner != null && this.owner.gate() == value;
      this.title = (Component)(categoryPath == null ? value.title() : Component.empty().append(categoryPath).append(value.title()));
      this.description = value.description();
      this.editable = !(value instanceof ConfigOption.UnsupportedValue);
      this.asToggle = this.isGate || value instanceof ConfigOption.BooleanValue bv && bv.isFeature();
      this.control = this.asToggle
         ? ConfigControllers.featureToggle((ConfigOption.BooleanValue)value, this.session, this::onEdited)
         : ConfigControllers.create(value, this.session, this::onEdited);
      this.resetButton = new IconButton(0, 0, 20, 20, Component.empty(), MoonlightIcons.RESET, 12, 12, b -> this.rollback());
      this.resetButton.setTooltip(Tooltip.create(Component.translatable("gui.moonlight.config.reset")));
      this.children = List.of(this.control.widget(), this.resetButton);
      this.refreshReset();
   }

   private void onEdited() {
      this.refreshReset();
      this.view.onValueEdited();
   }

   private void refreshReset() {
      this.resetButton.active = this.editable && !Objects.equals(this.session.currentRaw(this.value), this.value.defaultValue());
   }

   private void rollback() {
      if (this.editable) {
         Object def = this.value.defaultValue();
         this.session.put(this.value, def);
         this.control.set(def);
         this.onEdited();
      }
   }

   private boolean hasDescription() {
      return this.description != null;
   }

   public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
      Font font = this.view.font();
      int cy = top + (height - 20) / 2;
      boolean contextEnabled = this.owner == null || (this.isGate ? this.view.areAncestorsEnabled(this.owner) : this.view.isCategoryEnabled(this.owner));
      int resetX = left + width - this.resetButton.getWidth();
      this.resetButton.setX(resetX);
      this.resetButton.setY(cy);
      this.resetButton.active = this.editable && contextEnabled && !Objects.equals(this.session.currentRaw(this.value), this.value.defaultValue());
      this.resetButton.render(graphics, mouseX, mouseY, partialTick);
      AbstractWidget w = this.control.widget();
      w.active = this.editable && contextEnabled;
      int controlX = resetX - 4 - w.getWidth();
      w.setX(controlX);
      w.setY(top + (height - w.getHeight()) / 2);
      w.render(graphics, mouseX, mouseY, partialTick);
      int textLeft = left;
      if (this.hasDescription()) {
         boolean expanded = this.session.isExpanded(this.value);
         ResourceLocation arrow = expanded ? MoonlightIcons.SECTION_EXPANDED : MoonlightIcons.SECTION_COLLAPSED;
         int arrowSize = 7;
         if (!contextEnabled) {
            graphics.setColor(0.5F, 0.5F, 0.5F, 1.0F);
         }

         graphics.blitSprite(arrow, left + 2, top + (height - arrowSize) / 2, arrowSize, arrowSize);
         if (!contextEnabled) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         }

         textLeft = left + 12;
      }

      if (!this.asToggle && ConfigScreenIcons.has(this.value.icon())) {
         this.iconAnim.update(hovering);
         ConfigScreenIcons.renderAnimated(graphics, this.value.icon(), textLeft, top + (height - 16) / 2, this.iconAnim.phase(), contextEnabled);
         textLeft += 20;
      }

      int textRight = controlX - 4;
      this.reloadIconX0 = this.reloadIconX1 = -1;
      ResourceLocation reloadIcon = ConfigScreenLayout.reloadIcon(this.value.reloadType());
      if (reloadIcon != null) {
         int iconSize = 8;
         int iconX = left - iconSize - 3;
         graphics.blitSprite(reloadIcon, iconX, top + (height - iconSize) / 2, iconSize, iconSize);
         this.reloadIconX0 = iconX;
         this.reloadIconX1 = iconX + iconSize;
      }

      boolean modified = !Objects.equals(this.session.currentRaw(this.value), this.value.get());
      int titleColor = !contextEnabled ? ConfigGuiColors.DESCRIPTION : (modified ? ConfigGuiColors.MODIFIED : ConfigGuiColors.LABEL);
      GuiHelper.renderScrollingText(graphics, font, this.title, textLeft, textRight, top, height, titleColor);
      this.toggleX0 = left;
      this.toggleX1 = textRight;
      this.rowY0 = top;
      this.rowY1 = top + height;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.hasDescription() && button == 0 && mouseX >= this.toggleX0 && mouseX < this.toggleX1 && mouseY >= this.rowY0 && mouseY < this.rowY1) {
         GuiHelper.playClickSound();
         this.view.toggleExpanded(this.value);
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
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
      return null;
   }

   @Nullable
   @Override
   Component getGutterTooltip(int mouseX, int mouseY) {
      return this.reloadIconX0 >= 0 && mouseX >= this.reloadIconX0 && mouseX <= this.reloadIconX1 && mouseY >= this.rowY0 && mouseY <= this.rowY1
         ? reloadTooltip(this.value.reloadType())
         : null;
   }

   private static Component reloadTooltip(ConfigReloadType type) {
      return Component.translatable(type == ConfigReloadType.GAME_RESTART ? "gui.moonlight.config.reload.game" : "gui.moonlight.config.reload.world");
   }
}

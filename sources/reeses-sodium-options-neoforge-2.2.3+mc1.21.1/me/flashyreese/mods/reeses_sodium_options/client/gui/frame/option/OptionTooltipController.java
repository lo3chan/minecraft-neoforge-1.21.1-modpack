package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.ArrayList;
import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiThemes;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

final class OptionTooltipController {
   private static final int DEFAULT_TOOLTIP_BORDER_COLOR = -7019309;
   private static final int TEXT_PADDING = 3;
   private static final int BOX_PADDING = 3;
   private static final int LINE_HEIGHT = 12;
   private static final float TOOLTIP_Z_OFFSET = 400.0F;
   private final LayoutBounds viewportBounds;
   private final ModOptions modOptions;
   private final OptionStateStore optionStateStore;
   private final OptionTooltipController.BoxRenderer boxRenderer;
   private long targetStartTime;
   @Nullable
   private OptionRow targetElement;

   OptionTooltipController(
      LayoutBounds viewportBounds, ModOptions modOptions, OptionStateStore optionStateStore, OptionTooltipController.BoxRenderer boxRenderer
   ) {
      this.viewportBounds = viewportBounds;
      this.modOptions = modOptions;
      this.optionStateStore = optionStateStore;
      this.boxRenderer = boxRenderer;
   }

   void render(GuiGraphics guiGraphics, List<OptionRow> optionRows, int mouseX, int mouseY) {
      OptionRow targetElement = this.findTargetOptionRow(optionRows, mouseX, mouseY);
      if (targetElement != null && this.targetElement == targetElement) {
         if (this.targetStartTime == 0L) {
            this.targetStartTime = System.currentTimeMillis();
         }

         this.renderTooltip(guiGraphics, targetElement);
      } else {
         this.targetStartTime = 0L;
         this.targetElement = targetElement;
      }
   }

   @Nullable
   private OptionRow findTargetOptionRow(List<OptionRow> optionRows, int mouseX, int mouseY) {
      OptionRow hoveredElement = this.findHoveredOptionRow(optionRows, mouseX, mouseY);
      if (hoveredElement != null) {
         return hoveredElement;
      } else {
         OptionRow focusedElement = this.findFocusedOptionRow(optionRows);
         return focusedElement != null ? focusedElement : this.findSelectedSearchResultRow(optionRows);
      }
   }

   @Nullable
   private OptionRow findHoveredOptionRow(List<OptionRow> optionRows, int mouseX, int mouseY) {
      return !this.viewportBounds.contains(mouseX, mouseY)
         ? null
         : optionRows.stream().filter(this::isVisibleOptionRow).filter(optionRow -> optionRow.isMouseOver(mouseX, mouseY)).findFirst().orElse(null);
   }

   @Nullable
   private OptionRow findFocusedOptionRow(List<OptionRow> optionRows) {
      return !BaseWidget.isKeyboardFocusVisible()
         ? null
         : optionRows.stream().filter(this::isVisibleOptionRow).filter(GuiEventListener::isFocused).findFirst().orElse(null);
   }

   @Nullable
   private OptionRow findSelectedSearchResultRow(List<OptionRow> optionRows) {
      return !this.optionStateStore.searchActive()
         ? null
         : optionRows.stream().filter(this::isVisibleOptionRow).filter(this::isSelectedSearchResult).findFirst().orElse(null);
   }

   private boolean isSelectedSearchResult(OptionRow optionRow) {
      return optionRow.getOption() instanceof OptionExtended optionExtended
         ? this.optionStateStore.optionUiState(optionExtended.rso$getId()).isSelected()
         : false;
   }

   private boolean isVisibleOptionRow(OptionRow optionRow) {
      return this.viewportBounds.overlaps(optionRow.getDimensions());
   }

   private void renderTooltip(GuiGraphics guiGraphics, OptionRow element) {
      if (this.targetStartTime + ReeseSodiumOptionsConfig.config().getTooltipDelayMs() <= System.currentTimeMillis()) {
         LayoutBounds dim = element.getDimensions();
         int boxWidth = dim.width();
         int boxY = dim.getLimitY();
         int boxX = dim.x();
         List<FormattedCharSequence> tooltip = this.buildTooltip(element.getOption(), boxWidth);
         if (!tooltip.isEmpty()) {
            int boxHeight = tooltip.size() * 12 + 3;
            int boxYLimit = boxY + boxHeight;
            int boxYCutoff = this.viewportBounds.getLimitY();
            if (boxYLimit > boxYCutoff) {
               boxY -= boxHeight + dim.height();
            }

            if (boxY < 0) {
               boxY = dim.getLimitY();
            }

            guiGraphics.flush();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);

            try {
               this.boxRenderer.drawRect(guiGraphics, boxX, boxY, boxX + boxWidth, boxY + boxHeight, -536870912);
               int borderColor = ReeseSodiumOptionsConfig.config().isColorThemes() && ReeseSodiumOptionsConfig.config().isThemedTooltipBorders()
                  ? GuiThemes.fromSodium(this.modOptions.theme()).theme
                  : -7019309;
               this.boxRenderer.drawBorder(guiGraphics, boxX, boxY, boxX + boxWidth, boxY + boxHeight, borderColor);

               for (int i = 0; i < tooltip.size(); i++) {
                  guiGraphics.drawString(Minecraft.getInstance().font, tooltip.get(i), boxX + 3, boxY + 3 + i * 12, -1, true);
               }
            } finally {
               guiGraphics.flush();
               guiGraphics.pose().popPose();
            }
         }
      }
   }

   private List<FormattedCharSequence> buildTooltip(Option option, int boxWidth) {
      List<FormattedCharSequence> tooltip = new ArrayList<>();
      if (ReeseSodiumOptionsConfig.config().isTooltipOptionIds() && option instanceof OptionExtended optionExtended) {
         tooltip.add(Language.getInstance().getVisualOrder(Component.literal(optionExtended.rso$getId().toString()).withStyle(ChatFormatting.GRAY)));
         tooltip.add(Language.getInstance().getVisualOrder(Component.literal("")));
      }

      tooltip.addAll(Minecraft.getInstance().font.split(option.getTooltip(), boxWidth - 6));
      OptionImpact impact = option.getImpact();
      if (impact != null) {
         tooltip.add(
            Language.getInstance()
               .getVisualOrder(
                  Component.translatable("sodium.options.performance_impact_string", new Object[]{impact.getName()}).withStyle(ChatFormatting.GRAY)
               )
         );
      }

      return tooltip;
   }

   interface BoxRenderer {
      void drawRect(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6);

      void drawBorder(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6);
   }
}

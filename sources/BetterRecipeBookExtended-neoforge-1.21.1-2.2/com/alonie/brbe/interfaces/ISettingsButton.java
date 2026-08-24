package com.alonie.brbe.interfaces;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.config.Config;
import com.alonie.brbe.util.BRBTextures;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public interface ISettingsButton {
   MutableComponent OPEN_SETTINGS_TOOLTIP = Component.translatable("brb.gui.settings.open");

   default ImageButton createSettingsButton(int i, int j) {
      return BetterRecipeBook.ctx().config().settingsButton
         ? new ImageButton(
            i + 11,
            j + 137,
            18,
            18,
            BRBTextures.SETTINGS_BUTTON_SPRITES,
            button -> Minecraft.getInstance().setScreen((Screen)AutoConfig.getConfigScreen(Config.class, Minecraft.getInstance().screen).get())
         )
         : null;
   }

   default void renderSettingsButton(@Nullable ImageButton settingsButton, GuiGraphics gui, int mouseX, int mouseY, float delta) {
      if (settingsButton != null && BetterRecipeBook.ctx().config().settingsButton) {
         settingsButton.render(gui, mouseX, mouseY, delta);
      }
   }

   default boolean settingsButtonMouseClicked(@Nullable ImageButton settingsButton, double mouseX, double mouseY, int button) {
      return settingsButton != null && BetterRecipeBook.ctx().config().settingsButton ? settingsButton.mouseClicked(mouseX, mouseY, button) : false;
   }

   default void renderSettingsButtonTooltip(@Nullable ImageButton settingsButton, GuiGraphics gui, int mouseX, int mouseY) {
      if (settingsButton != null
         && settingsButton.isHoveredOrFocused()
         && BetterRecipeBook.ctx().config().settingsButton
         && Minecraft.getInstance().screen != null) {
         gui.renderTooltip(Minecraft.getInstance().font, OPEN_SETTINGS_TOOLTIP, mouseX, mouseY);
      }
   }
}

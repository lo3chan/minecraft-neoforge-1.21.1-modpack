package com.aetherteam.aether.client.gui.component.customization;

import com.aetherteam.aether.client.gui.screen.perks.AetherCustomizationsScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class CustomizationButton extends ImageButton {
   protected final AetherCustomizationsScreen screen;
   protected final ColorBox colorBox;
   protected final CustomizationButton.ButtonType buttonType;

   public CustomizationButton(
      AetherCustomizationsScreen screen,
      CustomizationButton.ButtonType buttonType,
      ColorBox colorBox,
      int x,
      int y,
      int width,
      int height,
      WidgetSprites sprites,
      OnPress onPress,
      Component message
   ) {
      super(x, y, width, height, sprites, onPress, message);
      this.screen = screen;
      this.colorBox = colorBox;
      this.buttonType = buttonType;
   }

   public static enum ButtonType {
      SAVE,
      UNDO;
   }
}

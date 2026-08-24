package com.aetherteam.aether.client.gui.component.customization;

import com.aetherteam.aether.client.gui.screen.perks.AetherCustomizationsScreen;
import com.aetherteam.aether.perk.CustomizationsOptions;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class HaloCustomizationButton extends CustomizationButton {
   public HaloCustomizationButton(
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
      super(screen, buttonType, colorBox, x, y, width, height, sprites, onPress, message);
   }

   public boolean isActive() {
      return this.buttonType == CustomizationButton.ButtonType.SAVE
         ? super.isActive()
            && (this.colorBox.hasValidColor() && this.colorBox.hasTextChanged() || this.screen.haloEnabled != CustomizationsOptions.INSTANCE.isHaloEnabled())
         : super.isActive() && (this.colorBox.hasTextChanged() || this.screen.haloEnabled != CustomizationsOptions.INSTANCE.isHaloEnabled());
   }
}

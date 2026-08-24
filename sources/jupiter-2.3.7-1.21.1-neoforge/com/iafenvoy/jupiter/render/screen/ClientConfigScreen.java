package com.iafenvoy.jupiter.render.screen;

import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import net.minecraft.client.gui.screens.Screen;

@Deprecated(
   forRemoval = true
)
public class ClientConfigScreen extends ConfigContainerScreen {
   public ClientConfigScreen(Screen parent, AbstractConfigContainer configContainer) {
      super(parent, configContainer, true);
   }
}

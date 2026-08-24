package com.iafenvoy.jupiter.render.screen;

import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.config.container.wrapper.RemoteConfigWrapper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;

@Deprecated(
   forRemoval = true
)
public class ServerConfigScreen extends ConfigContainerScreen {
   public ServerConfigScreen(Screen parent, AbstractConfigContainer configContainer) {
      super(parent, configContainer, false);
   }

   @Override
   protected String getCurrentEditText() {
      return this.container instanceof RemoteConfigWrapper
         ? I18n.get("jupiter.screen.current_modifying_dedicate_server", new Object[0])
         : I18n.get("jupiter.screen.current_modifying_local_server", new Object[0]);
   }
}

package com.iafenvoy.jupiter.render.widget.builder;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class ButtonWidgetBuilder<T> extends AbstractButtonWidgetBuilder<T> {
   private final OnPress action;

   public ButtonWidgetBuilder(ConfigMetaProvider provider, ConfigEntry<T> config, OnPress action, Supplier<Component> nameSupplier) {
      super(provider, config, nameSupplier);
      this.action = button -> {
         action.onPress(button);
         this.refresh();
      };
   }

   @Override
   protected Button createButton(WidgetBuilder.Context context, int x, int y, int width, int height) {
      return JupiterScreen.createButton(x, y, width, height, this.nameSupplier.get(), this.action);
   }
}

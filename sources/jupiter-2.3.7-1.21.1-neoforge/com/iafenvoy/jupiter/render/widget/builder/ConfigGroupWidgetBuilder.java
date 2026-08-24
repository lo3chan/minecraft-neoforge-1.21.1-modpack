package com.iafenvoy.jupiter.render.widget.builder;

import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.ConfigSide;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.screen.ConfigListScreen;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.jupiter.util.TextUtil;
import net.minecraft.client.gui.components.Button;

public class ConfigGroupWidgetBuilder extends AbstractButtonWidgetBuilder<ConfigGroup> {
   public ConfigGroupWidgetBuilder(ConfigMetaProvider provider, ConfigEntry<ConfigGroup> config) {
      super(provider, config, () -> TextUtil.translatable("jupiter.screen.edit"));
   }

   @Override
   protected Button createButton(WidgetBuilder.Context context, int x, int y, int width, int height) {
      ConfigGroup group = this.config.getValue();
      return JupiterScreen.createButton(
         x,
         y,
         width,
         height,
         this.nameSupplier.get(),
         button -> this.minecraft
            .setScreen(
               new ConfigListScreen(
                  context.parent(),
                  context.push(group.getName()),
                  this.provider.getConfigId(),
                  group.getConfigs(),
                  this.provider.getSide() == ConfigSide.CLIENT
               )
            )
      );
   }
}

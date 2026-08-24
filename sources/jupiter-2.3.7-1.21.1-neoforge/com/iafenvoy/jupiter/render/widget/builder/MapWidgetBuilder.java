package com.iafenvoy.jupiter.render.widget.builder;

import com.iafenvoy.jupiter.config.entry.MapBaseEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.iafenvoy.jupiter.render.screen.dialog.MapDialog;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.Map;
import net.minecraft.client.gui.components.Button;

public class MapWidgetBuilder<T> extends AbstractButtonWidgetBuilder<Map<String, T>> {
   protected final MapBaseEntry<T> config;

   public MapWidgetBuilder(ConfigMetaProvider provider, MapBaseEntry<T> config) {
      super(provider, config, () -> TextUtil.literal(String.valueOf(config.getValue())));
      this.config = config;
   }

   @Override
   protected Button createButton(WidgetBuilder.Context context, int x, int y, int width, int height) {
      return JupiterScreen.createButton(
         x,
         y,
         width,
         height,
         this.nameSupplier.get(),
         button -> this.minecraft.setScreen(new MapDialog<>(context.parent(), context.push(this.config.getName()), this.provider, this.config))
      );
   }
}

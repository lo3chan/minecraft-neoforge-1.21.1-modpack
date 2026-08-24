package com.iafenvoy.jupiter.render.widget.builder;

import com.iafenvoy.jupiter.config.entry.ListBaseEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.iafenvoy.jupiter.render.screen.dialog.ListDialog;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.List;
import net.minecraft.client.gui.components.Button;

public class ListWidgetBuilder<T> extends AbstractButtonWidgetBuilder<List<T>> {
   protected final ListBaseEntry<T> config;

   public ListWidgetBuilder(ConfigMetaProvider provider, ListBaseEntry<T> config) {
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
         button -> this.minecraft.setScreen(new ListDialog<>(context.parent(), context.push(this.config.getName()), this.provider, this.config))
      );
   }
}

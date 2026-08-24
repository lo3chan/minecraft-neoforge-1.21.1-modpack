package com.iafenvoy.jupiter.render.widget.builder;

import com.iafenvoy.jupiter.config.entry.EntryBaseEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.screen.WidgetBuilderManager;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import net.minecraft.client.gui.components.EditBox;
import org.jetbrains.annotations.Nullable;

public class EntryWidgetBuilder<T> extends WidgetBuilder<Entry<String, T>> {
   private final EntryBaseEntry<T> config;
   @Nullable
   private EditBox keyWidget;
   @Nullable
   private WidgetBuilder<T> valueBuilder;

   public EntryWidgetBuilder(ConfigMetaProvider provider, EntryBaseEntry<T> config) {
      super(provider, config);
      this.config = config;
   }

   @Override
   public void addCustomElements(WidgetBuilder.Context context, int x, int y, int width, int height) {
      this.keyWidget = new EditBox(this.minecraft.font, x, y, width / 2 - 5, height, TextUtil.empty());
      this.keyWidget.setValue(this.config.getValue().getKey());
      this.keyWidget.setResponder(s -> this.config.setValue(new SimpleEntry<>(s, this.config.getValue().getValue())));
      context.addWidget(this.keyWidget);
      this.valueBuilder = WidgetBuilderManager.get(this.provider, this.config.newValueInstance());
      this.valueBuilder.addCustomElements(context, x + width / 2, y, width / 2, height);
   }

   @Override
   public void updateCustom(boolean visible, int y) {
      if (this.keyWidget != null) {
         this.keyWidget.visible = visible;
         this.keyWidget.setY(y);
      }

      if (this.valueBuilder != null) {
         this.valueBuilder.update(visible, y);
      }
   }

   @Override
   public void refresh() {
      if (this.keyWidget != null) {
         this.keyWidget.setValue(this.config.getValue().getKey());
      }

      if (this.valueBuilder != null) {
         this.valueBuilder.refresh();
      }
   }
}

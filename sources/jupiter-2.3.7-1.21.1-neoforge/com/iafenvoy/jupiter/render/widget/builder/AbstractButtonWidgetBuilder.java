package com.iafenvoy.jupiter.render.widget.builder;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractButtonWidgetBuilder<T> extends WidgetBuilder<T> {
   protected final Supplier<Component> nameSupplier;
   @Nullable
   private Button button;

   protected AbstractButtonWidgetBuilder(ConfigMetaProvider provider, ConfigEntry<T> config, Supplier<Component> nameSupplier) {
      super(provider, config);
      this.nameSupplier = nameSupplier;
   }

   @Override
   public void addCustomElements(WidgetBuilder.Context context, int x, int y, int width, int height) {
      this.button = this.createButton(context, x, y, width, height);
      context.addWidget(this.button);
   }

   protected abstract Button createButton(WidgetBuilder.Context var1, int var2, int var3, int var4, int var5);

   @Override
   public void updateCustom(boolean visible, int y) {
      if (this.button != null) {
         this.button.visible = visible;
         this.button.setY(y);
      }
   }

   @Override
   public void refresh() {
      if (this.button != null) {
         this.button.setMessage(this.nameSupplier.get());
      }
   }
}

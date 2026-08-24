package com.iafenvoy.jupiter.render.screen;

import com.iafenvoy.jupiter.Platform;
import com.iafenvoy.jupiter.config.entry.EntryBaseEntry;
import com.iafenvoy.jupiter.config.entry.ListBaseEntry;
import com.iafenvoy.jupiter.config.entry.MapBaseEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.jupiter.render.widget.builder.ButtonWidgetBuilder;
import com.iafenvoy.jupiter.render.widget.builder.ConfigGroupWidgetBuilder;
import com.iafenvoy.jupiter.render.widget.builder.EntryWidgetBuilder;
import com.iafenvoy.jupiter.render.widget.builder.EnumWidgetBuilder;
import com.iafenvoy.jupiter.render.widget.builder.ListWidgetBuilder;
import com.iafenvoy.jupiter.render.widget.builder.MapWidgetBuilder;
import com.iafenvoy.jupiter.render.widget.builder.SeparatorWidgetBuilder;
import com.iafenvoy.jupiter.render.widget.builder.TextFieldWidgetBuilder;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

public class WidgetBuilderManager {
   private static final Map<ConfigType<?>, BiFunction<ConfigMetaProvider, ConfigEntry<?>, WidgetBuilder<?>>> BUILDERS = new HashMap<>();

   public static <T> void register(ConfigType<T> type, BiFunction<ConfigMetaProvider, ConfigEntry<T>, WidgetBuilder<T>> builder) {
      BUILDERS.put(type, builder);
   }

   public static <T> WidgetBuilder<T> get(ConfigMetaProvider provider, ConfigEntry<T> entry) {
      return (WidgetBuilder<T>)BUILDERS.getOrDefault(entry.getType(), WidgetBuilderManager.Fallback::new).apply(provider, entry);
   }

   static {
      register(ConfigTypes.SEPARATOR, SeparatorWidgetBuilder::new);
      register(ConfigTypes.CONFIG_GROUP, ConfigGroupWidgetBuilder::new);
      register(
         ConfigTypes.BOOLEAN,
         (provider, config) -> new ButtonWidgetBuilder<>(
            provider, config, button -> config.setValue(!(Boolean)config.getValue()), () -> TextUtil.literal(config.getValue() ? "§atrue" : "§cfalse")
         )
      );
      register(ConfigTypes.INTEGER, TextFieldWidgetBuilder::new);
      register(ConfigTypes.LONG, TextFieldWidgetBuilder::new);
      register(ConfigTypes.DOUBLE, TextFieldWidgetBuilder::new);
      register(ConfigTypes.FLOAT, TextFieldWidgetBuilder::new);
      register(ConfigTypes.STRING, TextFieldWidgetBuilder::new);
      register(ConfigTypes.ENUM, (provider, config) -> (WidgetBuilder<Enum<?>>)(new EnumWidgetBuilder<>(provider, config)));
      register(ConfigTypes.LIST_BOOLEAN, (provider, config) -> new ListWidgetBuilder<>(provider, (ListBaseEntry<Boolean>)config));
      register(ConfigTypes.LIST_INTEGER, (provider, config) -> new ListWidgetBuilder<>(provider, (ListBaseEntry<Integer>)config));
      register(ConfigTypes.LIST_LONG, (provider, config) -> new ListWidgetBuilder<>(provider, (ListBaseEntry<Long>)config));
      register(ConfigTypes.LIST_DOUBLE, (provider, config) -> new ListWidgetBuilder<>(provider, (ListBaseEntry<Double>)config));
      register(ConfigTypes.LIST_STRING, (provider, config) -> new ListWidgetBuilder<>(provider, (ListBaseEntry<String>)config));
      register(ConfigTypes.LIST_ENUM, (provider, config) -> new ListWidgetBuilder<>(provider, (ListBaseEntry<Enum<?>>)config));
      register(ConfigTypes.MAP_STRING, (provider, config) -> new MapWidgetBuilder<>(provider, (MapBaseEntry<String>)config));
      register(ConfigTypes.MAP_INTEGER, (provider, config) -> new MapWidgetBuilder<>(provider, (MapBaseEntry<Integer>)config));
      register(ConfigTypes.MAP_DOUBLE, (provider, config) -> new MapWidgetBuilder<>(provider, (MapBaseEntry<Double>)config));
      register(ConfigTypes.ENTRY_STRING, (provider, config) -> new EntryWidgetBuilder<>(provider, (EntryBaseEntry<String>)config));
      register(ConfigTypes.ENTRY_INTEGER, (provider, config) -> new EntryWidgetBuilder<>(provider, (EntryBaseEntry<Integer>)config));
      register(ConfigTypes.ENTRY_DOUBLE, (provider, config) -> new EntryWidgetBuilder<>(provider, (EntryBaseEntry<Double>)config));
      register(ConfigTypes.RESOURCE_LOCATION, TextFieldWidgetBuilder::new);
   }

   private static class Fallback<T> extends WidgetBuilder<T> {
      public Fallback(ConfigMetaProvider configMetaProvider, ConfigEntry<T> config) {
         super(configMetaProvider, config);
      }

      @Override
      public void addElements(WidgetBuilder.Context context, int x, int y, int width, int height) {
         Font textRenderer = this.minecraft.font;
         Component text = TextUtil.translatable(
            "jupiter.screen.unregistered_widget",
            this.config.getClass().getSimpleName(),
            this.provider.getSource().jupiterCapability() ? "Jupiter" : Platform.resolveModName(this.provider.getConfigId().getNamespace())
         );
         this.textWidget = new StringWidget(20, y, textRenderer.width(text), height, text, textRenderer);
         context.addWidget(this.textWidget);
      }

      @Override
      public void addCustomElements(WidgetBuilder.Context context, int x, int y, int width, int height) {
      }

      @Override
      public void updateCustom(boolean visible, int y) {
      }

      @Override
      public void refresh() {
      }
   }
}

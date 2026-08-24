package snownee.jade.gui;

import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.gui.config.OptionsList;
import snownee.jade.gui.config.value.OptionValue;
import snownee.jade.impl.ObjectDataCenter;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.config.PluginConfig;

public class PluginsConfigScreen extends PreviewOptionsScreen {
   @Nullable
   private Function<OptionsList, OptionsList.Entry> jumpTo;

   public PluginsConfigScreen(Screen parent) {
      super(parent, Component.translatable("gui.jade.plugin_settings"));
      this.saver = PluginConfig.INSTANCE::save;
      this.canceller = PluginConfig.INSTANCE::reload;
   }

   public static Screen createPluginConfigScreen(@Nullable Screen parent, @Nullable Function<OptionsList, OptionsList.Entry> jumpTo, boolean dontSave) {
      PluginsConfigScreen screen = new PluginsConfigScreen(parent);
      screen.jumpTo = jumpTo;
      return screen;
   }

   @Override
   public OptionsList createOptions() {
      OptionsList options = new OptionsList(this, this.minecraft, this.width - 120, this.height - 32, 0, 26, PluginConfig.INSTANCE::save);
      boolean noteServerFeature = Minecraft.getInstance().level == null || IWailaConfig.get().getGeneral().isDebug() || !ObjectDataCenter.serverConnected;
      BiConsumer<ResourceLocation, Object> setter = (key, value) -> {
         PluginConfig.INSTANCE.set(key, value);
         options.updateOptionValue(key);
      };
      PluginConfig.INSTANCE.getListView(IWailaConfig.get().getGeneral().getEnableAccessibilityPlugin()).forEach(category -> {
         options.add(new OptionsList.Title(category.title()));
         MutableObject<OptionValue<?>> lastPrimary = new MutableObject();
         category.entries().forEach(entry -> {
            OptionValue<?> option = entry.createUI(options, "plugin_" + entry.getId().toLanguageKey(), setter);
            option.setId(entry.getId());
            if (entry.isSynced()) {
               option.setDisabled(true);
               option.appendDescription(Component.translatable("gui.jade.forced_plugin_config").withStyle(ChatFormatting.DARK_RED));
            } else if (noteServerFeature && !WailaClientRegistration.instance().isClientFeature(entry.getId())) {
               option.serverFeature = true;
            }

            if (!PluginConfig.isPrimaryKey(entry.getId())) {
               if (lastPrimary.getValue() != null) {
                  option.parent((OptionsList.Entry)lastPrimary.getValue());
               }
            } else {
               lastPrimary.setValue(option);
            }
         });
      });
      return options;
   }

   @Override
   protected void init() {
      super.init();
      if (this.jumpTo != null) {
         OptionsList.Entry entry = this.jumpTo.apply(this.options);
         if (entry != null) {
            this.options.showOnTop(entry);
         }

         this.jumpTo = null;
      }
   }
}

package fuzs.puzzleslib.neoforge.impl.client.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationListScreen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen.Context;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen.Element;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.ListValueSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Range;
import net.neoforged.neoforge.common.ModConfigSpec.RestartType;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

public class NeoForgeConfigurationScreen extends OptionsSubScreen {
   private static final List<Type> CONFIG_TYPE_DISPLAY_ORDER = List.of(Type.STARTUP, Type.COMMON, Type.CLIENT, Type.SERVER);
   private static final Component EDIT_COMPONENT = Component.literal("Edit").append(CommonComponents.ELLIPSIS);
   private static final String LANG_PREFIX = "neoforge.configuration.uitext.";
   private static final String CRUMB = "neoforge.configuration.uitext.breadcrumb.order";
   protected final Set<String> modIds;

   public NeoForgeConfigurationScreen(String modId, Screen lastScreen) {
      this(Collections.singleton(modId), lastScreen, getConfigTitleComponent(modId));
   }

   public NeoForgeConfigurationScreen(String modId, Screen lastScreen, String... modIds) {
      this(ImmutableSet.builder().add(modId).add(modIds).build(), lastScreen, getConfigTitleComponent(modId));
   }

   public NeoForgeConfigurationScreen(Set<String> modIds, Screen lastScreen, Component title) {
      super(lastScreen, Minecraft.getInstance().options, title);
      this.modIds = modIds;
   }

   protected void addOptions() {
      boolean hasSectionHeader = false;
      List<Optional<ModConfig>> modConfigs = new ArrayList<>();

      for (Type type : CONFIG_TYPE_DISPLAY_ORDER) {
         if (type == Type.SERVER) {
            hasSectionHeader = false;
         }

         for (ModConfig modConfig : ModConfigs.getConfigSet(type)) {
            if (this.modIds.contains(modConfig.getModId())) {
               if (!hasSectionHeader) {
                  this.list.addSmall(new StringWidget(310, 20, getConfigSectionComponent(type), this.font).alignCenter(), null);
                  hasSectionHeader = true;
               }

               Button button = Button.builder(Component.literal(modConfig.getFileName()), buttonX -> this.openModConfigScreen(modConfig, this))
                  .width(310)
                  .build();
               Component tooltip = this.getTooltipComponent(type, modConfig);
               if (tooltip != null) {
                  button.setTooltip(Tooltip.create(tooltip));
                  button.active = false;
                  modConfigs.add(Optional.empty());
               } else {
                  modConfigs.add(Optional.of(modConfig));
               }

               this.list.addSmall(button, null);
            }
         }
      }

      if (modConfigs.size() == 1) {
         ((Optional)modConfigs.getFirst()).ifPresent(modConfigx -> this.openModConfigScreen(modConfigx, this.lastScreen));
      }
   }

   protected void openModConfigScreen(ModConfig modConfig, Screen lastScreen) {
      Component component = getConfigTitleComponent(modConfig.getModId());
      this.minecraft.setScreen(new NeoForgeConfigurationScreen.CustomConfigurationSectionScreen(lastScreen, modConfig.getType(), modConfig, component));
   }

   protected Component getTooltipComponent(Type type, ModConfig modConfig) {
      if (!((ModConfigSpec)modConfig.getSpec()).isLoaded()) {
         return ConfigurationScreen.TOOLTIP_CANNOT_EDIT_NOT_LOADED;
      } else if (type == Type.SERVER && this.minecraft.getCurrentServer() != null && !this.minecraft.isSingleplayer()) {
         return ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_ONLINE;
      } else {
         return type == Type.SERVER && this.minecraft.hasSingleplayerServer() && this.minecraft.getSingleplayerServer().isPublished()
            ? ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_OPEN_TO_LAN
            : null;
      }
   }

   private static Component getConfigTitleComponent(String modId) {
      return Component.literal(ModContainer.getDisplayName(modId) + " Settings");
   }

   private static Component getConfigSectionComponent(Type type) {
      String message;
      if (type != Type.SERVER) {
         message = "Global Configurations";
      } else {
         message = "World Configurations";
      }

      return Component.literal(message).withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.YELLOW});
   }

   private static MutableComponent getConfigValueComponent(String valueKey) {
      String string = ModContainer.getCapitalizedString(valueKey).replace(" And ", " & ").replace(" Or ", " / ");
      return Component.literal(string);
   }

   private static Component getConfigValueTooltipComponent(Component valueKeyComponent, String rawComment) {
      if (!Strings.isBlank(rawComment)) {
         Component component = getStylizedStrings(rawComment.split("\\R"));
         if (component != null) {
            return Component.empty().append(valueKeyComponent).append(CommonComponents.NEW_LINE).append(CommonComponents.NEW_LINE).append(component);
         }
      }

      return CommonComponents.EMPTY;
   }

   @Nullable
   private static Component getStylizedStrings(String... strings) {
      MutableComponent mutableComponent = null;
      int i = 0;

      for (int j = 0; i < strings.length; i++) {
         if (!strings[i].matches("^ Default: .*")) {
            String string = strings[i].replaceAll("^ Range: ", "Value Range: ");
            ChatFormatting chatFormatting = j++ % 2 == 0 ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
            MutableComponent component = Component.literal(string).withStyle(chatFormatting);
            if (mutableComponent != null) {
               mutableComponent.append(CommonComponents.NEW_LINE).append(component);
            } else {
               mutableComponent = component;
            }
         }
      }

      return mutableComponent;
   }

   public static class CustomConfigurationListScreen<T> extends ConfigurationListScreen<T> {
      public CustomConfigurationListScreen(Context context, String key, Component title, ListValueSpec spec, ConfigValue<List<T>> valueList) {
         super(context, key, title, spec, valueList);
         this.needsRestart = RestartType.NONE;
      }

      protected MutableComponent getTranslationComponent(String key) {
         return NeoForgeConfigurationScreen.getConfigValueComponent(key);
      }

      protected Component getTooltipComponent(String key, @Nullable Range<?> range) {
         return NeoForgeConfigurationScreen.getConfigValueTooltipComponent(this.getTranslationComponent(key), this.getComment(key));
      }

      public ConfigurationSectionScreen rebuild() {
         return super.rebuild();
      }

      @Nullable
      protected Element createOtherValue(int idx, T entry) {
         Element element = super.createOtherValue(idx, entry);
         if (element != null && element.widget() != null) {
            element.widget().setTooltip(null);
         }

         return element;
      }

      @Nullable
      protected Element createStringListValue(int idx, String value) {
         Element element = super.createStringListValue(idx, value);
         if (element != null && element.widget() != null) {
            element.widget().setTooltip(null);
         }

         return element;
      }

      @Nullable
      protected Element createDoubleListValue(int idx, Double value) {
         Element element = super.createDoubleListValue(idx, value);
         if (element != null && element.widget() != null) {
            element.widget().setTooltip(null);
         }

         return element;
      }

      @Nullable
      protected Element createLongListValue(int idx, Long value) {
         Element element = super.createLongListValue(idx, value);
         if (element != null && element.widget() != null) {
            element.widget().setTooltip(null);
         }

         return element;
      }

      @Nullable
      protected Element createIntegerListValue(int idx, Integer value) {
         Element element = super.createIntegerListValue(idx, value);
         if (element != null && element.widget() != null) {
            element.widget().setTooltip(null);
         }

         return element;
      }

      @Nullable
      protected Element createBooleanListValue(int idx, Boolean value) {
         Element element = super.createBooleanListValue(idx, value);
         if (element != null && element.widget() != null) {
            element.widget().setTooltip(null);
         }

         return element;
      }
   }

   public static class CustomConfigurationSectionScreen extends ConfigurationSectionScreen {
      public CustomConfigurationSectionScreen(Screen parent, Type type, ModConfig modConfig, Component title) {
         super(parent, type, modConfig, title);
         this.needsRestart = RestartType.NONE;
      }

      public CustomConfigurationSectionScreen(
         Context parentContext, Screen parent, Map<String, Object> valueSpecs, String key, Set<? extends Entry> entrySet, Component title
      ) {
         super(parentContext, parent, valueSpecs, key, entrySet, title);
         this.needsRestart = RestartType.NONE;
      }

      protected MutableComponent getTranslationComponent(String key) {
         return NeoForgeConfigurationScreen.getConfigValueComponent(key);
      }

      protected Component getTooltipComponent(String key, @Nullable Range<?> range) {
         return NeoForgeConfigurationScreen.getConfigValueTooltipComponent(this.getTranslationComponent(key), this.getComment(key));
      }

      public ConfigurationSectionScreen rebuild() {
         return super.rebuild();
      }

      public void onClose() {
         super.onClose();
         if (this.changed && !(this.lastScreen instanceof ConfigurationSectionScreen)) {
            switch (this.needsRestart) {
               case GAME:
                  this.openConfirmScreen(ConfigurationScreen.GAME_RESTART_TITLE, ConfigurationScreen.GAME_RESTART_MESSAGE);
                  break;
               case WORLD:
                  if (this.minecraft.level != null) {
                     this.openConfirmScreen(ConfigurationScreen.SERVER_RESTART_TITLE, ConfigurationScreen.SERVER_RESTART_MESSAGE);
                  }
            }
         }
      }

      private void openConfirmScreen(Component title, Component message) {
         this.minecraft.setScreen(new ConfirmScreen(hasConfirmed -> {
            if (hasConfirmed) {
               super.onClose();
            } else {
               this.minecraft.setScreen(this);
            }
         }, title, message, CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
      }

      @Nullable
      protected Element createSection(String key, UnmodifiableConfig subconfig, UnmodifiableConfig subsection) {
         return subconfig.isEmpty()
            ? null
            : new Element(
               this.getTranslationComponent(key).append(CommonComponents.ELLIPSIS),
               this.getTooltipComponent(key, null),
               Button.builder(
                     NeoForgeConfigurationScreen.EDIT_COMPONENT,
                     button -> this.minecraft
                        .setScreen(
                           new NeoForgeConfigurationScreen.CustomConfigurationSectionScreen(
                                 this.context, this, subconfig.valueMap(), key, subsection.entrySet(), this.getTranslationComponent(key)
                              )
                              .rebuild()
                        )
                  )
                  .tooltip(Tooltip.create(this.getTooltipComponent(key, null)))
                  .build(),
               false
            );
      }

      @Nullable
      protected <T> Element createList(String key, ListValueSpec spec, ConfigValue<List<T>> list) {
         return new Element(
            this.getTranslationComponent(key).append(CommonComponents.ELLIPSIS),
            this.getTooltipComponent(key, null),
            Button.builder(
                  NeoForgeConfigurationScreen.EDIT_COMPONENT,
                  button -> this.minecraft
                     .setScreen(
                        new NeoForgeConfigurationScreen.CustomConfigurationListScreen(
                              Context.list(this.context, this),
                              key,
                              Component.translatable(
                                 "neoforge.configuration.uitext.breadcrumb.order",
                                 new Object[]{this.getTitle(), ConfigurationScreen.CRUMB_SEPARATOR, this.getTranslationComponent(key)}
                              ),
                              spec,
                              list
                           )
                           .rebuild()
                     )
               )
               .tooltip(Tooltip.create(this.getTooltipComponent(key, null)))
               .build(),
            false
         );
      }
   }
}

package com.seibel.distanthorizons.common.wrappers.gui.classicConfig;

import com.mojang.blaze3d.platform.InputConstants;
import com.seibel.distanthorizons.api.enums.config.DisallowSelectingViaConfigGui;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreen_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.GuiHelper_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.TexturedButtonWidget_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.config.ConfigGuiInfo_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.updater.ChangelogScreen_neoforge;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftClientWrapper_neoforge;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.ConfigHandler;
import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import com.seibel.distanthorizons.core.config.types.ConfigCategory;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.config.types.ConfigUIButton;
import com.seibel.distanthorizons.core.config.types.ConfigUIComment;
import com.seibel.distanthorizons.core.config.types.ConfigUISpacer;
import com.seibel.distanthorizons.core.config.types.ConfigUiLinkedEntry;
import com.seibel.distanthorizons.core.config.types.enums.EConfigValidity;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.updater.SelfUpdater;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.AnnotationUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.config.ILangWrapper;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

class DhConfigScreen_neoforge extends DhScreen_neoforge {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final ILangWrapper LANG_WRAPPER = SingletonInjector.INSTANCE.get(ILangWrapper.class);
   private static final String TRANSLATION_PREFIX = "distanthorizons.config.";
   private static final MinecraftClientWrapper_neoforge MC_CLIENT = MinecraftClientWrapper_neoforge.INSTANCE;
   private final Screen parent;
   private final String category;
   private ClassicConfigGUI$ConfigListWidget_neoforge configListWidget;
   private boolean reload = false;
   private Button doneButton;

   protected DhConfigScreen_neoforge(Screen parent, String category) {
      super(
         GuiHelper_neoforge.Translatable(
            LANG_WRAPPER.langExists("distanthorizons.config" + (category.isEmpty() ? "." + category : "") + ".title")
               ? "distanthorizons.config.title"
               : "distanthorizons.config" + (category.isEmpty() ? "" : "." + category) + ".title"
         )
      );
      this.parent = parent;
      this.category = category;
   }

   public void tick() {
      super.tick();
   }

   protected void init() {
      super.init();
      if (!this.reload) {
         ConfigHandler.INSTANCE.configFileHandler.loadFromFile();
      }

      if (Config.Client.Advanced.AutoUpdater.enableAutoUpdater.get() && !ModInfo.IS_DEV_BUILD) {
         this.addBtn(
            new TexturedButtonWidget_neoforge(
               this.width - 28,
               this.height - 28,
               20,
               20,
               0,
               0,
               0,
               ResourceLocation.fromNamespaceAndPath("distanthorizons", "textures/gui/changelog.png"),
               20,
               20,
               buttonWidget -> {
                  ChangelogScreen_neoforge changelogScreen = new ChangelogScreen_neoforge(this);
                  if (changelogScreen.usable) {
                     DhScreenUtil_neoforge.setScreen(changelogScreen);
                  } else {
                     LOGGER.warn("Changelog was not able to open");
                  }
               },
               GuiHelper_neoforge.Translatable("distanthorizons.updater.title")
            )
         );
      }

      this.addBtn(
         GuiHelper_neoforge.MakeBtn(
            GuiHelper_neoforge.Translatable("distanthorizons.general.back"), this.width / 2 - 154, this.height - 28, 150, 20, button -> {
               ConfigHandler.INSTANCE.configFileHandler.loadFromFile();
               DhScreenUtil_neoforge.setScreen(this.parent);
            }
         )
      );
      this.doneButton = this.addBtn(
         GuiHelper_neoforge.MakeBtn(GuiHelper_neoforge.Translatable("distanthorizons.general.done"), this.width / 2 + 4, this.height - 28, 150, 20, button -> {
            ConfigHandler.INSTANCE.configFileHandler.saveToFile();
            DhScreenUtil_neoforge.setScreen(this.parent);
         })
      );
      this.configListWidget = new ClassicConfigGUI$ConfigListWidget_neoforge(this.minecraft, this.width * 2, this.height, 32, 32, 25);
      this.addWidget(this.configListWidget);

      for (AbstractConfigBase<?> configEntry : ConfigHandler.INSTANCE.configBaseList) {
         try {
            if (configEntry.getCategory().matches(this.category) && configEntry.getAppearance().showInGui) {
               this.addMenuItem(configEntry);
            }
         } catch (Exception var5) {
            String message = "ERROR: Failed to show [" + configEntry.getNameAndCategory() + "], error: [" + var5.getMessage() + "]";
            if (configEntry.get() != null) {
               message = message + " with the value [" + configEntry.get() + "] with type [" + configEntry.getType() + "]";
            }

            LOGGER.error(message, var5);
         }
      }

      ClassicConfigGUI_neoforge.CONFIG_CORE_INTERFACE.onScreenChangeListenerList.forEach(listener -> listener.run());
   }

   private void addMenuItem(AbstractConfigBase<?> configEntry) {
      trySetupConfigEntry(configEntry);
      if (!this.tryCreateInputField(configEntry)) {
         if (!this.tryCreateCategoryButton(configEntry)) {
            if (!this.tryCreateButton(configEntry)) {
               if (!this.tryCreateComment(configEntry)) {
                  if (!this.tryCreateSpacer(configEntry)) {
                     if (!this.tryCreateLinkedEntry(configEntry)) {
                        LOGGER.warn("Config [" + configEntry.getNameAndCategory() + "] failed to show. Please try something like changing its type.");
                     }
                  }
               }
            }
         }
      }
   }

   private static void trySetupConfigEntry(AbstractConfigBase<?> configMenuOption) {
      configMenuOption.guiValue = new ConfigGuiInfo_neoforge();
      Class<?> configValueClass = configMenuOption.getType();
      if (configMenuOption instanceof ConfigEntry<?> configEntry) {
         if (configValueClass == Integer.class) {
            setupTextMenuOption(configEntry, Integer::parseInt, ClassicConfigGUI_neoforge.INTEGER_ONLY_REGEX, true);
         } else if (configValueClass == Double.class) {
            setupTextMenuOption(configEntry, Double::parseDouble, ClassicConfigGUI_neoforge.DECIMAL_ONLY_REGEX, false);
         } else if (configValueClass == Float.class) {
            setupTextMenuOption(configEntry, Float::parseFloat, ClassicConfigGUI_neoforge.DECIMAL_ONLY_REGEX, false);
         } else if (configValueClass == String.class || configValueClass == List.class) {
            setupTextMenuOption(configEntry, String::length, null, true);
         } else if (configValueClass == Boolean.class) {
            setupBooleanMenuOption((ConfigEntry<Boolean>)configEntry);
         } else if (configValueClass.isEnum()) {
            setupEnumMenuOption((ConfigEntry<Enum<?>>)configEntry, (Class<? extends Enum<?>>)configValueClass);
         } else {
            LOGGER.error("No definition for config with type: [" + configValueClass.getName() + "], for config: [" + configMenuOption.name + "].");
         }
      }
   }

   private static void setupTextMenuOption(
      AbstractConfigBase<?> configMenuOption, Function<String, Number> parsingFunc, @Nullable Pattern pattern, boolean cast
   ) {
      ConfigGuiInfo_neoforge configGuiInfo = (ConfigGuiInfo_neoforge)configMenuOption.guiValue;
      configGuiInfo.tooltipFunction = (editBox, button) -> stringValue -> {
         boolean isNumber = pattern != null;
         stringValue = stringValue.trim();
         if (!stringValue.isEmpty() && isNumber && !pattern.matcher(stringValue).matches()) {
            return false;
         } else {
            Number numberValue = configMenuOption.typeIsFloatingPointNumber() ? 0.0 : 0.0;
            configGuiInfo.errorMessage = null;
            if (isNumber && !stringValue.isEmpty() && !stringValue.equals("-") && !stringValue.equals(".")) {
               ConfigEntry<Number> numberConfigEntry = (ConfigEntry<Number>)configMenuOption;

               try {
                  numberValue = parsingFunc.apply(stringValue);
               } catch (Exception var11) {
                  numberValue = null;
               }

               EConfigValidity validity = numberConfigEntry.getValidity(numberValue);
               switch (validity) {
                  case VALID:
                     configGuiInfo.errorMessage = null;
                     break;
                  case NUMBER_TOO_LOW:
                     configGuiInfo.errorMessage = GuiHelper_neoforge.TextOrTranslatable("§cMinimum length is " + numberConfigEntry.getMin());
                     break;
                  case NUMBER_TOO_HIGH:
                     configGuiInfo.errorMessage = GuiHelper_neoforge.TextOrTranslatable("§cMaximum length is " + numberConfigEntry.getMax());
                     break;
                  case INVALID:
                     configGuiInfo.errorMessage = GuiHelper_neoforge.TextOrTranslatable("§cValue is invalid");
               }
            }

            editBox.setTextColor(((ConfigEntry)configMenuOption).getValidity(numberValue) == EConfigValidity.VALID ? -1 : -34953);
            if (configMenuOption.getType() == String.class || configMenuOption.getType() == List.class) {
               ((AbstractConfigBase<String>)((ConfigEntry)configMenuOption)).uiSetWithoutSaving(stringValue);
            } else if (((AbstractConfigBase<Number>)((ConfigEntry)configMenuOption)).getValidity(numberValue) == EConfigValidity.VALID) {
               if (!cast) {
                  ((AbstractConfigBase<Number>)((ConfigEntry)configMenuOption)).uiSetWithoutSaving(numberValue);
               } else {
                  ((AbstractConfigBase<Integer>)((ConfigEntry)configMenuOption)).uiSetWithoutSaving(numberValue != null ? numberValue.intValue() : 0);
               }
            }

            return true;
         }
      };
   }

   private static void setupBooleanMenuOption(ConfigEntry<Boolean> booleanConfigEntry) {
      Function<Object, Component> func = value -> GuiHelper_neoforge.Translatable("distanthorizons.general." + ((Boolean)value ? "true" : "false"))
         .withStyle((Boolean)value ? ChatFormatting.GREEN : ChatFormatting.RED);
      ConfigGuiInfo_neoforge configGuiInfo = (ConfigGuiInfo_neoforge)booleanConfigEntry.guiValue;
      configGuiInfo.buttonOptionMap = new SimpleEntry<>(button -> {
         button.active = !booleanConfigEntry.apiIsOverriding();
         booleanConfigEntry.uiSetWithoutSaving(!booleanConfigEntry.get());
         button.setMessage(func.apply(booleanConfigEntry.get()));
      }, func);
   }

   private static void setupEnumMenuOption(ConfigEntry<Enum<?>> enumConfigEntry, Class<? extends Enum<?>> enumClass) {
      List<Enum<?>> enumList = Arrays.asList((Enum<?>[])enumClass.getEnumConstants());
      ConfigGuiInfo_neoforge configGuiInfo = (ConfigGuiInfo_neoforge)enumConfigEntry.guiValue;
      Function<Object, Component> getEnumTranslatableFunc = value -> GuiHelper_neoforge.Translatable(
         "distanthorizons.config.enum." + enumClass.getSimpleName() + "." + enumConfigEntry.get().toString()
      );
      configGuiInfo.buttonOptionMap = new SimpleEntry<>(
         button -> {
            int startingIndex = enumList.indexOf(enumConfigEntry.get());
            Enum<?> enumValue = enumList.get(startingIndex);
            boolean shiftPressed = InputConstants.isKeyDown(MC_CLIENT.getGlfwWindowId(), 340) || InputConstants.isKeyDown(MC_CLIENT.getGlfwWindowId(), 344);
            int index = shiftPressed ? startingIndex - 1 : startingIndex + 1;
            if (index >= enumList.size()) {
               index = 0;
            } else if (index < 0) {
               index = enumList.size() - 1;
            }

            while (index != startingIndex) {
               enumValue = enumList.get(index);
               if (!AnnotationUtil.doesEnumHaveAnnotation(enumValue, DisallowSelectingViaConfigGui.class)) {
                  break;
               }

               index = shiftPressed ? index - 1 : index + 1;
               if (index >= enumList.size()) {
                  index = 0;
               } else if (index < 0) {
                  index = enumList.size() - 1;
               }
            }

            if (index == startingIndex) {
               enumValue = enumList.get(startingIndex);
               LOGGER.warn(
                  "Enum ["
                     + enumValue.getClass()
                     + "] doesn't contain any values that should be selectable via the UI, sticking to the currently selected value ["
                     + enumValue
                     + "]."
               );
            }

            enumConfigEntry.uiSetWithoutSaving(enumValue);
            button.active = !enumConfigEntry.apiIsOverriding();
            button.setMessage(getEnumTranslatableFunc.apply(enumConfigEntry.get()));
         },
         getEnumTranslatableFunc
      );
   }

   private boolean tryCreateInputField(AbstractConfigBase<?> configBase) {
      ConfigGuiInfo_neoforge configGuiInfo = (ConfigGuiInfo_neoforge)configBase.guiValue;
      if (configBase instanceof ConfigEntry configEntry) {
         OnPress btnAction = button -> {
            configEntry.uiSetWithoutSaving(configEntry.getDefaultValue());
            this.reload = true;
            DhScreenUtil_neoforge.setScreen(this);
         };
         int resetButtonPosX = this.width - 60 - 10;
         int resetButtonPosZ = 0;
         Button resetButton = GuiHelper_neoforge.MakeBtn(
            GuiHelper_neoforge.Translatable("distanthorizons.general.reset").withStyle(ChatFormatting.RED), resetButtonPosX, resetButtonPosZ, 60, 20, btnAction
         );
         if (configEntry.mcVersionOverridePresent()) {
            resetButton.active = false;
            resetButton.setMessage(GuiHelper_neoforge.Translatable("distanthorizons.general.unsupportedMcVersion").withStyle(ChatFormatting.DARK_GRAY));
         } else if (configEntry.apiIsOverriding()) {
            resetButton.active = false;
            resetButton.setMessage(GuiHelper_neoforge.Translatable("distanthorizons.general.apiOverride").withStyle(ChatFormatting.DARK_GRAY));
         } else {
            resetButton.active = true;
         }

         Component textComponent = this.GetTranslatableTextComponentForConfig(configEntry);
         int optionFieldPosX = this.width - 10 - 60 - 5 - 150;
         int optionFieldPosZ = 0;
         if (configGuiInfo.buttonOptionMap != null) {
            Entry<OnPress, Function<Object, Component>> widget = configGuiInfo.buttonOptionMap;
            if (configEntry.getType().isEnum()) {
               widget.setValue(
                  value -> GuiHelper_neoforge.Translatable(
                     "distanthorizons.config.enum." + configEntry.getType().getSimpleName() + "." + configEntry.get().toString()
                  )
               );
            }

            Button button = GuiHelper_neoforge.MakeBtn(widget.getValue().apply(configEntry.get()), optionFieldPosX, optionFieldPosZ, 150, 20, widget.getKey());
            if (configEntry.mcVersionOverridePresent() || configEntry.apiIsOverriding()) {
               button.active = false;
            }

            this.configListWidget.addButton(this, configEntry, button, resetButton, null, textComponent);
            return true;
         } else {
            EditBox widgetx = new EditBox(this.font, optionFieldPosX, optionFieldPosZ, 146, 20, GuiHelper_neoforge.Translatable(""));
            widgetx.setMaxLength(3000000);
            widgetx.insertText(String.valueOf(configEntry.get()));
            Predicate<String> processor = configGuiInfo.tooltipFunction.apply(widgetx, this.doneButton);
            widgetx.setFilter(processor);
            this.configListWidget.addButton(this, configEntry, widgetx, resetButton, null, textComponent);
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean tryCreateCategoryButton(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigCategory configCategory) {
         Component textComponent = this.GetTranslatableTextComponentForConfig(configCategory);
         int categoryPosX = this.width - 200 - 10;
         int categoryPosZ = this.height - 20;
         Button widget = GuiHelper_neoforge.MakeBtn(textComponent, categoryPosX, categoryPosZ, 200, 20, button -> {
            ConfigHandler.INSTANCE.configFileHandler.saveToFile();
            DhScreenUtil_neoforge.setScreen(ClassicConfigGUI_neoforge.getScreen(this, configCategory.getDestination()));
         });
         this.configListWidget.addButton(this, configType, widget, null, null, null);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryCreateButton(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigUIButton configUiButton) {
         Component textComponent = this.GetTranslatableTextComponentForConfig(configUiButton);
         int buttonPosX = this.width - 200 - 10;
         Button widget = GuiHelper_neoforge.MakeBtn(textComponent, buttonPosX, this.height - 28, 200, 20, button -> ((ConfigUIButton)configType).runAction());
         this.configListWidget.addButton(this, configType, widget, null, null, null);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryCreateComment(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigUIComment configUiComment) {
         Component textComponent = this.GetTranslatableTextComponentForConfig(configUiComment);
         if (configUiComment.parentConfigPath != null) {
            textComponent = GuiHelper_neoforge.Translatable("distanthorizons.config." + configUiComment.parentConfigPath);
         }

         this.configListWidget.addButton(this, configType, null, null, null, textComponent);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryCreateSpacer(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigUISpacer) {
         Button spacerButton = GuiHelper_neoforge.MakeBtn(GuiHelper_neoforge.Translatable("distanthorizons.general.spacer"), 10, 10, 1, 1, button -> {});
         spacerButton.visible = false;
         this.configListWidget.addButton(this, configType, spacerButton, null, null, null);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryCreateLinkedEntry(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigUiLinkedEntry) {
         this.addMenuItem(((ConfigUiLinkedEntry)configType).get());
         return true;
      } else {
         return false;
      }
   }

   private Component GetTranslatableTextComponentForConfig(AbstractConfigBase<?> configType) {
      return GuiHelper_neoforge.Translatable("distanthorizons.config." + configType.getNameAndCategory());
   }

   public void render(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
      super.render(matrices, mouseX, mouseY, delta);
      this.configListWidget.render(matrices, mouseX, mouseY, delta);
      this.DhDrawCenteredString(matrices, this.font, this.title, this.width / 2, 15, 16777215);
      this.DhDrawString(matrices, this.font, GuiHelper_neoforge.TextOrLiteral("3.2.0-b"), 2, this.height - 10, 11184810);
      if (SelfUpdater.deleteOldJarOnJvmShutdown) {
         this.DhDrawString(matrices, this.font, GuiHelper_neoforge.Translatable("distanthorizons.updater.waitingForClose"), 4, this.height - 42, 16777215);
      }

      this.renderTooltip(matrices, mouseX, mouseY, delta);
   }

   private void renderTooltip(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
      AbstractWidget hoveredWidget = this.configListWidget.getHoveredButton(mouseX, mouseY);
      if (hoveredWidget != null) {
         ClassicConfigGUI$DhButtonEntry_neoforge button = ClassicConfigGUI$DhButtonEntry_neoforge.BUTTON_BY_WIDGET.get(hoveredWidget);
         AbstractConfigBase<?> configBase = ConfigUiLinkedEntry.class.isAssignableFrom(button.dhConfigType.getClass())
            ? ((ConfigUiLinkedEntry)button.dhConfigType).get()
            : button.dhConfigType;
         boolean apiOverrideActive = false;
         boolean unsupportedMcVersion = false;
         if (configBase instanceof ConfigEntry) {
            apiOverrideActive = ((ConfigEntry)configBase).apiIsOverriding();
            unsupportedMcVersion = ((ConfigEntry)configBase).mcVersionOverridePresent();
         }

         String key = "distanthorizons.config." + (configBase.category.isEmpty() ? "" : configBase.category + ".") + configBase.getName() + ".@tooltip";
         if (unsupportedMcVersion) {
            key = "distanthorizons.general.unsupportedMcVersion.@tooltip";
         } else if (apiOverrideActive) {
            key = "distanthorizons.general.disabledByApi.@tooltip";
         }

         ConfigGuiInfo_neoforge configGuiInfo = (ConfigGuiInfo_neoforge)configBase.guiValue;
         if (configGuiInfo.errorMessage != null) {
            this.DhRenderTooltip(matrices, this.font, configGuiInfo.errorMessage, mouseX, mouseY);
         } else if (LANG_WRAPPER.langExists(key)) {
            List<Component> list = new ArrayList<>();
            String lang = LANG_WRAPPER.getLang(key);

            for (String langLine : lang.split("\n")) {
               list.add(GuiHelper_neoforge.TextOrTranslatable(langLine));
            }

            this.DhRenderComponentTooltip(matrices, this.font, list, mouseX, mouseY);
         }
      }
   }

   public void onClose() {
      ConfigHandler.INSTANCE.configFileHandler.saveToFile();
      DhScreenUtil_neoforge.setScreen(this.parent);
      ClassicConfigGUI_neoforge.CONFIG_CORE_INTERFACE.onScreenChangeListenerList.forEach(listener -> listener.run());
   }
}

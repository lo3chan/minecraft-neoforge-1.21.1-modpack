package com.seibel.distanthorizons.common.wrappers.gui.classicConfig;

import com.seibel.distanthorizons.api.enums.config.DisallowSelectingViaConfigGui;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreen_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.GuiHelper_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.TexturedButtonWidget_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.config.ConfigGuiInfo_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.updater.ChangelogScreen_fabric;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftClientWrapper_fabric;
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
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_3675;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4185.class_4241;
import org.jetbrains.annotations.Nullable;

class DhConfigScreen_fabric extends DhScreen_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final ILangWrapper LANG_WRAPPER = SingletonInjector.INSTANCE.get(ILangWrapper.class);
   private static final String TRANSLATION_PREFIX = "distanthorizons.config.";
   private static final MinecraftClientWrapper_fabric MC_CLIENT = MinecraftClientWrapper_fabric.INSTANCE;
   private final class_437 parent;
   private final String category;
   private ClassicConfigGUI$ConfigListWidget_fabric configListWidget;
   private boolean reload = false;
   private class_4185 doneButton;

   protected DhConfigScreen_fabric(class_437 parent, String category) {
      super(
         GuiHelper_fabric.Translatable(
            LANG_WRAPPER.langExists("distanthorizons.config" + (category.isEmpty() ? "." + category : "") + ".title")
               ? "distanthorizons.config.title"
               : "distanthorizons.config" + (category.isEmpty() ? "" : "." + category) + ".title"
         )
      );
      this.parent = parent;
      this.category = category;
   }

   public void method_25393() {
      super.method_25393();
   }

   protected void method_25426() {
      super.method_25426();
      if (!this.reload) {
         ConfigHandler.INSTANCE.configFileHandler.loadFromFile();
      }

      if (Config.Client.Advanced.AutoUpdater.enableAutoUpdater.get() && !ModInfo.IS_DEV_BUILD) {
         this.addBtn(
            new TexturedButtonWidget_fabric(
               this.field_22789 - 28,
               this.field_22790 - 28,
               20,
               20,
               0,
               0,
               0,
               class_2960.method_60655("distanthorizons", "textures/gui/changelog.png"),
               20,
               20,
               buttonWidget -> {
                  ChangelogScreen_fabric changelogScreen = new ChangelogScreen_fabric(this);
                  if (changelogScreen.usable) {
                     DhScreenUtil_fabric.setScreen(changelogScreen);
                  } else {
                     LOGGER.warn("Changelog was not able to open");
                  }
               },
               GuiHelper_fabric.Translatable("distanthorizons.updater.title")
            )
         );
      }

      this.addBtn(
         GuiHelper_fabric.MakeBtn(
            GuiHelper_fabric.Translatable("distanthorizons.general.back"), this.field_22789 / 2 - 154, this.field_22790 - 28, 150, 20, button -> {
               ConfigHandler.INSTANCE.configFileHandler.loadFromFile();
               DhScreenUtil_fabric.setScreen(this.parent);
            }
         )
      );
      this.doneButton = this.addBtn(
         GuiHelper_fabric.MakeBtn(
            GuiHelper_fabric.Translatable("distanthorizons.general.done"), this.field_22789 / 2 + 4, this.field_22790 - 28, 150, 20, button -> {
               ConfigHandler.INSTANCE.configFileHandler.saveToFile();
               DhScreenUtil_fabric.setScreen(this.parent);
            }
         )
      );
      this.configListWidget = new ClassicConfigGUI$ConfigListWidget_fabric(this.field_22787, this.field_22789 * 2, this.field_22790, 32, 32, 25);
      this.method_25429(this.configListWidget);

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

      ClassicConfigGUI_fabric.CONFIG_CORE_INTERFACE.onScreenChangeListenerList.forEach(listener -> listener.run());
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
      configMenuOption.guiValue = new ConfigGuiInfo_fabric();
      Class<?> configValueClass = configMenuOption.getType();
      if (configMenuOption instanceof ConfigEntry<?> configEntry) {
         if (configValueClass == Integer.class) {
            setupTextMenuOption(configEntry, Integer::parseInt, ClassicConfigGUI_fabric.INTEGER_ONLY_REGEX, true);
         } else if (configValueClass == Double.class) {
            setupTextMenuOption(configEntry, Double::parseDouble, ClassicConfigGUI_fabric.DECIMAL_ONLY_REGEX, false);
         } else if (configValueClass == Float.class) {
            setupTextMenuOption(configEntry, Float::parseFloat, ClassicConfigGUI_fabric.DECIMAL_ONLY_REGEX, false);
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
      ConfigGuiInfo_fabric configGuiInfo = (ConfigGuiInfo_fabric)configMenuOption.guiValue;
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
                     configGuiInfo.errorMessage = GuiHelper_fabric.TextOrTranslatable("§cMinimum length is " + numberConfigEntry.getMin());
                     break;
                  case NUMBER_TOO_HIGH:
                     configGuiInfo.errorMessage = GuiHelper_fabric.TextOrTranslatable("§cMaximum length is " + numberConfigEntry.getMax());
                     break;
                  case INVALID:
                     configGuiInfo.errorMessage = GuiHelper_fabric.TextOrTranslatable("§cValue is invalid");
               }
            }

            editBox.method_1868(((ConfigEntry)configMenuOption).getValidity(numberValue) == EConfigValidity.VALID ? -1 : -34953);
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
      Function<Object, class_2561> func = value -> GuiHelper_fabric.Translatable("distanthorizons.general." + ((Boolean)value ? "true" : "false"))
         .method_27692((Boolean)value ? class_124.field_1060 : class_124.field_1061);
      ConfigGuiInfo_fabric configGuiInfo = (ConfigGuiInfo_fabric)booleanConfigEntry.guiValue;
      configGuiInfo.buttonOptionMap = new SimpleEntry<>(button -> {
         button.field_22763 = !booleanConfigEntry.apiIsOverriding();
         booleanConfigEntry.uiSetWithoutSaving(!booleanConfigEntry.get());
         button.method_25355(func.apply(booleanConfigEntry.get()));
      }, func);
   }

   private static void setupEnumMenuOption(ConfigEntry<Enum<?>> enumConfigEntry, Class<? extends Enum<?>> enumClass) {
      List<Enum<?>> enumList = Arrays.asList((Enum<?>[])enumClass.getEnumConstants());
      ConfigGuiInfo_fabric configGuiInfo = (ConfigGuiInfo_fabric)enumConfigEntry.guiValue;
      Function<Object, class_2561> getEnumTranslatableFunc = value -> GuiHelper_fabric.Translatable(
         "distanthorizons.config.enum." + enumClass.getSimpleName() + "." + enumConfigEntry.get().toString()
      );
      configGuiInfo.buttonOptionMap = new SimpleEntry<>(
         button -> {
            int startingIndex = enumList.indexOf(enumConfigEntry.get());
            Enum<?> enumValue = enumList.get(startingIndex);
            boolean shiftPressed = class_3675.method_15987(MC_CLIENT.getGlfwWindowId(), 340) || class_3675.method_15987(MC_CLIENT.getGlfwWindowId(), 344);
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
            button.field_22763 = !enumConfigEntry.apiIsOverriding();
            button.method_25355(getEnumTranslatableFunc.apply(enumConfigEntry.get()));
         },
         getEnumTranslatableFunc
      );
   }

   private boolean tryCreateInputField(AbstractConfigBase<?> configBase) {
      ConfigGuiInfo_fabric configGuiInfo = (ConfigGuiInfo_fabric)configBase.guiValue;
      if (configBase instanceof ConfigEntry configEntry) {
         class_4241 btnAction = button -> {
            configEntry.uiSetWithoutSaving(configEntry.getDefaultValue());
            this.reload = true;
            DhScreenUtil_fabric.setScreen(this);
         };
         int resetButtonPosX = this.field_22789 - 60 - 10;
         int resetButtonPosZ = 0;
         class_4185 resetButton = GuiHelper_fabric.MakeBtn(
            GuiHelper_fabric.Translatable("distanthorizons.general.reset").method_27692(class_124.field_1061),
            resetButtonPosX,
            resetButtonPosZ,
            60,
            20,
            btnAction
         );
         if (configEntry.mcVersionOverridePresent()) {
            resetButton.field_22763 = false;
            resetButton.method_25355(GuiHelper_fabric.Translatable("distanthorizons.general.unsupportedMcVersion").method_27692(class_124.field_1063));
         } else if (configEntry.apiIsOverriding()) {
            resetButton.field_22763 = false;
            resetButton.method_25355(GuiHelper_fabric.Translatable("distanthorizons.general.apiOverride").method_27692(class_124.field_1063));
         } else {
            resetButton.field_22763 = true;
         }

         class_2561 textComponent = this.GetTranslatableTextComponentForConfig(configEntry);
         int optionFieldPosX = this.field_22789 - 10 - 60 - 5 - 150;
         int optionFieldPosZ = 0;
         if (configGuiInfo.buttonOptionMap != null) {
            Entry<class_4241, Function<Object, class_2561>> widget = configGuiInfo.buttonOptionMap;
            if (configEntry.getType().isEnum()) {
               widget.setValue(
                  value -> GuiHelper_fabric.Translatable(
                     "distanthorizons.config.enum." + configEntry.getType().getSimpleName() + "." + configEntry.get().toString()
                  )
               );
            }

            class_4185 button = GuiHelper_fabric.MakeBtn(widget.getValue().apply(configEntry.get()), optionFieldPosX, optionFieldPosZ, 150, 20, widget.getKey());
            if (configEntry.mcVersionOverridePresent() || configEntry.apiIsOverriding()) {
               button.field_22763 = false;
            }

            this.configListWidget.addButton(this, configEntry, button, resetButton, null, textComponent);
            return true;
         } else {
            class_342 widgetx = new class_342(this.field_22793, optionFieldPosX, optionFieldPosZ, 146, 20, GuiHelper_fabric.Translatable(""));
            widgetx.method_1880(3000000);
            widgetx.method_1867(String.valueOf(configEntry.get()));
            Predicate<String> processor = configGuiInfo.tooltipFunction.apply(widgetx, this.doneButton);
            widgetx.method_1890(processor);
            this.configListWidget.addButton(this, configEntry, widgetx, resetButton, null, textComponent);
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean tryCreateCategoryButton(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigCategory configCategory) {
         class_2561 textComponent = this.GetTranslatableTextComponentForConfig(configCategory);
         int categoryPosX = this.field_22789 - 200 - 10;
         int categoryPosZ = this.field_22790 - 20;
         class_4185 widget = GuiHelper_fabric.MakeBtn(textComponent, categoryPosX, categoryPosZ, 200, 20, button -> {
            ConfigHandler.INSTANCE.configFileHandler.saveToFile();
            DhScreenUtil_fabric.setScreen(ClassicConfigGUI_fabric.getScreen(this, configCategory.getDestination()));
         });
         this.configListWidget.addButton(this, configType, widget, null, null, null);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryCreateButton(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigUIButton configUiButton) {
         class_2561 textComponent = this.GetTranslatableTextComponentForConfig(configUiButton);
         int buttonPosX = this.field_22789 - 200 - 10;
         class_4185 widget = GuiHelper_fabric.MakeBtn(
            textComponent, buttonPosX, this.field_22790 - 28, 200, 20, button -> ((ConfigUIButton)configType).runAction()
         );
         this.configListWidget.addButton(this, configType, widget, null, null, null);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryCreateComment(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigUIComment configUiComment) {
         class_2561 textComponent = this.GetTranslatableTextComponentForConfig(configUiComment);
         if (configUiComment.parentConfigPath != null) {
            textComponent = GuiHelper_fabric.Translatable("distanthorizons.config." + configUiComment.parentConfigPath);
         }

         this.configListWidget.addButton(this, configType, null, null, null, textComponent);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryCreateSpacer(AbstractConfigBase<?> configType) {
      if (configType instanceof ConfigUISpacer) {
         class_4185 spacerButton = GuiHelper_fabric.MakeBtn(GuiHelper_fabric.Translatable("distanthorizons.general.spacer"), 10, 10, 1, 1, button -> {});
         spacerButton.field_22764 = false;
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

   private class_2561 GetTranslatableTextComponentForConfig(AbstractConfigBase<?> configType) {
      return GuiHelper_fabric.Translatable("distanthorizons.config." + configType.getNameAndCategory());
   }

   public void method_25394(class_332 matrices, int mouseX, int mouseY, float delta) {
      super.method_25394(matrices, mouseX, mouseY, delta);
      this.configListWidget.method_25394(matrices, mouseX, mouseY, delta);
      this.DhDrawCenteredString(matrices, this.field_22793, this.field_22785, this.field_22789 / 2, 15, 16777215);
      this.DhDrawString(matrices, this.field_22793, GuiHelper_fabric.TextOrLiteral("3.2.0-b"), 2, this.field_22790 - 10, 11184810);
      if (SelfUpdater.deleteOldJarOnJvmShutdown) {
         this.DhDrawString(
            matrices, this.field_22793, GuiHelper_fabric.Translatable("distanthorizons.updater.waitingForClose"), 4, this.field_22790 - 42, 16777215
         );
      }

      this.renderTooltip(matrices, mouseX, mouseY, delta);
   }

   private void renderTooltip(class_332 matrices, int mouseX, int mouseY, float delta) {
      class_339 hoveredWidget = this.configListWidget.getHoveredButton(mouseX, mouseY);
      if (hoveredWidget != null) {
         ClassicConfigGUI$DhButtonEntry_fabric button = ClassicConfigGUI$DhButtonEntry_fabric.BUTTON_BY_WIDGET.get(hoveredWidget);
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

         ConfigGuiInfo_fabric configGuiInfo = (ConfigGuiInfo_fabric)configBase.guiValue;
         if (configGuiInfo.errorMessage != null) {
            this.DhRenderTooltip(matrices, this.field_22793, configGuiInfo.errorMessage, mouseX, mouseY);
         } else if (LANG_WRAPPER.langExists(key)) {
            List<class_2561> list = new ArrayList<>();
            String lang = LANG_WRAPPER.getLang(key);

            for (String langLine : lang.split("\n")) {
               list.add(GuiHelper_fabric.TextOrTranslatable(langLine));
            }

            this.DhRenderComponentTooltip(matrices, this.field_22793, list, mouseX, mouseY);
         }
      }
   }

   public void method_25419() {
      ConfigHandler.INSTANCE.configFileHandler.saveToFile();
      DhScreenUtil_fabric.setScreen(this.parent);
      ClassicConfigGUI_fabric.CONFIG_CORE_INTERFACE.onScreenChangeListenerList.forEach(listener -> listener.run());
   }
}

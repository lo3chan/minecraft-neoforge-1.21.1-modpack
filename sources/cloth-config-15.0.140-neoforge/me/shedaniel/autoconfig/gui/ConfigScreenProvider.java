package me.shedaniel.autoconfig.gui;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfigScreenProvider<T extends ConfigData> implements Supplier<Screen> {
   private static final ResourceLocation TRANSPARENT_BACKGROUND = ResourceLocation.parse("cloth-config2:transparent");
   private final ConfigManager<T> manager;
   private final GuiRegistryAccess registry;
   private final Screen parent;
   private Function<ConfigManager<T>, String> i18nFunction = managerx -> String.format("text.autoconfig.%s", managerx.getDefinition().name());
   private Function<ConfigBuilder, Screen> buildFunction = ConfigBuilder::build;
   private BiFunction<String, Field, String> optionFunction = (baseI13n, field) -> String.format("%s.option.%s", baseI13n, field.getName());
   private BiFunction<String, String, String> categoryFunction = (baseI13n, categoryName) -> String.format("%s.category.%s", baseI13n, categoryName);

   public ConfigScreenProvider(ConfigManager<T> manager, GuiRegistryAccess registry, Screen parent) {
      this.manager = manager;
      this.registry = registry;
      this.parent = parent;
   }

   @Deprecated
   public void setI13nFunction(Function<ConfigManager<T>, String> i18nFunction) {
      this.i18nFunction = i18nFunction;
   }

   @Deprecated
   public void setBuildFunction(Function<ConfigBuilder, Screen> buildFunction) {
      this.buildFunction = buildFunction;
   }

   @Deprecated
   public void setCategoryFunction(BiFunction<String, String, String> categoryFunction) {
      this.categoryFunction = categoryFunction;
   }

   @Deprecated
   public void setOptionFunction(BiFunction<String, Field, String> optionFunction) {
      this.optionFunction = optionFunction;
   }

   public Screen get() {
      T config = this.manager.getConfig();
      T defaults = this.manager.getSerializer().createDefault();
      String i18n = this.i18nFunction.apply(this.manager);
      ConfigBuilder builder = ConfigBuilder.create()
         .setParentScreen(this.parent)
         .setTitle(Component.translatable(String.format("%s.title", i18n)))
         .setSavingRunnable(this.manager::save);
      Class<T> configClass = this.manager.getConfigClass();
      if (configClass.isAnnotationPresent(Config.Gui.Background.class)) {
         String bg = configClass.getAnnotation(Config.Gui.Background.class).value();
         ResourceLocation bgId = ResourceLocation.tryParse(bg);
         if (TRANSPARENT_BACKGROUND.equals(bgId)) {
            builder.transparentBackground().setDefaultBackgroundTexture(null);
         } else {
            builder.solidBackground().setDefaultBackgroundTexture(bgId);
         }
      }

      Map<String, ResourceLocation> categoryBackgrounds = Arrays.stream(configClass.getAnnotationsByType(Config.Gui.CategoryBackground.class))
         .collect(Collectors.toMap(Config.Gui.CategoryBackground::category, ann -> ResourceLocation.parse(ann.background())));
      Arrays.stream(configClass.getDeclaredFields())
         .collect(
            Collectors.groupingBy(field -> this.getOrCreateCategoryForField(field, builder, categoryBackgrounds, i18n), LinkedHashMap::new, Collectors.toList())
         )
         .forEach((key, value) -> value.forEach(field -> {
            String optionI13n = this.optionFunction.apply(i18n, field);
            this.registry.getAndTransform(optionI13n, field, config, defaults, this.registry).forEach(key::addEntry);
         }));
      return this.buildFunction.apply(builder);
   }

   private ConfigCategory getOrCreateCategoryForField(Field field, ConfigBuilder screenBuilder, Map<String, ResourceLocation> backgroundMap, String baseI13n) {
      String categoryName = "default";
      if (field.isAnnotationPresent(ConfigEntry.Category.class)) {
         categoryName = field.getAnnotation(ConfigEntry.Category.class).value();
      }

      Component categoryKey = Component.translatable(this.categoryFunction.apply(baseI13n, categoryName));
      if (!screenBuilder.hasCategory(categoryKey)) {
         ConfigCategory category = screenBuilder.getOrCreateCategory(categoryKey);
         if (backgroundMap.containsKey(categoryName)) {
            category.setCategoryBackground(backgroundMap.get(categoryName));
         }

         return category;
      } else {
         return screenBuilder.getOrCreateCategory(categoryKey);
      }
   }
}

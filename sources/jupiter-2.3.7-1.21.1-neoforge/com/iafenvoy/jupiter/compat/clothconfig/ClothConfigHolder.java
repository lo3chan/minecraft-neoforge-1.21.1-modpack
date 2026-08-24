package com.iafenvoy.jupiter.compat.clothconfig;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.compat.ExtraConfigHolder;
import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.ConfigSide;
import com.iafenvoy.jupiter.config.ConfigSource;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.ConfigGroupEntry;
import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.config.entry.EnumEntry;
import com.iafenvoy.jupiter.config.entry.FloatEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.config.entry.ListBooleanEntry;
import com.iafenvoy.jupiter.config.entry.ListDoubleEntry;
import com.iafenvoy.jupiter.config.entry.ListIntegerEntry;
import com.iafenvoy.jupiter.config.entry.ListLongEntry;
import com.iafenvoy.jupiter.config.entry.ListStringEntry;
import com.iafenvoy.jupiter.config.entry.LongEntry;
import com.iafenvoy.jupiter.config.entry.StringEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigBuilder;
import com.iafenvoy.jupiter.util.JupiterUtils;
import com.iafenvoy.jupiter.util.RLUtil;
import com.iafenvoy.jupiter.util.TextUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.annotation.Config.Gui.Background;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class ClothConfigHolder<D extends ConfigData> implements ExtraConfigHolder {
   private final ConfigManager<D> manager;
   private final String modId;
   private final D values;
   private final D defaults;

   public ClothConfigHolder(ConfigManager<D> manager) {
      this.manager = manager;
      this.modId = this.manager.getDefinition().name().toLowerCase(Locale.ROOT);
      this.values = (D)manager.getConfig();
      this.defaults = (D)manager.getSerializer().createDefault();
   }

   public String getModId() {
      return this.modId;
   }

   @Override
   public ResourceLocation getConfigId() {
      return RLUtil.id(this.modId, "config");
   }

   @Override
   public String getPath() {
      return String.format(Locale.ROOT, "%s.json", this.modId);
   }

   public String baseTranslateKey() {
      return String.format(Locale.ROOT, "text.autoconfig.%s", this.modId);
   }

   @Override
   public Component getTitle() {
      return TextUtil.translatable(String.format(Locale.ROOT, "%s.title", this.baseTranslateKey()));
   }

   @Override
   public ConfigSide getSide() {
      return ConfigSide.UNKNOWN;
   }

   @Override
   public ConfigSource getSource() {
      return ConfigSource.CLOTH_CONFIG;
   }

   @Override
   public void save() {
      this.manager.save();
   }

   @Nullable
   @Override
   public ResourceLocation getBackgroundTexture(boolean ingame) {
      Background background = this.values.getClass().getAnnotation(Background.class);
      if (background == null) {
         return null;
      } else {
         String id = background.value();
         return "cloth-config2:transparent".equals(id) ? null : RLUtil.tryParse(id);
      }
   }

   @Override
   public Collection<? extends ConfigGroup> buildGroups() {
      return List.of(this.buildGroup(this.getConfigId().toString(), "", this.defaults, this.values));
   }

   public <T> ConfigGroup buildGroup(String id, String parentKey, T defaults, T values) {
      ConfigGroup group = new ConfigGroup(id, TextUtil.translatable(String.format(Locale.ROOT, "%s.category%s", this.baseTranslateKey(), parentKey)));

      for (Field field : defaults.getClass().getDeclaredFields()) {
         if (!Modifier.isStatic(field.getModifiers())
            && !Modifier.isFinal(field.getModifiers())
            && field.canAccess(defaults)
            && field.getAnnotation((Class<T>)Excluded.class) == null) {
            try {
               String nameKey = String.format(Locale.ROOT, "%s.option%s.%s", this.baseTranslateKey(), parentKey, field.getName());
               ConfigBuilder<?, ?, ?> builder = this.process(nameKey, defaults, values, field);
               if (builder == null) {
                  builder = ConfigGroupEntry.builder(
                     String.format(Locale.ROOT, "%s.category%s.%s", this.baseTranslateKey(), parentKey, field.getName()),
                     this.buildGroup(field.getName(), String.format(Locale.ROOT, "%s.%s", parentKey, field.getName()), field.get(defaults), field.get(values))
                  );
               }

               if (field.getAnnotation((Class<T>)Tooltip.class) != null) {
                  builder.tooltip(String.format(Locale.ROOT, "%s.@Tooltip", nameKey));
               }

               group.addEntry(builder.build());
            } catch (Exception var12) {
               Jupiter.LOGGER
                  .error("Failed to load field {} class {} from class {}", new Object[]{field.getName(), field.getType(), defaults.getClass().getName(), var12});
            }
         }
      }

      return group;
   }

   private <T> ConfigBuilder<?, ?, ?> process(String nameKey, T defaults, T values, Field field) {
      AtomicReference<ConfigBuilder<?, ?, ?>> holder = new AtomicReference<>(null);
      Component name = TextUtil.translatable(nameKey);
      this.processEntry(holder, name, field, defaults, values, Boolean.class, BooleanEntry::builder);
      this.processEntry(holder, name, field, defaults, values, Integer.class, IntegerEntry::builder);
      this.processEntry(holder, name, field, defaults, values, Long.class, LongEntry::builder);
      this.processEntry(holder, name, field, defaults, values, Double.class, DoubleEntry::builder);
      this.processEntry(holder, name, field, defaults, values, Float.class, FloatEntry::builder);
      this.processEntry(holder, name, field, defaults, values, String.class, StringEntry::builder);
      this.processEntry(holder, name, field, defaults, values, Enum.class, EnumEntry::builder);
      this.processEntry(holder, name, field, defaults, values, boolean.class, BooleanEntry::builder);
      this.processEntry(holder, name, field, defaults, values, int.class, IntegerEntry::builder);
      this.processEntry(holder, name, field, defaults, values, long.class, LongEntry::builder);
      this.processEntry(holder, name, field, defaults, values, double.class, DoubleEntry::builder);
      this.processEntry(holder, name, field, defaults, values, float.class, FloatEntry::builder);
      if (field.getType().isArray()) {
         this.processArrayEntry(holder, name, field, defaults, values, Boolean.class, ListBooleanEntry::builder);
         this.processArrayEntry(holder, name, field, defaults, values, Integer.class, ListIntegerEntry::builder);
         this.processArrayEntry(holder, name, field, defaults, values, Long.class, ListLongEntry::builder);
         this.processArrayEntry(holder, name, field, defaults, values, Double.class, ListDoubleEntry::builder);
         this.processArrayEntry(holder, name, field, defaults, values, String.class, ListStringEntry::builder);
      }

      if (List.class.isAssignableFrom(field.getType())) {
         this.processCollectionEntry(holder, name, field, defaults, values, Boolean.class, ListBooleanEntry::builder);
         this.processCollectionEntry(holder, name, field, defaults, values, Integer.class, ListIntegerEntry::builder);
         this.processCollectionEntry(holder, name, field, defaults, values, Long.class, ListLongEntry::builder);
         this.processCollectionEntry(holder, name, field, defaults, values, Double.class, ListDoubleEntry::builder);
         this.processCollectionEntry(holder, name, field, defaults, values, String.class, ListStringEntry::builder);
      }

      return holder.get();
   }

   private <V, T, B extends ConfigBuilder<T, ?, B>> void processEntry(
      AtomicReference<ConfigBuilder<?, ?, ?>> reference,
      Component name,
      Field field,
      V defaults,
      V values,
      Class<T> clazz,
      BiFunction<Component, T, B> entryProvider
   ) {
      if (clazz.isAssignableFrom(field.getType())) {
         B builder = (B)entryProvider.apply(name, (T)Utils.getUnsafely(field, defaults));
         builder.callback((v, r, d) -> Utils.setUnsafely(field, values, v)).value((T)Utils.getUnsafely(field, values));
         reference.set(builder);
      }
   }

   private <V, T, B extends ConfigBuilder<List<T>, ?, B>> void processArrayEntry(
      AtomicReference<ConfigBuilder<?, ?, ?>> reference,
      Component name,
      Field field,
      V defaults,
      V values,
      Class<T> clazz,
      BiFunction<Component, List<T>, B> entryProvider
   ) {
      if (clazz.isAssignableFrom(field.getType().componentType())) {
         B builder = (B)entryProvider.apply(
            name, List.of((T[])((Object[])Objects.requireNonNullElseGet((Object[])Utils.getUnsafely(field, defaults), () -> Array.newInstance(clazz, 0))))
         );
         builder.callback((v, r, d) -> Utils.setUnsafely(field, values, v.toArray((T[])((Object[])Array.newInstance(clazz, 0)))))
            .value(List.of((T[])((Object[])Objects.requireNonNullElseGet((Object[])Utils.getUnsafely(field, values), () -> Array.newInstance(clazz, 0)))));
         reference.set(builder);
      }
   }

   private <V, T, B extends ConfigBuilder<List<T>, ?, B>> void processCollectionEntry(
      AtomicReference<ConfigBuilder<?, ?, ?>> reference,
      Component name,
      Field field,
      V defaults,
      V values,
      Class<T> clazz,
      BiFunction<Component, List<T>, B> entryProvider
   ) {
      Class<?> actual = JupiterUtils.getGenericActualClass(field);
      if (actual != null && clazz.isAssignableFrom(actual)) {
         B builder = (B)entryProvider.apply(name, Objects.requireNonNullElseGet((List<T>)Utils.getUnsafely(field, defaults), List::of));
         builder.callback((v, r, d) -> Utils.setUnsafely(field, values, v))
            .value(Objects.requireNonNullElseGet((List<T>)Utils.getUnsafely(field, values), List::of));
         reference.set(builder);
      }
   }
}

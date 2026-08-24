package com.iafenvoy.jupiter.compat.forgeconfigspec;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.compat.ExtraConfigHolder;
import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.ConfigSide;
import com.iafenvoy.jupiter.config.ConfigSource;
import com.iafenvoy.jupiter.config.entry.BaseEntry;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.ConfigGroupEntry;
import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.config.entry.EnumEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.config.entry.ListBooleanEntry;
import com.iafenvoy.jupiter.config.entry.ListDoubleEntry;
import com.iafenvoy.jupiter.config.entry.ListEnumEntry;
import com.iafenvoy.jupiter.config.entry.ListIntegerEntry;
import com.iafenvoy.jupiter.config.entry.ListLongEntry;
import com.iafenvoy.jupiter.config.entry.ListStringEntry;
import com.iafenvoy.jupiter.config.entry.LongEntry;
import com.iafenvoy.jupiter.config.entry.SeparatorEntry;
import com.iafenvoy.jupiter.config.entry.StringEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigBuilder;
import com.iafenvoy.jupiter.util.JupiterUtils;
import com.iafenvoy.jupiter.util.RLUtil;
import com.iafenvoy.jupiter.util.TextFormatter;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import org.jetbrains.annotations.Nullable;

public final class NightConfigHolder implements ExtraConfigHolder {
   private final String modId;
   private final ConfigSide side;
   private final String fileName;
   private final UnmodifiableConfig defaults;
   private final CommentedConfig values;
   private final Runnable save;

   public NightConfigHolder(String modId, ConfigSide side, String fileName, UnmodifiableConfig defaults, CommentedConfig values, Runnable save) {
      this.modId = modId;
      this.side = side;
      this.fileName = fileName;
      this.defaults = defaults;
      this.values = values;
      this.save = save;
   }

   @Override
   public ResourceLocation getConfigId() {
      return RLUtil.id(this.modId, this.side.name().toLowerCase(Locale.ROOT));
   }

   @Override
   public Component getTitle() {
      return TextUtil.literal(TextFormatter.formatToTitleCase(this.modId, true))
         .append(" ")
         .append(TextUtil.translatable(String.format(Locale.ROOT, "jupiter.screen.%s_config", this.side.name().toLowerCase(Locale.ROOT))));
   }

   @Override
   public ConfigSide getSide() {
      return this.side;
   }

   @Override
   public ConfigSource getSource() {
      return ConfigSource.NIGHT_CONFIG;
   }

   @Override
   public String getPath() {
      return this.fileName;
   }

   @Override
   public void save() {
      this.save.run();
   }

   @Nullable
   @Override
   public ResourceLocation getBackgroundTexture(boolean ingame) {
      return null;
   }

   public List<ConfigGroup> buildGroups() {
      return List.of(this.buildGroup(this.getConfigId().toString(), this.getTitle(), this.defaults, this.values));
   }

   public ConfigGroup buildGroup(String id, Component groupName, UnmodifiableConfig defaults, CommentedConfig values) {
      ConfigGroup group = new ConfigGroup(id, groupName);

      for (Entry entry : defaults.entrySet()) {
         Object entryValue = entry.getValue();
         Object value = values.get(entry.getKey());
         if (entryValue instanceof ValueSpec spec) {
            Object defaultValue = spec.getDefault();

            try {
               String translateKey = Objects.requireNonNullElseGet(spec.getTranslationKey(), entry::getKey);
               ConfigBuilder<?, ?, ?> builder = this.process(
                  values,
                  TextUtil.translatableWithFallback(translateKey, TextFormatter.formatToTitleCase(translateKey, false)),
                  entry,
                  defaultValue,
                  value,
                  JupiterUtils.packPredicate(spec::test)
               );
               if (builder == null) {
                  Jupiter.LOGGER
                     .warn(
                        "Cannot find suitable entry for key={}, type={} in config={}:{}",
                        new Object[]{entry.getKey(), defaultValue.getClass().getName(), this.modId, this.side}
                     );
               } else {
                  if (builder instanceof BaseEntry.Builder<?, ?, ?> baseBuilder) {
                     baseBuilder.key(entry.getKey());
                     if (spec.getComment() != null) {
                        baseBuilder.tooltip(spec.getComment());
                     }
                  }

                  group.addEntry(builder.build());
               }
            } catch (Exception var17) {
               Jupiter.LOGGER
                  .error(
                     "Cannot load key={}, type={} in config={}:{}",
                     new Object[]{entry.getKey(), defaultValue.getClass().getName(), this.modId, this.side, var17}
                  );
            }
         } else if (entryValue instanceof UnmodifiableConfig spec && value instanceof CommentedConfig config) {
            Component name = TextUtil.translatableWithFallback(entry.getKey(), TextFormatter.formatToTitleCase(entry.getKey(), false));
            group.addEntry(ConfigGroupEntry.builder(name, this.buildGroup(entry.getKey(), name, spec, config)).key(entry.getKey()).build());
         }
      }

      return group;
   }

   private ConfigBuilder<?, ?, ?> process(CommentedConfig values, Component name, Entry entry, Object defaultValue, Object value, Predicate<Object> validator) {
      AtomicReference<ConfigBuilder<?, ?, ?>> holder = new AtomicReference<>(null);
      this.processEntry(holder, values, name, entry, defaultValue, value, Boolean.class, BooleanEntry::builder);
      this.processEntry(holder, values, name, entry, defaultValue, value, Integer.class, IntegerEntry::builder);
      this.processEntry(holder, values, name, entry, defaultValue, value, Long.class, LongEntry::builder);
      this.processEntry(holder, values, name, entry, defaultValue, value, Double.class, DoubleEntry::builder);
      this.processEntry(holder, values, name, entry, defaultValue, value, String.class, StringEntry::builder);
      this.processEnum(holder, values, name, entry, defaultValue, value, defaultValue.getClass());
      if (Collection.class.isAssignableFrom(defaultValue.getClass())) {
         if (validator.test(List.of(""))) {
            this.processCollectionEntry(holder, values, name, entry, defaultValue, value, ListStringEntry::builder);
         } else if (validator.test(List.of(false))) {
            this.processCollectionEntry(holder, values, name, entry, defaultValue, value, ListBooleanEntry::builder);
         } else if (validator.test(List.of(0))) {
            this.processCollectionEntry(holder, values, name, entry, defaultValue, value, ListIntegerEntry::builder);
         } else if (validator.test(List.of(0L))) {
            this.processCollectionEntry(holder, values, name, entry, defaultValue, value, ListLongEntry::builder);
         } else if (validator.test(List.of(0.0))) {
            this.processCollectionEntry(holder, values, name, entry, defaultValue, value, ListDoubleEntry::builder);
         } else {
            Optional<?> any = ((List)defaultValue).stream().findAny();
            if (any.isPresent() && any.get().getClass().isEnum()) {
               this.processEnumCollection(holder, values, name, entry, defaultValue, value, (Enum)any.get());
            } else {
               ConfigSpecLoader.meetEmptyEnumList();
               holder.set(SeparatorEntry.builder().text("jupiter.screen.cannot_process_list_enum").tooltip(name));
            }
         }
      }

      return holder.get();
   }

   private <T, B extends ConfigBuilder<T, ?, B>> void processEntry(
      AtomicReference<ConfigBuilder<?, ?, ?>> reference,
      CommentedConfig values,
      Component name,
      Entry entry,
      Object defaultValue,
      Object value,
      Class<T> clazz,
      BiFunction<Component, T, B> entryProvider
   ) {
      if (clazz.isAssignableFrom(defaultValue.getClass()) && clazz.isAssignableFrom(value.getClass())) {
         B builder = (B)entryProvider.apply(name, (T)defaultValue);
         builder.callback((v, r, d) -> values.set(entry.getKey(), v)).value((T)value);
         reference.set(builder);
      }
   }

   private <T extends Enum<T>> void processEnum(
      AtomicReference<ConfigBuilder<?, ?, ?>> reference, CommentedConfig values, Component name, Entry entry, Object defaultValue, Object value, Class<?> clazz
   ) {
      if (clazz.isEnum() && clazz.isAssignableFrom(defaultValue.getClass()) && value instanceof String valueStr) {
         EnumEntry.Builder<T> builder = EnumEntry.builder(name, (T)defaultValue);
         builder.callback((v, r, d) -> values.set(entry.getKey(), v.name())).value(Enum.valueOf((Class<T>)clazz, valueStr));
         reference.set(builder);
      }
   }

   private <T, B extends ConfigBuilder<List<T>, ?, B>> void processCollectionEntry(
      AtomicReference<ConfigBuilder<?, ?, ?>> reference,
      CommentedConfig values,
      Component name,
      Entry entry,
      Object defaultValue,
      Object value,
      BiFunction<Component, List<T>, B> entryProvider
   ) {
      B builder = (B)entryProvider.apply(name, (List<T>)defaultValue);
      builder.callback((v, r, d) -> values.set(entry.getKey(), v));
      if (value != null) {
         builder.value(new LinkedList<>((List)value));
      }

      reference.set(builder);
   }

   private <T extends Enum<T>> void processEnumCollection(
      AtomicReference<ConfigBuilder<?, ?, ?>> reference, CommentedConfig values, Component name, Entry entry, Object defaultValue, Object value, T any
   ) {
      Class<T> clazz = any.getDeclaringClass();
      ListEnumEntry.Builder<T> builder = ListEnumEntry.builder(name, (List<T>)defaultValue, any);
      builder.callback((v, r, d) -> values.set(entry.getKey(), v.stream().map(Enum::name).toList()))
         .value(new LinkedList<>(((List)value).stream().map(x -> Enum.valueOf(clazz, x)).toList()));
      reference.set(builder);
   }
}

package de.cristelknight.cristellib.config.client.simple.custom;

import de.cristelknight.cristellib.config.simple.custom.AlphaColorField;
import de.cristelknight.cristellib.config.simple.custom.ColorField;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;

public class SimpleScreenTypes {
   private static final List<SimpleScreenTypes.EntryPredicate> FIELD_ENTRIES = new ArrayList<>();

   public static void addEntry(ConfigFieldFactory<?> factory, Class<?>... classes) {
      for (Class<?> clazz : classes) {
         FIELD_ENTRIES.add(
            new SimpleScreenTypes.EntryPredicate(c -> c == clazz, value -> true, new SimpleScreenTypes.ScreenFieldEntry(factory, Optional.empty()))
         );
      }
   }

   public static void addEntry(ConfigFieldFactory<?> factory, Function<Object, ?> toOriginal, Class<?>... classes) {
      for (Class<?> clazz : classes) {
         FIELD_ENTRIES.add(
            new SimpleScreenTypes.EntryPredicate(c -> c == clazz, value -> true, new SimpleScreenTypes.ScreenFieldEntry(factory, Optional.of(toOriginal)))
         );
      }
   }

   public static void addAdvancedEntry(Predicate<Class<?>> classMatcher, Predicate<Object> valueMatcher, ConfigFieldFactory<?> factory) {
      FIELD_ENTRIES.add(
         new SimpleScreenTypes.EntryPredicate(
            Objects.requireNonNull(classMatcher),
            valueMatcher == null ? v -> true : valueMatcher,
            new SimpleScreenTypes.ScreenFieldEntry(factory, Optional.empty())
         )
      );
   }

   public static <V> void addAdvancedEntry(
      Predicate<Class<?>> classMatcher, Predicate<Object> valueMatcher, ConfigFieldFactory<?> factory, Function<Object, V> toOriginal
   ) {
      FIELD_ENTRIES.add(
         new SimpleScreenTypes.EntryPredicate(
            Objects.requireNonNull(classMatcher),
            valueMatcher == null ? v -> true : valueMatcher,
            new SimpleScreenTypes.ScreenFieldEntry(factory, Optional.of(toOriginal))
         )
      );
   }

   public static Optional<SimpleScreenTypes.ScreenFieldEntry> getEntry(Class<?> clazz, Object value) {
      return FIELD_ENTRIES.stream()
         .filter(e -> e.classMatcher().test(clazz) && e.valueMatcher().test(value))
         .map(SimpleScreenTypes.EntryPredicate::entry)
         .findFirst();
   }

   static {
      addEntry(
         (entryBuilder, name, value, defaultValue) -> entryBuilder.startStrField(Component.literal(name), (String)value).setDefaultValue((String)defaultValue),
         String.class
      );
      addEntry(
         (entryBuilder, name, value, defaultValue) -> entryBuilder.startIntField(Component.literal(name), ((Number)value).intValue())
            .setDefaultValue(((Number)defaultValue).intValue()),
         Integer.class,
         int.class
      );
      addEntry(
         (entryBuilder, name, value, defaultValue) -> entryBuilder.startDoubleField(Component.literal(name), ((Number)value).doubleValue())
            .setDefaultValue(((Number)defaultValue).doubleValue()),
         Double.class,
         double.class
      );
      addEntry(
         (entryBuilder, name, value, defaultValue) -> entryBuilder.startBooleanToggle(Component.literal(name), (Boolean)value)
            .setDefaultValue((Boolean)defaultValue),
         Boolean.class,
         boolean.class
      );
      addEntry(
         (entryBuilder, name, value, defaultValue) -> entryBuilder.startColorField(Component.literal(name), ((ColorField)value).toInt())
            .setDefaultValue(((ColorField)defaultValue).toInt()),
         intColor -> ColorField.fromInt((Integer)intColor),
         ColorField.class
      );
      addEntry(
         (entryBuilder, name, value, defaultValue) -> entryBuilder.startAlphaColorField(Component.literal(name), ((AlphaColorField)value).toInt())
            .setDefaultValue(((AlphaColorField)defaultValue).toInt()),
         intColor -> AlphaColorField.fromInt((Integer)intColor),
         AlphaColorField.class
      );
      addAdvancedEntry(
         List.class::isAssignableFrom,
         value -> value instanceof List<?> list && list.stream().allMatch(it -> it instanceof String),
         (entryBuilder, name, value, defaultValue) -> entryBuilder.startStrList(Component.literal(name), (List)value).setDefaultValue((List)defaultValue)
      );
   }

   private record EntryPredicate(Predicate<Class<?>> classMatcher, Predicate<Object> valueMatcher, SimpleScreenTypes.ScreenFieldEntry entry) {
   }

   public record ScreenFieldEntry(ConfigFieldFactory<?> fieldFactory, Optional<Function<Object, ?>> toOriginal) {
      public boolean hasConverter() {
         return this.toOriginal.isPresent();
      }
   }
}

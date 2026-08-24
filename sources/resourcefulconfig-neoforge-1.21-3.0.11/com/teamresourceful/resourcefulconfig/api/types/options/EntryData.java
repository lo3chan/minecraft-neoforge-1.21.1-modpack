package com.teamresourceful.resourcefulconfig.api.types.options;

import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.Optionull;

public record EntryData(TranslatableValue title, TranslatableValue comment, Map<Option<?, ?>, Object> options) {
   public static EntryData.Builder builder() {
      return new EntryData.Builder();
   }

   public static EntryData of(Field field, Class<?> type) {
      return of(field::getAnnotation, type);
   }

   public static EntryData of(AnnotationGetter getter, Class<?> type) {
      ConfigEntry entry = getter.get(ConfigEntry.class);
      EntryData.Builder builder = builder()
         .translation(entry.id(), entry.translation())
         .comment(
            (String)Optionull.mapOrDefault(getter.get(Comment.class), Comment::value, ""),
            (String)Optionull.mapOrDefault(getter.get(Comment.class), Comment::translation, "")
         )
         .options(Option.gatherOptions(getter, type));
      return builder.build();
   }

   public boolean inRange(double value) {
      ConfigOption.Range range = this.getOption(Option.RANGE);
      return value >= range.min() && value <= range.max();
   }

   public boolean hasOption(Option<?, ?> option) {
      return this.options.containsKey(option);
   }

   public <T extends Annotation, D> D getOption(Option<T, D> option) {
      return (D)this.options.get(option);
   }

   public <T extends Annotation, D> D getOrDefaultOption(Option<T, D> option, D defaultValue) {
      return this.hasOption(option) ? this.getOption(option) : defaultValue;
   }

   public static class Builder {
      private TranslatableValue title = TranslatableValue.EMPTY;
      private TranslatableValue comment = TranslatableValue.EMPTY;
      private Map<Option<?, ?>, Object> options = new HashMap<>();

      public EntryData.Builder translation(String value, String translation) {
         this.title = new TranslatableValue(value, translation);
         return this;
      }

      public EntryData.Builder comment(String value, String translation) {
         this.comment = new TranslatableValue(value, translation);
         return this;
      }

      public EntryData.Builder options(Map<Option<?, ?>, Object> options) {
         this.options = options;
         return this;
      }

      public EntryData build() {
         return new EntryData(this.title, this.comment, this.options);
      }
   }
}

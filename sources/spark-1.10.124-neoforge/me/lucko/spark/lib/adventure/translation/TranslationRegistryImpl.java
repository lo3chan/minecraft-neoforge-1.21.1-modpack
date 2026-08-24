package me.lucko.spark.lib.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.internal.Internals;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TranslationRegistryImpl implements Examinable, TranslationRegistry {
   private final Key name;
   private final Map<String, TranslationRegistryImpl.Translation> translations = new ConcurrentHashMap<>();
   private Locale defaultLocale = Locale.US;

   TranslationRegistryImpl(final Key name) {
      this.name = name;
   }

   @Override
   public void register(@NotNull final String key, @NotNull final Locale locale, @NotNull final MessageFormat format) {
      this.translations.computeIfAbsent(key, x$0 -> new TranslationRegistryImpl.Translation(x$0)).register(locale, format);
   }

   @Override
   public void unregister(@NotNull final String key) {
      this.translations.remove(key);
   }

   @NotNull
   @Override
   public Key name() {
      return this.name;
   }

   @Override
   public boolean contains(@NotNull final String key) {
      return this.translations.containsKey(key);
   }

   @NotNull
   @Override
   public TriState hasAnyTranslations() {
      return !this.translations.isEmpty() ? TriState.TRUE : TriState.FALSE;
   }

   @Nullable
   @Override
   public MessageFormat translate(@NotNull final String key, @NotNull final Locale locale) {
      TranslationRegistryImpl.Translation translation = this.translations.get(key);
      return translation == null ? null : translation.translate(locale);
   }

   @Override
   public void defaultLocale(@NotNull final Locale defaultLocale) {
      this.defaultLocale = Objects.requireNonNull(defaultLocale, "defaultLocale");
   }

   @NotNull
   @Override
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("translations", this.translations));
   }

   @Override
   public boolean equals(final Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof TranslationRegistryImpl)) {
         return false;
      } else {
         TranslationRegistryImpl that = (TranslationRegistryImpl)other;
         return this.name.equals(that.name) && this.translations.equals(that.translations) && this.defaultLocale.equals(that.defaultLocale);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name, this.translations, this.defaultLocale);
   }

   @Override
   public String toString() {
      return Internals.toString(this);
   }

   final class Translation implements Examinable {
      private final String key;
      private final Map<Locale, MessageFormat> formats;

      Translation(@NotNull final String key) {
         this.key = Objects.requireNonNull(key, "translation key");
         this.formats = new ConcurrentHashMap<>();
      }

      void register(@NotNull final Locale locale, @NotNull final MessageFormat format) {
         if (this.formats.putIfAbsent(Objects.requireNonNull(locale, "locale"), Objects.requireNonNull(format, "message format")) != null) {
            throw new IllegalArgumentException(String.format("Translation already exists: %s for %s", this.key, locale));
         }
      }

      @Nullable
      MessageFormat translate(@NotNull final Locale locale) {
         MessageFormat format = this.formats.get(Objects.requireNonNull(locale, "locale"));
         if (format == null) {
            format = this.formats.get(new Locale(locale.getLanguage()));
            if (format == null) {
               format = this.formats.get(TranslationRegistryImpl.this.defaultLocale);
               if (format == null) {
                  format = this.formats.get(TranslationLocales.global());
               }
            }
         }

         return format;
      }

      @NotNull
      @Override
      public Stream<? extends ExaminableProperty> examinableProperties() {
         return Stream.of(ExaminableProperty.of("key", this.key), ExaminableProperty.of("formats", this.formats));
      }

      @Override
      public boolean equals(final Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof TranslationRegistryImpl.Translation)) {
            return false;
         } else {
            TranslationRegistryImpl.Translation that = (TranslationRegistryImpl.Translation)other;
            return this.key.equals(that.key) && this.formats.equals(that.formats);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.key, this.formats);
      }

      @Override
      public String toString() {
         return Internals.toString(this);
      }
   }
}

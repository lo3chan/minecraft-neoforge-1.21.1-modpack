package com.mrcrayfish.configured.impl.neoforge;

import com.mrcrayfish.configured.api.IConfigValue;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.Range;
import net.neoforged.neoforge.common.ModConfigSpec.RestartType;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import org.jetbrains.annotations.Nullable;

public class NeoForgeValue<T> implements IConfigValue<T> {
   public final ConfigValue<T> configValue;
   public final ValueSpec valueSpec;
   protected final T initialValue;
   protected T value;
   protected Component validationHint;

   public NeoForgeValue(ConfigValue<T> configValue, ValueSpec valueSpec) {
      this.configValue = configValue;
      this.valueSpec = valueSpec;
      this.initialValue = (T)configValue.get();
      this.set((T)configValue.get());
   }

   @Override
   public T get() {
      return this.value;
   }

   @Override
   public void set(T value) {
      this.value = value;
   }

   @Override
   public boolean isDefault() {
      return Objects.equals(this.get(), this.valueSpec.getDefault());
   }

   @Override
   public boolean isChanged() {
      return !Objects.equals(this.get(), this.initialValue);
   }

   @Override
   public void restore() {
      this.set(this.getDefault());
   }

   @Override
   public T getDefault() {
      return (T)this.valueSpec.getDefault();
   }

   @Override
   public boolean isValid(T value) {
      return this.valueSpec.test(value);
   }

   @Nullable
   @Override
   public Component getComment() {
      String rawComment = this.valueSpec.getComment();
      String key = this.getTranslationKey() + ".tooltip";
      if (!I18n.exists(key)) {
         return rawComment != null ? Component.literal(rawComment) : null;
      } else {
         MutableComponent comment = Component.translatable(key);
         if (rawComment != null) {
            int rangeIndex = rawComment.indexOf("Range: ");
            int allowedValIndex = rawComment.indexOf("Allowed Values: ");
            if (rangeIndex >= 0 || allowedValIndex >= 0) {
               comment.append(Component.literal(rawComment.substring(Math.max(rangeIndex, allowedValIndex) - 1)));
            }
         }

         return comment;
      }
   }

   @Override
   public String getTranslationKey() {
      return this.valueSpec.getTranslationKey();
   }

   @Nullable
   @Override
   public Component getValidationHint() {
      if (this.validationHint == null) {
         Range<?> range = this.valueSpec.getRange();
         if (range != null) {
            this.validationHint = Component.translatable("configured.validator.range_hint", new Object[]{range.getMin().toString(), range.getMax().toString()});
         }
      }

      return this.validationHint;
   }

   @Override
   public String getName() {
      return lastValue(this.configValue.getPath(), "");
   }

   @Override
   public void cleanCache() {
      this.configValue.clearCache();
   }

   @Override
   public boolean requiresWorldRestart() {
      return this.valueSpec.restartType() == RestartType.WORLD;
   }

   @Override
   public boolean requiresGameRestart() {
      return this.valueSpec.restartType() == RestartType.GAME;
   }

   public static <V> V lastValue(List<V> list, V defaultValue) {
      return !list.isEmpty() ? list.get(list.size() - 1) : defaultValue;
   }
}

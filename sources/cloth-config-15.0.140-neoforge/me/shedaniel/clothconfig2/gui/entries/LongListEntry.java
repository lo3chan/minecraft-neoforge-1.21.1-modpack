package me.shedaniel.clothconfig2.gui.entries;

import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;

@OnlyIn(Dist.CLIENT)
public class LongListEntry extends AbstractNumberListEntry<Long> {
   @Deprecated
   @Internal
   public LongListEntry(Component fieldName, Long value, Component resetButtonKey, Supplier<Long> defaultValue, Consumer<Long> saveConsumer) {
      super(fieldName, value, resetButtonKey, defaultValue);
      this.saveCallback = saveConsumer;
   }

   @Deprecated
   @Internal
   public LongListEntry(
      Component fieldName,
      Long value,
      Component resetButtonKey,
      Supplier<Long> defaultValue,
      Consumer<Long> saveConsumer,
      Supplier<Optional<Component[]>> tooltipSupplier
   ) {
      this(fieldName, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
   }

   @Deprecated
   @Internal
   public LongListEntry(
      Component fieldName,
      Long value,
      Component resetButtonKey,
      Supplier<Long> defaultValue,
      Consumer<Long> saveConsumer,
      Supplier<Optional<Component[]>> tooltipSupplier,
      boolean requiresRestart
   ) {
      super(fieldName, value, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
      this.saveCallback = saveConsumer;
   }

   @Override
   protected Entry<Long, Long> getDefaultRange() {
      return new SimpleEntry<>(-9223372036854775807L, 9223372036854775807L);
   }

   public LongListEntry setMinimum(long minimum) {
      this.minimum = minimum;
      return this;
   }

   public LongListEntry setMaximum(long maximum) {
      this.maximum = maximum;
      return this;
   }

   public Long getValue() {
      try {
         return Long.valueOf(this.textFieldWidget.getValue());
      } catch (Exception var2) {
         return 0L;
      }
   }

   @Override
   public Optional<Component> getError() {
      try {
         long i = Long.parseLong(this.textFieldWidget.getValue());
         if (i > this.maximum) {
            return Optional.of(Component.translatable("text.cloth-config.error.too_large", new Object[]{this.maximum}));
         }

         if (i < this.minimum) {
            return Optional.of(Component.translatable("text.cloth-config.error.too_small", new Object[]{this.minimum}));
         }
      } catch (NumberFormatException var3) {
         return Optional.of(Component.translatable("text.cloth-config.error.not_valid_number_long"));
      }

      return super.getError();
   }
}

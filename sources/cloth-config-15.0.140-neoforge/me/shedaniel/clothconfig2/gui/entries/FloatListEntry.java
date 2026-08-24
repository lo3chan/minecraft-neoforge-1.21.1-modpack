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
public class FloatListEntry extends AbstractNumberListEntry<Float> {
   @Deprecated
   @Internal
   public FloatListEntry(Component fieldName, Float value, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer) {
      super(fieldName, value, resetButtonKey, defaultValue);
      this.saveCallback = saveConsumer;
   }

   @Deprecated
   @Internal
   public FloatListEntry(
      Component fieldName,
      Float value,
      Component resetButtonKey,
      Supplier<Float> defaultValue,
      Consumer<Float> saveConsumer,
      Supplier<Optional<Component[]>> tooltipSupplier
   ) {
      this(fieldName, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
   }

   @Deprecated
   @Internal
   public FloatListEntry(
      Component fieldName,
      Float value,
      Component resetButtonKey,
      Supplier<Float> defaultValue,
      Consumer<Float> saveConsumer,
      Supplier<Optional<Component[]>> tooltipSupplier,
      boolean requiresRestart
   ) {
      super(fieldName, value, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
      this.saveCallback = saveConsumer;
   }

   @Override
   protected Entry<Float, Float> getDefaultRange() {
      return new SimpleEntry<>(-3.4028235E38F, 3.4028235E38F);
   }

   public FloatListEntry setMinimum(float minimum) {
      this.minimum = minimum;
      return this;
   }

   public FloatListEntry setMaximum(float maximum) {
      this.maximum = maximum;
      return this;
   }

   public Float getValue() {
      try {
         return Float.valueOf(this.textFieldWidget.getValue());
      } catch (Exception var2) {
         return 0.0F;
      }
   }

   @Override
   public Optional<Component> getError() {
      try {
         float i = Float.parseFloat(this.textFieldWidget.getValue());
         if (i > this.maximum) {
            return Optional.of(Component.translatable("text.cloth-config.error.too_large", new Object[]{this.maximum}));
         }

         if (i < this.minimum) {
            return Optional.of(Component.translatable("text.cloth-config.error.too_small", new Object[]{this.minimum}));
         }
      } catch (NumberFormatException var2) {
         return Optional.of(Component.translatable("text.cloth-config.error.not_valid_number_float"));
      }

      return super.getError();
   }
}

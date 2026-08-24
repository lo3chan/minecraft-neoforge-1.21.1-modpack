package snownee.jade.impl.config.entry;

import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import snownee.jade.gui.config.OptionsList;
import snownee.jade.gui.config.value.InputOptionValue;
import snownee.jade.gui.config.value.OptionValue;

public class IntConfigEntry extends ConfigEntry<Integer> {
   private boolean slider;
   private int min;
   private int max;

   public IntConfigEntry(ResourceLocation id, int defaultValue, int min, int max, boolean slider) {
      super(id, defaultValue);
      this.slider = slider;
      this.min = min;
      this.max = max;
   }

   @Override
   public boolean isValidValue(Object value) {
      return value instanceof Number && ((Number)value).intValue() >= this.min && ((Number)value).intValue() <= this.max;
   }

   @Override
   public void setValue(Object value) {
      super.setValue(((Number)value).intValue());
   }

   @Override
   public OptionValue<?> createUI(OptionsList options, String optionName, BiConsumer<ResourceLocation, Object> setter) {
      return this.slider
         ? options.slider(
            optionName, () -> (float)this.getValue().intValue(), f -> setter.accept(this.id, (int)f.floatValue()), this.min, this.max, f -> Math.round(f)
         )
         : options.input(
            optionName,
            this::getValue,
            i -> setter.accept(this.id, Mth.clamp(i, this.min, this.max)),
            InputOptionValue.INTEGER.and($ -> this.isValidValue(Integer.valueOf($)))
         );
   }
}

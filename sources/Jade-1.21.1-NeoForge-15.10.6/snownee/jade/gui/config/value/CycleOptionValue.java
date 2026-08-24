package snownee.jade.gui.config.value;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.CycleButton.Builder;

public class CycleOptionValue<T> extends OptionValue<T> {
   private final CycleButton<T> button;

   public CycleOptionValue(String optionName, Builder<T> cycleBtn, Supplier<T> getter, Consumer<T> setter) {
      super(optionName, getter, setter);
      this.button = cycleBtn.displayOnlyValue().withInitialValue(this.value).create(0, 0, 100, 20, this.getTitle(), (btn, v) -> {
         this.value = (T)v;
         this.save();
      });
      this.updateValue();
      this.addWidget(this.button, 0);
   }

   @Override
   public void setValue(T value) {
      this.button.onValueChange.onValueChange(this.button, value);
      this.updateValue();
   }

   @Override
   public void updateValue() {
      this.button.setValue(this.value = this.getter.get());
   }
}

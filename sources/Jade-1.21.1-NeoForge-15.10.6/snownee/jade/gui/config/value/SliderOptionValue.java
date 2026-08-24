package snownee.jade.gui.config.value;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public class SliderOptionValue extends OptionValue<Float> {
   private final SliderOptionValue.Slider slider;
   private float min;
   private float max;
   private FloatUnaryOperator aligner;

   public SliderOptionValue(String optionName, Supplier<Float> getter, Consumer<Float> setter, float min, float max, FloatUnaryOperator aligner) {
      super(optionName, getter, setter);
      this.value = getter.get();
      this.min = min;
      this.max = max;
      this.aligner = aligner;
      this.slider = new SliderOptionValue.Slider(this, 0, 0, 100, 20, this.getTitle());
      this.updateValue();
      this.addWidget(this.slider, 0);
   }

   public void setValue(Float value) {
      this.slider.setValue(value, true);
   }

   @Override
   public void updateValue() {
      this.slider.setValue(this.value = this.getter.get(), false);
   }

   public static class Slider extends AbstractSliderButton {
      private static final DecimalFormat fmt = new DecimalFormat("##.##");
      private final SliderOptionValue parent;

      public Slider(SliderOptionValue parent, int x, int y, int width, int height, Component message) {
         super(x, y, width, height, message, fromScaled(parent.value, parent.min, parent.max));
         this.parent = parent;
         this.updateMessage();
      }

      public static double fromScaled(float f, float min, float max) {
         return Mth.clamp((f - min) / (max - min), 0.0F, 1.0F);
      }

      public float toScaled() {
         float f = this.parent.aligner.apply(this.parent.min + (this.parent.max - this.parent.min) * (float)this.value);
         String s = fmt.format(f);

         try {
            return fmt.parse(s).floatValue();
         } catch (ParseException var4) {
            return f;
         }
      }

      protected void updateMessage() {
         this.setMessage(Component.literal(fmt.format(this.toScaled())));
      }

      protected void applyValue() {
         float scaled = this.toScaled();
         if (this.parent.value != scaled) {
            this.parent.value = scaled;
            this.parent.save();
         }
      }

      private void setValue(float value, boolean applyValue) {
         if (value != this.toScaled()) {
            this.value = fromScaled(value, this.parent.min, this.parent.max);
            if (applyValue) {
               this.applyValue();
            }
         }

         this.updateMessage();
      }

      protected MutableComponent createNarrationMessage() {
         return CommonComponents.joinForNarration(new Component[]{this.parent.getTitle(), super.createNarrationMessage()});
      }
   }
}
